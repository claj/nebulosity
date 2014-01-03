(ns nebulosity.my-own-tree
  "Swing trees seem to be very hard to update
they aren't that pretty either

can I make my own trees, better?

idea:

root <click>
    some_children
    some_other_children"
(:use [seesaw core]))

;;todo: click handling
;;todo: nice coloring
;;todo: nice rendering
;;todo: maybe a stilized directory?
;;can we show pictures in labels? yup http://docs.oracle.com/javase/tutorial/uiswing/components/label.html
;;add buttons etc? yup///

(def fr (frame :title "freetree"))

(-> fr pack! show!)

(config! fr :content "some content")

(defn display [content]
  (config! fr :content content))

(config! lbl :background :pink :foreground "#000")
(config! lbl :font "ARIAL-BOLD-21")

(defn set-text-size [thing size]
  (config! thing :font (str "ARIAL-BOLD-" size)))

(set-size 40)

(def vert (vertical-panel :items [(label :id :sel :text "sel") (label :id :salt :text "   salt") (label :id :sylt :text "    sylt")]))

(display vert)

(config! (select fr [:#sel]) :background "#99F")
(config! (select fr [:#salt]) :visible? true)
(config! (select fr [:#salt]) :text "   salt")
(config! (select fr [:#sylt]) :text "   sylt")
(config! (select fr [:#sylt]) :background "#F99")


