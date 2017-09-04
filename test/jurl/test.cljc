(ns jurl.test
  (:require [clojure.test #?(:clj :refer :cljs :refer-macros) [deftest is]]
            [jurl.core :refer [encode decode search->seq search->map seq->search map->search]]))

(deftest test-encode
  (is (= "" (encode nil)))
  (is (= "" (encode "")))
  (is (= "a%2Fz" (encode "a/z"))))

(deftest test-decode
  (is (= "" (decode nil)))
  (is (= "" (decode "")))
  (is (= "a/z" (decode "a%2Fz"))))

(deftest test-search->seq
  (is (= nil (search->seq "")))
  (is (= () (search->seq "?")))
  (is (= (list ["a" "1"] ["b" "2"]) (search->seq "?a=1&b=2"))))

(deftest test-update-conj
  ; update-conj is private
  (is (= {:k [1]} (#'jurl.core/update-conj {} [:k 1])))
  (is (= {:k [1 2]} (#'jurl.core/update-conj {:k [1]} [:k 2]))))

(deftest test-search->map
  (is (= {} (search->map "")))
  (is (= {} (search->map "?")))
  (is (= {"a" ["1"] "b" ["2"]} (search->map "?a=1&b=2")))
  (is (= {"a" ["1" "2"]} (search->map "?a=1&a=2"))))

(deftest test-seq->search
  (is (= nil (seq->search nil)))
  (is (= "?" (seq->search ())))
  (is (= "?a=1&b=2" (seq->search (list ["a" "1"] ["b" "2"]))))
  (is (= "?a=1&a=2" (seq->search (list ["a" "1"] ["a" "2"])))))

(deftest test-map->search
  (is (= nil (map->search nil)))
  (is (= "?" (map->search {})))
  (is (= "?a=1&b=2" (map->search {"a" ["1"] "b" ["2"] "c" []})))
  (is (= "?a=1&a=2" (map->search {"a" ["1" "2"]}))))
