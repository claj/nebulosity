(defproject nebulosity "0.1.0-SNAPSHOT"
  :description "Clojure code surrounded by nebulosity, eventually to reach star state"
  :url "https://github.com/claj/nebulosity"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs"]
  :java-source-paths ["src/jvm"]
  :resource-paths ["multilang" "resources"]
  :test-paths  ["test/clj"]

  :main educational.webifc
;;  :aot :all
  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src/clj" "src/cljs"]
                        :compiler {
                                   :output-to "resources/public/js/main.js"
                                   :output-dir "resources/public/js/out"
                                   :optimizations :none
                                   :source-map true}}]}

  :profiles {:dev {:plugins [[lein-midje "3.1.1"]]}}

  :jvm-opts ^:replace ["-Xmx1g" "-server"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2234"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [org.clojure/core.logic "0.8.7"]
                 [org.clojure/core.match "0.2.1"]
                 [org.clojure/core.contracts "0.0.5"]
                 [org.clojure/core.cache "0.6.3"]
                 [org.clojure/core.unify "0.5.6"]
                 [org.clojure/core.memoize "0.5.6"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [org.clojure/data.priority-map "0.0.5"]
                 [org.clojure/data.xml "0.0.7"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.clojure/data.generators "0.1.2"]
                 [org.clojure/data.json "0.2.5"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.clojure/data.fressian "0.2.0"]
                 [org.clojure/data.finger-tree "0.0.2"]

                 [org.clojure/math.numeric-tower "0.0.4"]

                 [org.clojure/tools.logging "0.3.0"]
                 [org.clojure/tools.trace "0.7.6"]
                 [org.clojure/tools.macro "0.1.2"]

                 [org.clojure/java.data "0.1.1"]
                 [org.clojure/java.jdbc "0.3.3"]

                 [org.clojure/test.generative "0.5.1"]

                 [midje "1.6.3"]

                 [seesaw "1.4.4"]

                 [enlive "1.1.5"]
                 [compojure "1.1.8"]
                 [ring/ring "1.3.0"]
                 [fogus/ring-edn "0.2.0"]
                 [om "0.6.4"]
                 [om-sync "0.1.1"]
                 [ring-mock "0.1.5"]

                 [com.cemerick/pomegranate "0.3.0"]
                 [criterium "0.4.3"]
                 [org.flatland/useful "0.11.2"]

                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [prismatic/schema "0.2.2"]
                 [incanter/incanter-core "1.5.5"]
                 [com.datomic/datomic-free "0.9.4766.16"]
                 [com.taoensso/timbre "3.2.1"]]
  :plugins [[lein-cljsbuild "1.0.2"]])
