(ns nebulosity.scrolls
  (:require [seesaw.core :as s]
            [seesaw.table :as t]

)
(:use [clojure.repl]             [seesaw.swingx])
)


(def fr (s/frame))

(defn hl-predicate [renderer adapter]
  (> (.getValueAt adapter (.row adapter) 0 ) 30)
)

(def tm (t/table-model :columns ["a" "v" ] :rows [[1 2] [ 2 3]]))
(def tabl (table-x :model tm :show-grid? true :highlighters [((hl-color :foreground :red) hl-predicate)]))


(s/config! fr :content (s/scrollable tabl))

(-> fr s/pack! s/show!)


(time (dotimes [i 10000]
        (t/insert-at! tm i  [i (* 2 i)]))) 

(t/value-at tm 1)

;; det går att lägga metadata varsomhelst

;;går det att påverka layouten i tabellen?

(use 'seesaw.cells)

(ancestors javax.swing.JList)
(descendants javax.swing.JList)

(count "(shuffle (range 10))")

(shuffle (range 10))

(use 'clojure.reflect)


(.getCellRenderer tabl 0 0)
(s/config tabl)

(s/config! fr :title "tablex")
