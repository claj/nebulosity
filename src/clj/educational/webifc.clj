(ns educational.webifc
  "web interface
TODO: login, logout
TODO: pre-store potential new tasks
TODO: some layout, so it's not as bad
TODO: some naive ordering of tasks
TODO: some more complicated ordering of tasks
"
  (:require [ring.util.response :refer [file-response]]

            [ring.middleware.edn :refer [wrap-edn-params]]
            [compojure.core :refer [defroutes GET PUT]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [datomic.api :as d :refer [db q entity]]
            [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre :refer [info warn error spy]])
 (:gen-class))

;; example of a task datomic schema (although not realized until we ask for the values
(comment {:query "what is 0+1?"
          :type :fourfield
          :answers #{{:answer/text "1" :answer/correct true}
                     {:answer/text "2" :answer/correct false}
                     {:answer/text "0" :answer/correct false}
                     {:answer/text "10" :answer/correct false}}})

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
                      :db/ident :task/type
                      :db/valueType :db.type/ref
                      :db/cardinality :db.cardinality/one
                      :db/doc "type of task, :tasktype/fourfield and others"
                      :db/isComponent true
                      :db.install/_attribute :db.part/db}

                     {:db/id (d/tempid :db.part/db)
                      :db/ident :task/answer
                      :db/valueType :db.type/ref
                      :db/cardinality :db.cardinality/many
                      :db/doc "points to one or many answers, correct or not"
                      :db/isComponent true
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

                     ;;user

                     {:db/id (d/tempid :db.part/db)
                      :db/ident :user/name
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "name of the user"
                      :db.install/_attribute :db.part/db}

                     {:db/id (d/tempid :db.part/db)
                      :db/ident :user/current-task
                      :db/valueType :db.type/ref
                      :db/cardinality :db.cardinality/one
                      :db/doc "the current task of the user"
                      :db.install/_attribute :db.part/db}

                     {:db/id (d/tempid :db.part/db)
                      :db/ident :user/solved
                      :db/valueType :db.type/ref
                      :db/cardinality :db.cardinality/many
                      :db/doc "already solved tasks"
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
(install-simple-data "for which x is sin(x) = x?" "0" "π" "2⋅π⋅n, n ∈ ℕ" "2x")
(install-simple-data "what is 10*10?" "100" "20" "0" "1000")
(install-simple-data "what is 10*2+2" "22" "220" "1022" "54")
(install-simple-data "sin(0)=" "0" "-1" "1" "not defined")
(install-simple-data "cos(0)=" "1" "0" "-1" "π")

;;so how to make a graph out of this?
(defn create-user [name]
  (d/transact conn [{:db/id (d/tempid :db.part/user)
                     :user/name name}]))

(create-user "Linus")

(defn set-task [username taskid]
  (let [userid (ffirst (d/q '[:find ?id :in $ ?name :where [?id :user/name ?name]] (d/db conn) username))]
    (d/transact conn [{:db/id userid :user/current-task taskid}])))

;;(q '[:find ?text :where [?userid :user/current-task ?tid] [?tid :task/query ?text]] (d/db conn))

(defn username-to-userid [name db]
  (ffirst  (q '[:find ?uid :in $ ?name :where [?uid :user/name ?name]] db name)))


;;(comment (q '[:find ?e ?q  :where [?e :task/query ?q]] (d/db conn))) ;;ok

(defn find-one-task-id 
  "finds a quite random task id"
  [db]
  (ffirst (q '[:find ?e :where [?e :task/query]] db)))

(set-task "Linus" (find-one-task-id (d/db conn)))

(defn really-random-task-id [db]
  (first 
   (rand-nth 
    (vec 
     (d/q '[:find ?id :where [?id :task/query]] db)))))




(defn generate-response 
  "from om-async tut"
  [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/edn"}
   :body (pr-str data)})



;;for rendering on server (without react)
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
  (GET "/" [] (file-response "public/html/index.html" {:root "resources"}))
  (GET "/task" [] (let [db (d/db conn)] 
                    (pr-str 
                     (d/touch 
                      (d/entity db 
                                (find-one-task-id db))))))
  (GET "/next/:user/:answer-id" 
       [user answer-id] 
       (let [db (d/db conn)
             answer-id (Long/parseLong answer-id)
             correct-answer? (spy :info (:answer/corrent (d/entity db answer-id)))
             next-task (really-random-task-id db)]
         (spy :info (set-task user next-task))
         (spy :info (pr-str (d/touch (d/entity db next-task))))))
  (GET "/taskforuser/:user" 
       [user] 
       (spy :info 
            (let [db (d/db conn)
                  user-id (spy :info (username-to-userid (spy :info user) db))
                  current-task-entity (spy :info (:user/current-task (d/entity db user-id)))] 
              (pr-str (d/touch current-task-entity)))))

  (GET "/tasknumber/:task-id" [task-id] (let [tid (Long/parseLong task-id)]
                                          (info "trying to load task number " task-id)
                                          (assert (number? tid))
                                          (tasktemplate 
                                           (d/entity
                                            (d/db conn) (Long/parseLong tid)))))

  (route/files "/" {:root "resources/public"}))



(def app
  (-> routes
      wrap-edn-params))

