(ns nebulosity.the-standard-tree
  (:use [seesaw core tree]))
(comment
  (def treething )

  (def treemodel (simple-tree-model identity :children {:children nil :tag "hello"} ))

  ;;hur fungerade det med renderer i seesaw?

                                        ; Make a model for the directory tree
  (def tree-model
    (simple-tree-model
     #(.isDirectory %)
     (fn [f] (filter #(.isDirectory %) (.listFiles f)))
     (File. ".")))

  (frame :title "File Explorer" :width 500 :height 500 :pack? false :content
         (border-panel :border 5 :hgap 5 :vgap 5
                       :north (label :id :current-dir :text "Location")

                       :center (left-right-split
                                (scrollable (tree :id :tree :model tree-model :renderer render-file-item))
                                (scrollable (listbox :id :list :renderer render-file-item)))

                       :south (label :id :status :text "Ready")))

  (frame :title "File Explorer" :width 500 :height 500 :pack? false :content
         (border-panel :border 5 :hgap 5 :vgap 5
                       :north (label :id :current-dir :text "Location")

                       :center (left-right-split
                                (scrollable (tree :id :tree :model tree-model :renderer render-file-item))
                                (scrollable (listbox :id :list :renderer render-file-item)))

                       :south (label :id :status :text "Ready")))
  )
