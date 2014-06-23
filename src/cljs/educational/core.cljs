(ns educational.core
  "a web application rendering the datastructures it fetches dynamically
from the webserver

TODO: add support for prefetching next questions (whatever outcome the answer is)
 - this make it possible to show next task at once, and fetching the new possible
tasks in the background.
TODO: expand the above prefetch to as many tasks as nescessary given the bandwidth
TODO: make it possible to change state :user
"
  (:require [cljs.reader :as reader]
            [goog.events :as events]
            [goog.dom :as gdom]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [educational.timbre-like :as timbre-like :include-macros true])
  (:import [goog.net XhrIo]
           goog.net.EventType ;; fully qualified because namespace clash
           [goog.events EventType]))

(enable-console-print!)

(def ^:private meths
  {:get "GET"
   :put "PUT"
   :post "POST"
   :delete "DELETE"})

(defn edn-xhr 
  "communicate with server through closure ajax"
  [{:keys [method url data on-complete]}]
  (let [xhr (XhrIo.)]
    (events/listen xhr goog.net.EventType.COMPLETE
                   (fn [e]
                     (on-complete (reader/read-string (.getResponseText xhr)))))
    (. xhr
       (send url (meths method) (when data (pr-str data))
             #js {"Content-Type" "application/edn"}))))

(def app-state
  (atom {:query nil
         :user "Linus"}))

(defn say-what [app id] 
  (let [url (str "/next/" (:user @app) "/" id)]
    (edn-xhr
     {:method :get
      :url url
      :on-complete #(om/transact! app :query (fn [_] %))})))

(defn set-mouse-over 
  "sets a state of one of the answers to be rendered as special"
  [app id state]
  (find-current-elements)
  (om/transact! app 
                (fn [app] 
                  (update-in 
                   app 
                   [:query :task/answer]
                   #(reduce 
                     (fn [result new] 
                       (conj result 
                             (if (= id new) 
                               (assoc new :mouseover state) 
                               new))) 
                     #{} %)))))

(defn
  task-view 
  "idempotent function always returning a React handled 'DOM' of the datastructure in
the app variable

owner     is the backing React component

must return an om component - something that implements om/IRender
"
[app owner]
  (reify 
    om/IWillMount
    (will-mount [_]
      (edn-xhr 
       {:method :get 
        :url (str "/taskforuser/" (:user app)) 
        :on-complete #(om/transact! app :query (fn [_] %))}))
    om/IRender
    (render [_]
      (dom/div #js {:id "task" }
               (dom/h2 nil (get-in app [:query :task/query]))
               (apply dom/ul nil                     
                      (map (fn [answer]
                             (dom/li #js {:className "answer-button"
                                          :onClick #(say-what app (:db/id answer))
                                          :onMouseOver #(set-mouse-over app answer true)
                                          :onMouseOut #(set-mouse-over app answer false)} 
                                     (str (:answer/text answer))))
                           (get-in app [:query :task/answer])))))
    (comment
      om/IDidUpdate
      (did-update [this prev-props prev-state]
                  (println "in update!!!!")))))

;;establish an Om rendering loop on a specific element of the DOM
(om/root task-view app-state
         {:target (gdom/getElement "task")})


(defn user-view [app owner]
  (reify 
    om/IRender
    (render [_]
      (dom/div #js {:id "user"}
               (str "user: " (:user app))))))

(om/root user-view app-state
         {:target (gdom/getElement "user")})

(defn ^:export  
  changeuser
"can be reached from javascript console,
educational.core.changeuser('BobbyLjungren')

breaks everything..."
[username]
  (swap! app-state assoc :user username))
