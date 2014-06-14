(ns educational.webifc
  "web interface for students etc

TODO: get a new task
TODO: receive answer and update students things depending on result
TODO: login, logout
TODO: receive a task
TODO: ring-mock for testing the web responses
TODO: add some magic link-pressing functionality
"
  (:require [ring.util.response :refer [file-response]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [compojure.core :refer [defroutes GET PUT]]<
            [compojure.route :as route]
            [compojure.handler :as handler]
            [datomic.api :as d]
            [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]))

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

(d/q '[:find ?e ?q  :where [?e :task/query ?q]] (d/db conn)) ;;ok

;; find correct ones:
(d/q '[:find ?e ?text :where 
       [?e :task/answer ?a] 
       [?a :answer/text ?text] 
       [?a :answer/correct true]]
     (d/db conn))

;; find wrong ones:
(d/q '[:find ?e ?text :where 
       [?e :task/answer ?a]
       [?a :answer/text ?text]
       [?a :answer/correct false]] 
     (d/db conn))

(defn find-one-task-id [db]
  (ffirst (d/q '[:find ?e :where [?e :task/query _]] db)))

;;expected result from below:
(comment (def a-task {:query "When is sin(x) = x?"
                      :answers ["x = 0"]
                      :decoys ["x = π" "x=2 ∙ π" "∅"]}))

(defn read-task 
  "result should be"
  [db id]
  (let [query-text (ffirst (d/q '[:find ?text 
                                  :in $ ?id 
                                  :where [?id :task/query ?text]] db id))
        answer (mapv first (d/q '[:find ?answer-text 
                             :in $ ?id 
                             :where 
                             [?id :task/answer ?aid] 
                             [?aid :answer/correct true] 
                             [?aid :answer/text ?answer-text]] 
                           db id))
        decoys (mapv first (d/q '[:find ?answer-text
                                  :in $ ?id
                                  :where
                                  [?id :task/answer ?aid]
                                  [?aid :answer/correct false]
                                  [?aid :answer/text ?answer-text]] db id))]
    (assert (= (count decoys) 3))
    {:query query-text
     :answer answer
     :decoys decoys}))

;;we just want to use the same db for transactional security
(let [db (d/db conn)]
  (read-task db (find-one-task-id db)))


;; ∙ ∅ π √ ∛ ∝ ∞ ∟ ∂
;; ∠ ∡ ∢ ∣ (divides)
;; ∤ (does not divide)
;; ∥ parallel to
;; ∦ not parallel to
;; ∧ logical and
;; ∨ logical or
;; ∩ intersection
;; ∪ union
;; ⊂ ⊃ (sub/super set of)
;; ⊄ ⊅ ⊆ ⊇ ⊈ ⊉
;; ⊏ ⊐ ⊑ ⊒
;; ⊓ ⊔ square cap/cup
;; ⊕, ⊖, ⊗, ⊘, ⊙, ⊚, ⊛, ⊜, ⊝
;; ⊞, ⊟, ⊠, ⊡
;; ∫ ∬ ∭ ∮ ∯ ∰ ∴ ∵ ∶ (ratio) ∷ (proportion)
;; ∿ sine wave
;; ≅ ≈ ≉ ≠ ≡ ≣ ≤ ≥ ≪ ≫ ≬ (between)
;; ⊾ ⋄ ⋅ (dot) ⋆ (star)
;; ⋇ (division times)
;; ⋈ ⋮ ⋯ ⋰ ⋱
;; ⋲ ∂ ∃ ∄ ∁ (complement)
;; UTF-8, non?

(def a-task {:query "When is sin(x) = x?"
             :answers ["x = 0"]
             :decoys ["x = π" "x=2 ∙ π" "∅"]})

(:query a-task)

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

(defn update-class [id params]
  (let [db    (d/db conn)
        title (:class/title params)
        eid   (ffirst
               (d/q '[:find ?class
                      :in $ ?id
                      :where 
                      [?class :class/id ?id]]
                    db id))]
    (d/transact conn [[:db/add eid :class/title title]])
    (generate-response {:status :ok})))

(defn classes []
  (let [db (d/db conn)
        classes
        (vec (map #(d/touch (d/entity db (first %)))
                  (d/q '[:find ?class
                         :where
                         [?class :class/id]]
                       db)))]
    (generate-response classes)))

(defn populator 
  "just make templating in enlive easier, the macros are in the way, really"
  [task]
  (let [{:keys [query answers  decoys]} task
        answer (first answers)
        [decoy1 decoy2 decoy3] decoys]
    (zipmap [:query :answer1 :answer2 :answer3 :answer4] 
            [query answer decoy1 decoy2 decoy3])))

;;(populator a-task)

(html/deftemplate tasktemplate (io/reader "templates/query.html") [task]
  [:#query] (html/content (:query task))
  [:#answer1] (html/content (:answer1 task))
  [:#answer2] (html/content (:answer2 task))
  [:#answer3] (html/content (:answer3 task))
  [:#answer4] (html/content (:answer4 task)))

(defroutes routes
  (GET "/" [] (index))
  (GET "/task" [] (tasktemplate (populator a-task)))
  (GET "/classes" [] (classes))
  (PUT "/class/:id/update" {params :params edn-params :edn-params} (update-class (:id params) edn-params))
  (route/files "/" {:root "resources/public"}))

(def app
  (-> routes
      wrap-edn-params))

(defonce server
  (run-jetty #'app {:port 8080 :join? false}))
