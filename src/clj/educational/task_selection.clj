(ns educational.task-selection
  "sketches for task-selection"
  (:require [clojure.string :as s]))

;;markov toys

;;assume we have some tasks

(defn keychars 
  " string abc -> [:a :b :c] "
  [string]
  (mapv (comp keyword str) string))

(def tasks "a range of tasks, symbolised by letters" (keychars "abcdefghijklmnopqrstuv"))

;; rounds: assumed to be ended with a failure

(def one-round (keychars "abcdef"))

(def two-round (keychars "abcdghji"))

;;we can deduce
;;
;; :a -> :b -> :c -> :d 
;;
;; as quite a safe road
;;
;; it also looks like
;;
;; :d -> :e -> :f
;;
;; is good
;;
;; and also :d -> :g -> :h -> :j -> :i
;;
;; what would happend if we got also
;;
;; :b -> :a
;;
;; then the ordering are most likely not very important
;;
;; one sparse graph representation would be

{[:a :b] 1
 [:b :a] 1}

(defn twostepper
  [{graph :graph previous :previous} new] 
  {:graph (if previous (merge-with + graph {[previous new] 1}) graph) 
   :previous new})

(assert (= (twostepper {:graph {} :previous :a} :b)) {:graph {[:a :b] 1}, :previous :b})

(defn add-to-sparse-graph 
  "adds sequences in a sparse graph {[to from] common-ness}" 
  ([seq-of-tasks]
     (add-to-sparse-graph {} seq-of-tasks))
  ([graph seq-of-tasks]
     (:graph (reduce twostepper {:graph graph :previous nil} seq-of-tasks))))

(add-to-sparse-graph (keychars "abcdefghjiklmnoababapoqpdae"))

;;;another one (with forward and backward) would be

{:forward {:a {:b 1 :c 3}}
 :backward {:b {:a 1}
            :c {:a 3}}}

(defn fw-bw-graph-reduce-fn 
  "creates :forward and :backward lookup graphs {:a {:b 1}}"
  [{graph :graph previous :previous} new]
  {:graph (if (and previous new)
            (-> graph
                (update-in [:forward previous new] (fnil inc 0))
                (update-in [:backward new previous] (fnil inc 0)))
            graph) 
   :previous new})



(defn fw-bw-graph [seq]
  (:graph (reduce fw-bw-graph-reduce-fn {:graph {:forward {} 
                                                 :backward {}} 
                                         :previous nil} 
                  seq)))

;;how to search in this beast?
;;how to summarize?

(def a-graph (fw-bw-graph [:a :b :c :d :e nil :e :f :fail nil :e :f :a :b :fail nil :a :c :d :fail nil :e :f :fail nil :a :b :c :fail nil :a :b]))

(defn select-most-likely [some-map]
  (key (first (sort-by val > some-map))))

(comment (select-most-likely {:A 1 :B 2}))

a-graph

(get-in a-graph [:forward :c])

(def accelerando (slurp "/home/linus/Documents/accelerando.txt"))

(def word-seq (filter #(not (s/blank? %)) (s/split (s/lower-case (apply str (filter (comp not (set ".,!?\"")) accelerando))) #"\s")))

(def very-boring-hmm (fw-bw-graph word-seq))

very-boring-hmm


(select-most-likely (get-in very-boring-hmm [:forward "the"]))

(defn common-word-chain [])


;;let's say we want to go from :a to :f
;;we would then "simply" try to find a way from :a to :f or f to a in the backward graph
;;somehow maximising the chances



;; how would we know how "good" a certain way would be?
;; it's just to 

;;    c╱
;;   ╱ ╲
;;  ╱
;; a
;;  ╲
;;   ╲b╱
;;     ╲
