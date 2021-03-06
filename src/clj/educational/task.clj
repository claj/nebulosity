(ns educational.task
  "a task description
TODO: vectors and students as refs
TODO: transient vector operations?
TODO: connect the vector things to persistence layer"
  (:use [clojure.data.generators :only [reservoir-sample]]
        [incanter.stats :only [cosine-similarity]])
  (:require [schema.core :as schema]))

(def key-size 12)
(def vector-size 1000)
(def reservoir (apply vector-of :long (range vector-size)))

(defn generate-key 
  "generate a random key"
  []
  (reservoir-sample key-size reservoir))

(defn empty-vector 
  "generate a double vector of vector-size length, all zeroes"
  []
  (apply vector-of :double (repeat vector-size (double 0))))

(defn generate-task 
  "creates a task, 
with its huge probability matrixes, 
key and question/answer"
  [question answer]
  {:questions question
   :answer answer
   :key (generate-key)
   :student-accomplish (empty-vector)
   :student-fail (empty-vector)
   :unsuccessful-next-tasks (empty-vector)
   :successful-next-tasks (empty-vector)
   :unsuccessful-previous-tasks (empty-vector)
   :successful-previous-tasks (empty-vector)})

(defn generate-student
  "creates a new student with name, the random key
vector for failed tasks, vector for successful tasks
goal-vector for the student"
  [name goal-vector]
  {:name name
   :key (generate-key)
   :failed-tasks (generate-vector)
   :successful-tasks (generate-vector)
   :goal-vector (generate-vector)})

(defn add-one [vector pos amount]
  (assoc vector pos (+ (get vector pos) amount)))

(defn add-key 
  [vector key amount]  
  {:pre [(vector? vector) (vector? key) (= (count key) key-size) (number? amount)]}
  (reduce #(add-one %1 %2 amount) vector key))

;;(add-key (generate-vector) (generate-key) 10)

(defn ^String read-task [task]
  (:question task))

(defn correct-answer? [task answer]
  (= (:answer task) answer))

(defn write-back 
  "when we know the result of the current task
we can write it back to the previous task"
  [previous-task current-task student correct?]
  (add-key 
   (get previous-task 
        (if correct? 
          :successful-next-task 
          :unsuccessful-next-task))
   (:key current-task)))

(defn write-forward
  "writes the result of the previous task to the current task"
  [previous-task current-task student previous-correct?]
  (add-key 
   (get current-task 
        (if correct? 
          :successful-previous-task 
          :unsuccessful-previous-task)) 
   (:key previous-task)))


(defn get-next-task 
  "given a student state, find and return a new suitable task"
  [student-state]

) 


