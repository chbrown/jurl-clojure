(ns jurl.core
  "Throughout this module, the empty string and nil are interchangeable.
  Use str or seq wherever a concrete type is required."
  (:require [clojure.string :as str]))

;; URL encoding/decoding

(defn encode
  "URL-encode `string`; other implementations sometimes convert '+' to '%20',
  but this method does not. Uses UTF-8 as the encoding if applicable."
  {:test (fn []
           (assert (= "" (encode nil)))
           (assert (= "" (encode "")))
           (assert (= "a%2Fz" (encode "a/z"))))}
  [string]
  (-> (str string)
      #?(:clj  (java.net.URLEncoder/encode "UTF-8")
         :cljs (js/encodeURIComponent))))

(defn decode
  "URL-decode `string`. Uses UTF-8 as the encoding if applicable."
  [string]
  {:test (fn []
           (assert (= "" (decode nil)))
           (assert (= "" (decode "")))
           (assert (= "a/z" (decode "a%2Fz"))))}
  (-> (str string)
      #?(:clj  (java.net.URLDecoder/decode "UTF-8")
         :cljs (js/decodeURIComponent))))

;; Deserialization

(defn search->seq
  "Convert a query string, like that returned by window.location.search,
  to a seq of key-value tuples.

  | `search` input | result | description |
  |:---------------|:-------|:------------|
  | \"\"           | nil                           | No querystring
  | \"?\"          | ()                            | Empty querystring
  | \"?a=1&b=2\"   | ([\"a\" \"1\"] [\"b\" \"2\"]) | Two normal query parameters"
  {:test (fn []
           (assert (= nil (search->seq "")))
           (assert (= () (search->seq "?")))
           (assert (= (list ["a" "1"] ["b" "2"]) (search->seq "?a=1&b=2"))))}
  ; TODO: define how to handle non-tuple parameters
  ; TODO: consider using URLSearchParams when more browsers support it.
  [search]
  (when-not (empty? search)
    (->> (str/split (subs search 1) #"&")
         ; (str/split "" #"&") returns [""], but we would prefer []
         (remove empty?)
         (map #(map decode (str/split % #"=" 2))))))

(defn- update-conj
  "Add v to the (maybe empty) vector at (get m k)"
  {:test (fn []
           (assert (= {:k [1]} (update-conj {} [:k 1])))
           (assert (= {:k [1 2]} (update-conj {:k [1]} [:k 2]))))}
  [m [k v]]
  ; we don't use update since that doesn't allow a default for missing values
  ; if we didn't mind the ordering we could just use (update m k conj v)
  ; but this way the order of the vector matches the order of the values
  (assoc m k (conj (get m k []) v)))

(defn search->map
  "Run search->seq then converts to a map with string keys and vector values"
  {:test (fn []
           (assert (= {} (search->map "")))
           (assert (= {} (search->map "?")))
           (assert (= {"a" ["1"] "b" ["2"]} (search->map "?a=1&b=2")))
           (assert (= {"a" ["1" "2"]} (search->map "?a=1&a=2"))))}
  [search]
  (reduce update-conj {} (search->seq search)))

;; Serialization

(defn seq->search
  "Serialize a seq of key-value tuples back into a string"
  {:test (fn []
           (assert (= nil (seq->search nil)))
           (assert (= "?" (seq->search ())))
           (assert (= "?a=1&b=2" (seq->search (list ["a" "1"] ["b" "2"]))))
           (assert (= "?a=1&a=2" (seq->search (list ["a" "1"] ["a" "2"])))))}
  [kvs]
  (some->> kvs
           (map #(str/join "=" %))
           (str/join "&")
           (str "?")))

(defn- ungroup
  "Unpack (flatten) the (k, values) pairs in `m` into a seq of (k, value) pairs"
  [m]
  (for [[k vs] m v vs] [k v]))

(defn map->search
  "Serialize a map of name-vector tuples back into a string"
  {:test (fn []
           (assert (= nil (map->search nil)))
           (assert (= "?" (map->search {})))
           (assert (= "?a=1&b=2" (map->search {"a" ["1"] "b" ["2"]})))
           (assert (= "?a=1&a=2" (map->search {"a" ["1" "2"]}))))}
  [m]
  (some->> m ungroup (map #(map encode %)) seq->search))
