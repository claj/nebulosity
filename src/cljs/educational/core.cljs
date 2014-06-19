(ns educational.core
  "from advanced om-tutorial"
  (:require [cljs.reader :as reader]
            [goog.events :as events]
            [goog.dom :as gdom]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:import [goog.net XhrIo]
           goog.net.EventType ;; fully qualified because namespace clash
           [goog.events EventType]))

(enable-console-print!)
(println "hello world")

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
    (println url)
    (edn-xhr
     {:method :get
      :url url
      :on-complete #(om/transact! app :query (fn [_] %))})))

(defn find-current-elements []
  (println (. js/document querySelectorAll ":hover")))

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

(defn conditional-style 
  "renders different styles depending on the mouseover flag, set "
  [mouseover?]
  #js {:backgroundColor (if mouseover? "#ffc" "#fff")
       :borderStyle (if mouseover? "double" "solid")
       :width "40%"})




(defn
  task-view [app owner]
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
                             (println answer)
                             (dom/li #js {:style (conditional-style (:mouseover answer))
                                          :onClick #(say-what app (:db/id answer))
                                          :onMouseOver #(set-mouse-over app answer true)
                                          :onMouseOut #(set-mouse-over app answer false)} 
                                     (str (:answer/text answer))))
                           (get-in app [:query :task/answer])))))))

(om/root task-view app-state
         {:target (gdom/getElement "task")})

