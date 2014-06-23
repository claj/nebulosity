(ns educational.webifc-test
  (:require [clojure.test :refer :all]
            [educational.webifc :refer [app]]
            [ring.mock.request :refer :all]
            [midje.sweet :refer :all]))

;;(autotest)
;;(use 'midje.repl)

(fact "checks the first page things"
      (:status (app (request :get "/"))) => 200
      (:body (app (request :get "/"))) => truthy     

      ;;look for certain content in first page
      (slurp (:body (app (request :get "/")))) => #"<title>"
      (slurp (:body (app (request :get "/")))) => #"<html>")

(fact "check compiled javascript availiability"
      (:status (app (request :get "/js/main.js"))) => 200
      (:body (app (request :get "/js/main.js"))) => truthy)


(future-fact "check that we can add a user")





