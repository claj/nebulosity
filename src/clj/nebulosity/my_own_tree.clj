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
(comment
  (def fr (frame :title "freetree"))

  (-> fr pack! show!)

  (config! fr :content "some content")

  (defn display [content]
    (config! fr :content content))

  (def lbl "abc")

  (config! lbl :background :pink :foreground "#000")
  (config! lbl :font "ARIAL-BOLD-21")

  (defn set-text-size [thing size]
    (config! thing :font (str "ARIAL-BOLD-" size)))

  (set-text-size (select fr [:#hextal]) 40)
  (config! (select fr [:#hextal]) :font (str "MONOSPACED-BOLD-16"))



  (def vert (vertical-panel :items [(label :id :sel :text "sel") (label :id :salt :text "   salt") (label :id :sylt :text "   sylt")]))

  ;;(display vert)

  (config! (select fr [:#sel]) :background "#99F")
  (config! (select fr [:#salt]) :visible? true)
  (config! (select fr [:#salt]) :text "   salt")
  (config! (select fr [:#sylt]) :text "   sylt")
  (config! (select fr [:#sylt]) :background "#F99")

  (add! vert (label :id :kalas :text "   kalas"))

  (listen (select fr [:#sylt]) :mouse-clicked (fn [e] (config! select fr [:#sylt]) :background (rand-nth [:blue :red :yellow :green])))

  (listen (select fr [:#sylt]) :mouse-clicked (fn [e] (config! (select fr [:#sylt]) :background (rand-nth [:red :blue :green :yellow]))))

  (defn hex-dec-label [number id]
    (label :id id :text (Long/toHexString number) :font "MONOSPACED-24")
    )
  (add! vert (hex-dec-label 2526262 :hextal))

  (remove! vert (select fr [:#hextal]))

  (listen (select fr [:#sel]) :mouse-clicked 
          (fn [e] (doseq [child [:#salt :#sel :#kalas]]
                    (config! (select fr [child]) :background (rand-nth [:blue :red])))))

  )
