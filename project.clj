(defproject jurl "0.2.0-SNAPSHOT"
  :description "Cross-platform URL parsing / manipulation"
  :url "https://github.com/chbrown/jurl-clojure"
  :license {:name "Eclipse Public License"
            :url "https://www.eclipse.org/legal/epl-v10.html"}
  :pom-addition [:developers [:developer
                              [:name "Christopher Brown"]
                              [:email "io@henrian.com"]]]
  :deploy-repositories [["releases" :clojars]]
  :plugins [[lein-cljsbuild "1.1.7"]]
  :cljsbuild {:builds [{:id "production"
                        :source-paths ["src"]
                        :compiler {:output-dir "target"
                                   :output-to "target/main.js"
                                   :optimizations :advanced}}
                       {:id "test"
                        :source-paths ["src" "test"]
                        :compiler {:output-dir "target/test"
                                   :output-to "target/test/main.js"
                                   :main jurl.runner
                                   :optimizations :whitespace}}]}
  :profiles {:dev  {:dependencies [[org.clojure/clojure "1.8.0"]
                                   [org.clojure/clojurescript "1.9.946"]]}
             :test {:doo {:paths {:rhino "lein run -m org.mozilla.javascript.tools.shell.Main"}}
                    :plugins [[lein-doo "0.1.8"]
                              [lein-cloverage "1.0.10"]]}})
