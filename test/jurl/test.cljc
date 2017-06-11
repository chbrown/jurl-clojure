(ns jurl.test
  (:require [clojure.test :as test]
            [jurl.core]))

; jurl.core has inline (meta) tests
(test/run-tests 'jurl.core)
