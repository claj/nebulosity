(ns nebuolsity.config_inference
"want to generate configuration given some properties"

 (:refer-clojure :exclude [==])
   (:use clojure.core.logic)
)

(defrel man p)
(fact man 'Bob)
(fact man 'John)
(fact man 'Ricky)

(defrel woman p)
(fact woman 'Mary)
(fact woman 'Martha)
(fact woman 'Lucy)
    
(defrel likes p1 p2)
(fact likes 'Bob 'Mary)
(fact likes 'John 'Martha)
(fact likes 'Ricky 'Lucy)

(defrel fun p)
(fact fun 'Lucy)

(run* [q]
  (fresh [x y]
    (fun y)
    (likes x y)
    (== q [x y]))) ; ([Ricky Lucy])
