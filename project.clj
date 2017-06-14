(defproject jurl "0.1.1"
  :description "Cross-platform URL parsing / manipulation"
  :url "https://github.com/chbrown/jurl-clojure"
  :license {:name "Eclipse Public License"
            :url "https://www.eclipse.org/legal/epl-v10.html"}
  :deploy-repositories [["releases" :clojars]]
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :profiles {:test {:plugins [[lein-cloverage "1.0.9"]]}})
