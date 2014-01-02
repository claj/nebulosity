(ns nebulosity.impossible-bit-seq
"I have a really impolite bit-seq to parse, turns out I have to... put it in a BigInteger")

(defn conditional-read 
"supply me with the magical {:bitnum :index :current-state}-map and a [:sequence :of :bitflags]
and the function reads them if the bit at index is 1

grammar: {0 | 1 <bitflags>}"
[{:keys [bitnum current-state index] :or {index 0 current-state {}}} bit-flags]
  (if (.testBit bitnum index)
    (loop [state current-state 
           flags bit-flags
           iidx (inc index)]
      (if (empty? flags)
        {:current-state state :index iidx :bitnum bitnum}
        (recur (assoc state (first flags) (.testBit bitnum iidx)) (rest flags) (inc iidx))))
    {:current-state (merge current-state (zipmap bit-flags (repeat nil))) :bitnum bitnum :index (inc index)}))

(comment
  (conditional-read {:bitnum (BigInteger. "0")}  [:drama :comedy :gameshow])
  (conditional-read {:bitnum (BigInteger. "7")}  [:drama :comedy :gameshow])
  (conditional-read {:bitnum (BigInteger. "8")}  [:drama :comedy :gameshow]))

(defn definitive-bit-read [{:keys [bitnum current-state index] :or {index 0 current-state {}}} flag]
  {:currrent-state (assoc current-state flag (.testBit bitnum index)) :index (inc index) :bitnum bitnum})

(comment
  (definitive-bit-read {:bitnum (BigInteger. "1") :current-state {:apple 23}} :argus)
  (definitive-bit-read {:bitnum (BigInteger. "0") :current-state {:apple 23}} :argus))
