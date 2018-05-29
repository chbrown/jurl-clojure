(ns jurl.test
  (:require [clojure.test :refer [deftest are]]
            [jurl.core :refer [encode decode search->seq search->map seq->search map->search]]))

(deftest test-encode
  (are [expected actual] (= expected actual)
       ""      (encode nil)
       ""      (encode "")
       "a%2Fz" (encode "a/z")))

(deftest test-decode
  (are [expected actual] (= expected actual)
       ""    (decode nil)
       ""    (decode "")
       "a/z" (decode "a%2Fz")))

(deftest test-search->seq
  (are [expected actual] (= expected actual)
       nil                        (search->seq "")
       ()                         (search->seq "?")
       (list ["a" "1"] ["b" "2"]) (search->seq "?a=1&b=2")))

(deftest test-update-conj
  ; update-conj is private
  (are [expected actual] (= expected actual)
       {:k [1]}   (#'jurl.core/update-conj {} [:k 1])
       {:k [1 2]} (#'jurl.core/update-conj {:k [1]} [:k 2])))

(deftest test-search->map
  (are [expected actual] (= expected actual)
       {}                    (search->map "")
       {}                    (search->map "?")
       {"a" ["1"] "b" ["2"]} (search->map "?a=1&b=2")
       {"a" ["1" "2"]}       (search->map "?a=1&a=2")))

(deftest test-seq->search
  (are [expected actual] (= expected actual)
       nil        (seq->search nil)
       "?"        (seq->search ())
       "?a=1&b=2" (seq->search (list ["a" "1"] ["b" "2"]))
       "?a=1&a=2" (seq->search (list ["a" "1"] ["a" "2"]))))

(deftest test-map->search
  (are [expected actual] (= expected actual)
       nil        (map->search nil)
       "?"        (map->search {})
       "?a=1&b=2" (map->search {"a" ["1"] "b" ["2"] "c" []})
       "?a=1&a=2" (map->search {"a" ["1" "2"]})))
