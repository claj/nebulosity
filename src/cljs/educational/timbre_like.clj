(ns educational.timbre-like
  "timbre-like spy functionality for clojurescript")         

(defmacro spy [title expr]
  `(let [result# ~expr]
     (println ~title '~expr result#)
     result#))
