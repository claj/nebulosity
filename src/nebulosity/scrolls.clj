(ns nebulosity.scrolls
  "nice highlights on tables would be nice"
  (:use [clojure repl pprint]
        [seesaw swingx core table]))

(native!)

(def fr (frame :title "highlights"))
(.setAlwaysOnTop fr true)

;;works!
(defn hl-predicate [renderer adapter]
  (> (.getValueAt adapter (.row adapter) 0 ) 30))

(def menus (menubar :items [(menu :text "File" :items [ (menu-item :text "hello!")])]))


(def tm (table-model :columns ["a" "v" ] :rows [[1 2] [ 2 3]]))
(def tabl (table-x :model tm :show-grid? true :highlighters [((hl-color :foreground :red) hl-predicate)]))

(config! fr :content (scrollable tabl))


(config! fr :menubar menus)

(config! tabl :highlighters [
                             ((hl-color :foreground :green) hl-predicate) 
((hl-color :background "#ffc") 
                                                                           (fn [renderer adapter] (odd? (.getValueAt adapter (.row adapter) 0)) ))])

(config! tabl :highlighters nil)

(-> fr pack! show!)

(time (dotimes [i 100]
        (insert-at! tm i  [i (* 2 i)]))) 

;;(insert-at! tm 0 ["<html><b>FET</b></html>" 2])

(insert-at! tm 0 [2222 33333])

(insert-at! tm 0 ["<html><b>hej</b></html>" "<html>hahaha <i>hihihih</i></html>"])

;; ≈Label label = new JLabel("<html><font color="FF0000">My</font> <font
;; color="00FF00">label</font></html>");



(value-at tm 1)

;; det går att lägga metadata varsomhelst
;;går det att påverka layouten i tabellen?

(use 'seesaw.cells)

(ancestors javax.swing.JList)
(descendants javax.swing.JList)

(count "(shuffle (range 10))")

(shuffle (range 10))

(use 'clojure.reflect)

(.getCellRenderer tabl 0 0)
(config tabl)

(config! fr :title "tablex")
