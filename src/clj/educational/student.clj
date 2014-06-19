(ns educational.student
  (:require [datomic.api :as d :])

"model the student")

(def example-student (ref {:current-task nil :previous-task nil :name nil :login nil}))

(defn transform 
  "writes"
[student task success-or-fail]

  
)
;;transformation
;;(transform student task-x :failed)

;;given this...
;;(get-next-task student)


;;the student is a statemachine:
;;"new"
;;
;; add goals

;; get-next task
;; (answer student "123") -> :success :corrent

;; (get-next-task student)


;; but we need to solve it like

;; (transform student task-a :fail)
;; (transform student task-b :fail)
;; (transform student task-c :success)



;;and it's out of the state of student a new task can be selected.

;; maybe we cannot ask for a new task to many times?

;;login logout? what happends then?


;;the transforms actually builds up a matrix.
;;a new student can be found to have similar events as someone else.

;;TODO: define a test that shows that a second student get something similar as the first student that had a successful way through the problem.

;;TODO: define some curated tasks that should really come after each other, and some which definitly not should do such.

;;TODO: think about a way to solve something by slight dithering/randomness.




