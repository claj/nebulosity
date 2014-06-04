(defproject nebulosity "0.1.0-SNAPSHOT"
  :description "Clojure code surrounded by nebulosity, eventually to reach star state"
  :url "https://github.com/claj/nebulosity"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src/clj" "src/cljs"]
  :java-source-paths ["src/jvm"]
  :resource-paths ["multilang"]
  :aot :all

  :jvm-opts ^:replace ["-Xmx1g" "-server"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [org.clojure/core.logic "0.8.5"]
                 [org.clojure/core.match "0.2.0"]
                 [org.clojure/core.contracts "0.0.5"]
                 [org.clojure/core.cache "0.6.3"]
                 [org.clojure/core.unify "0.5.5"]
                 [org.clojure/core.memoize "0.5.6"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [org.clojure/data.priority-map "0.0.4"]
                 [org.clojure/data.xml "0.0.7"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.clojure/data.generators "0.1.2"]
                 [org.clojure/data.json "0.2.3"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.clojure/data.fressian "0.2.0"]
                 [org.clojure/data.finger-tree "0.0.1"]

                 [org.clojure/math.numeric-tower "0.0.3"]

                 [org.clojure/tools.logging "0.2.6"]
                 [org.clojure/tools.trace "0.7.6"]
                 [org.clojure/tools.macro "0.1.2"]

                 [org.clojure/java.jmx "0.2.0"]
                 [org.clojure/java.data "0.1.1"]
                 [org.clojure/java.jdbc "0.3.3"]

                 [org.clojure/test.generative "0.5.0"]

                 [midje "1.6.3"]

                 [org.bouncycastle/bcprov-jdk15on "1.50"]
                 [net.cgrand/parsley "0.9.2"]

                 [seesaw "1.4.4"]

                 [enlive "1.1.5"]
                 [compojure "1.1.6"]
                 [ring/ring "1.2.1"]
                 [fogus/ring-edn "0.2.0"]
                 [om "0.5.3"]

                 [bytebuffer "0.2.0"]
                 [nio "1.0.2"]
                 [gloss "0.2.2"]
                 [aleph "0.3.3-SNAPSHOT"]

                 [cc.artifice/clj-ml "0.5.0-SNAPSHOT"]

                 [reduce-fsm "0.1.0"]
                 [zookeeper-clj "0.9.1"]
                 [avout "0.5.3"]

                 [clj-net-pcap "1.6.0"]

                 [com.cemerick/pomegranate "0.2.0"]

                 [criterium "0.4.2"]

                 [org.flatland/useful "0.11.1"]

                 [cc.qbits/nippy-lz4 "0.1.0"]
                 [com.taoensso/nippy "2.6.0-RC1"]

                 [clojurewerkz/meltdown "1.0.0-beta8"]

                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [prismatic/schema "0.2.2"]

                 [com.datomic/datomic-free "0.9.4766.16"]]


  :plugins [[lein-cljsbuild "1.0.2"]]

  :source-paths ["/src/clj" "src/cljs"]
  :resource-paths ["resources"])
