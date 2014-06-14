(ns educational.webifc
  "web interface for students etc

TODO: serve the first page
TODO: get a new task
TODO: receive answer and update students things depending on result
TODO: login, logout
TODO: show a task
TODO: receive a task
TODO: ring-mock for testing the web responses"
  (:require [ring.util.response :refer [file-response]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [compojure.core :refer [defroutes GET PUT]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [datomic.api :as d]
            [net.cgrand.enlive-html :as html]
            [clojure.java.io :as io]))

(def uri "datomic:mem://om_async")
(d/create-database uri)
(def conn (d/connect uri))

(defn index
  []
  (file-response "public/html/index.html" {:root "resources"}))

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

(defn generate-response [data & [status]]
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

(defn populator [task]
  (let [{:keys [query answers  decoys]} task
        answer (first answers)
        [decoy1 decoy2 decoy3] decoys]
    (zipmap [:query :answer1 :answer2 :answer3 :answer4] 
            [query answer decoy1 decoy2 decoy3])))

(populator a-task)

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
