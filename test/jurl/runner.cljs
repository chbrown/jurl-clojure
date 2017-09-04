(ns jurl.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [jurl.test]))

(doo-tests 'jurl.test)
