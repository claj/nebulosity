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

(defn task-view [app owner]
  (reify 
    om/IWillMount
    (will-mount [_]
      (edn-xhr 
       {:method :get 
        :url (str "/taskforuser/" (:user app)) 
        :on-complete #(om/transact! app :query (fn [_] %))}))
    om/IRender
    (render [_]
      (dom/div #js {:id "classes"}
               (dom/h2 nil (get-in app [:query :task/query]))
               (apply dom/ul nil
                      (map (fn [answer]
                             (dom/li #js {:onClick #(say-what app (:db/id answer))} (str (:answer/text answer))))
                           (get-in app [:query :task/answer])))))))

(om/root task-view app-state
  {:target (gdom/getElement "classes")})

