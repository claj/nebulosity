(ns educational.main
  (:require [educational.webifc :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(def port 8080)

(defonce server
  (run-jetty #'app {:port port :join? false}))

(defn -main [& args] (println "webserver already started at " port))
