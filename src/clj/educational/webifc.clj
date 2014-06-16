(ns educational.webifc
  "web interface for students etc
TODO: receive answer and update students things depending on result
TODO: login, logout
TODO: ring-mock for testing the web responses
"
  (:require [ring.util.response :refer [file-response]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [compojure.core :refer [defroutes GET PUT]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [datomic.api :as d]
            [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre :refer [info warn error spy]]
            ))

(def uri "datomic:mem://om_async")
(d/delete-database uri)
(d/create-database uri)

(def conn (d/connect uri))

(def minimal-schema [{:db/id (d/tempid :db.part/db)
                      :db/ident :task/query
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "a query, like 'what is 1+1?'"
                      :db.install/_attribute :db.part/db}

                     {:db/id (d/tempid :db.part/db)
                      :db/ident :task/answer
                      :db/valueType :db.type/ref
                      :db/cardinality :db.cardinality/many
                      :db/doc "points to one or many answers, correct or not"
                      :db.install/_attribute :db.part/db}

                     {:db/id (d/tempid :db.part/db)
                      :db/ident :answer/text
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "an answer, correct or not, '2', '3', 'π' etc"
                      :db.install/_attribute :db.part/db}

                     {:db/id (d/tempid :db.part/db)
                      :db/ident :answer/correct
                      :db/valueType :db.type/boolean
                      :db/cardinality :db.cardinality/one
                      :db/doc "is the answer correct or not?"
                      :db.install/_attribute :db.part/db}
                     ])

(d/transact conn minimal-schema)

(defn install-simple-data 
  "boiler plateau for installing a task with 
one correct answer and three wrong ones

please note that one can give a multi-cardinal attribute as a set of attributes.

Party!"
  [query correct-answer wrong-answer1 wrong-answer2 wrong-answer3]
  (let [query-id (d/tempid :db.part/user) 
        correct-answer-id (d/tempid :db.part/user)
        wrong-answer1-id  (d/tempid :db.part/user)
        wrong-answer2-id  (d/tempid :db.part/user)
        wrong-answer3-id  (d/tempid :db.part/user)]
    (d/transact conn [{:db/id query-id
                       :task/query query}
                      {:db/id query-id
                       :task/answer #{correct-answer-id 
                                      wrong-answer1-id 
                                      wrong-answer2-id 
                                      wrong-answer3-id}}
                      {:db/id correct-answer-id
                       :answer/text correct-answer
                       :answer/correct true}
                      {:db/id wrong-answer1-id
                       :answer/text wrong-answer1
                       :answer/correct false}
                      {:db/id wrong-answer2-id
                       :answer/text wrong-answer2
                       :answer/correct false}
                      {:db/id wrong-answer3-id
                       :answer/text wrong-answer3
                       :answer/correct false}])))

(install-simple-data "what is 1+1?" "2" "√3" "1" "π")
(install-simple-data "when is sin(x) = x" "0" "π" "2⋅π⋅n, n ∈ ℕ" "2x")

(comment (d/q '[:find ?e ?q  :where [?e :task/query ?q]] (d/db conn))) ;;ok

(defn find-one-task-id 
  "finds a quite random task id"
  [db]
  (ffirst (d/q '[:find ?e :where [?e :task/query]] db)))

;; example of a task datomic schema (although not realized until we ask for the values
(comment {:query "what is 0+1?"
          :type :fourfield
          :answers #{{:answer/text "1" :answer/correct true}
                     {:answer/text "2" :answer/correct false}
                     {:answer/text "0" :answer/correct false}
                     {:answer/text "10" :answer/correct false}}})

(defn index
  "from om-async tut"
  []
  (file-response "public/html/index.html" {:root "resources"}))

(defn generate-response 
  "from om-async tut"
  [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/edn"}
   :body (pr-str data)})

(html/deftemplate tasktemplate (io/reader "templates/query.html") [task]
  [:title]  (html/content (str "rendering " (:db/id task)))
  [:#query] (html/content (str (:task/query task)))
  [:#answers :li] (html/clone-for 
                         [answer-map (:task/answer task)]
                         [:a] (html/do->
                               (html/content (:answer/text answer-map))
                               (html/remove-attr :id)
                               (html/set-attr :href (str "/answer/" (:db/id answer-map))))))

(defroutes routes
  (GET "/" [] (index))
  (GET "/answer/:aid" [aid] (str (spy :info (:answer/correct (d/entity (d/db conn) (Long/parseLong aid))))))
  (GET "/realrt" [] (let [db (d/db conn)] 
                      (tasktemplate (d/entity db (find-one-task-id db)))))
  (GET "/tasknumber/:task-id" [task-id] (let [tid (Long/parseLong task-id)]
                                          (info "trying to load task number " task-id)
                                          (assert (number? tid))
                                          (render-task 
                                           (read-task
                                            (d/db conn) (Long/parseLong tid)))))
  (route/files "/" {:root "resources/public"}))

(def app
  (-> routes
      wrap-edn-params))

(defonce server
  (run-jetty #'app {:port 8080 :join? false}))
