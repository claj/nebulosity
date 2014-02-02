(ns nebulosity.rich-message-handler
  "get some incoming data, act on it")

(def king-of-the-hill (atom "Karl-Gerhard"))

(defn change-king [new-king]
  (reset! king-of-the-hill new-king))

(change-king "Magnus Uggla")


(defn new-hill [sym])

;;we need some real data to get this example to be worthwhile



(def hlr (ref {}))
;;

;;what is the most simplified example we can have?

;;let's say a network socket, port 80.





