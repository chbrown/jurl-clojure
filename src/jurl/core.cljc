(ns jurl.core
  "Throughout this module, the empty string and nil are interchangeable.
  Use str or seq wherever a concrete type is required."
  (:require [clojure.string :as str]))

;; URL encoding/decoding

(defn ^:export encode
  "URL-encode `string`; other implementations sometimes convert '+' to '%20',
  but this method does not. Uses UTF-8 as the encoding if applicable."
  [string]
  {:pre [(or (nil? string) (string? string))]
   :post [(string? %)]}
  (-> (str string)
      #?(:clj  (java.net.URLEncoder/encode "UTF-8")
         :cljs (js/encodeURIComponent))))

(defn ^:export decode
  "URL-decode `string`. Uses UTF-8 as the encoding if applicable."
  [string]
  {:pre  [(or (nil? string) (string? string))]
   :post [(string? %)]}
  (-> (str string)
      #?(:clj  (java.net.URLDecoder/decode "UTF-8")
         :cljs (js/decodeURIComponent))))

;; Deserialization

(defn ^:export search->seq
  "Convert a query string, like that returned by window.location.search,
  to a seq of key-value tuples.

  | `search` input | result | description |
  |:---------------|:-------|:------------|
  | \"\"           | nil                           | No querystring
  | \"?\"          | ()                            | Empty querystring
  | \"?a=1&b=2\"   | ([\"a\" \"1\"] [\"b\" \"2\"]) | Two normal query parameters"
  ; TODO: define how to handle non-tuple parameters
  ; TODO: consider using URLSearchParams when more browsers support it.
  [search]
  {:pre  [(string? search)]
   :post [(every? string? (map first %))
          (every? string? (map second %))]}
  (when-not (empty? search)
    (->> (str/split (subs search 1) #"&")
         ; (str/split "" #"&") returns [""], but we would prefer []
         (remove empty?)
         (map #(map decode (str/split % #"=" 2))))))

(defn- update-conj
  "Add v to the (maybe empty) vector at (get m k)"
  [m [k v]]
  {:pre  [(map? m)]
   :post [(contains? % k)]}
  ; we don't use update since that doesn't allow a default for missing values
  ; if we didn't mind the ordering we could just use (update m k conj v)
  ; but this way the order of the vector matches the order of the values
  (assoc m k (conj (get m k []) v)))

(defn ^:export search->map
  "Run search->seq then convert to a map with string keys and vector values"
  ([search]
   (search->map search nil))
  ([search {re :split}]
   {:pre  [(string? search)]
    :post [(map? %)]}
   (reduce update-conj {} (cond->> (search->seq search)
                            ; when {:split re} is supplied, produce a [k v] pair for each split substring of v
                            re (mapcat (fn [[k v]] (map vector (repeat k) (str/split v re))))))))

;; Serialization

(defn ^:export seq->search
  "Serialize a seq of key-value tuples back into a string"
  [kvs]
  {:pre  [(or (nil? kvs) (coll? kvs))
          (every? string? (map first kvs))
          (every? string? (map second kvs))]
   :post [(or (nil? %) (string? %))]}
  (some->> kvs
           (map #(str/join "=" %))
           (str/join "&")
           (str "?")))

(defn- map-values
  "Construct a new map with all the values of the given map passed through f"
  [f kvs]
  {:pre  [(ifn? f) (map? kvs)]
   :post [(map? %)]}
  (into (empty kvs) (for [[k v] kvs] [k (f v)])))

(defn- ungroup
  "Unpack (flatten) the (k, values) pairs in the map `m` into a seq of (k, value) pairs.
  If `separator` is provided, return one pair for each [k values] pair,
  joining all values into a single string (in a 1-element vector)."
  ([m]
   (ungroup nil m))
  ([separator m]
   {:pre  [(or (nil? separator) (string? separator))
           (map? m)
           (every? string? (keys m))
           (every? string? (apply concat (vals m)))]
    :post [(every? string? (map first %))
           (every? string? (map second %))]}
   (if separator
     (for [[k vs] m]
       [k (str/join separator vs)])
     (for [[k vs] m
           v vs]
       [k v]))))

(defn ^:export map->search
  "Serialize a map of name-vector tuples back into a string
  (prefixed with '?' if non-empty)"
  ([m]
   (map->search m nil))
  ([m {:keys [separator]}]
   {:pre  [(or (nil? m) (map? m))
           (every? string? (keys m))
           (every? string? (apply concat (vals m)))
           (or (nil? separator) (string? separator))]
    :post [(or (nil? %) (string? %))]}
   (some->> m (map-values #(map encode %)) (ungroup separator) seq->search)))
