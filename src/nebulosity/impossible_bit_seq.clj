(ns nebulosity.impossible-bit-seq
"I have a really impolite bit-seq to parse, turns out I have to... put it in a BigInteger

My evil seq is in bit-first order (lowest bit in numbers, though)"
  (:use [bytebuffer.buff]) (:require [clojure.string]))

(defn bit-seq [bytearr]
  (let [bitnum (BigInteger. bytearr)]
    {:bitnum bitnum :index (* (count bytearr) 8)}))

(defn conditional-read 
"supply me with the magical {:bitnum :index :current-state}-map and a [:sequence :of :bitflags]
and the function reads them if the bit at index is 1
grammar: {0 | 1 <bitflags>}"
[{:keys [bitnum current-state index] :or {index 0 current-state {}}} bit-flags]
  (if (.testBit bitnum index)
    (loop [state current-state 
           flags bit-flags
           didx (dec index)]
      (if (empty? flags)
        {:current-state (merge current-state state) :index didx :bitnum bitnum}
        (recur (assoc state (first flags) (.testBit bitnum didx)) (rest flags) (dec didx))))
    {:current-state (merge current-state (zipmap bit-flags (repeat nil))) :index (dec index) :bitnum bitnum}))


(defn read-bit [{:keys [bitnum current-state index]} key]
  {:current-state (assoc current-state key (.testBit bitnum index)) :index (dec index) :bitnum bitnum})


(defn 
  read-num 
  [{:keys [bitnum current-state index] 
    :or {index 0 current-state {}}} key numlen]
  (loop [val 0 offseq (range (- index numlen) index) pow 0]
    (if (empty? offseq)    
      ;;returnera state
      (hash-map :current-state (assoc current-state key val) 
                :index (- index numlen) 
                :bitnum bitnum)
      (recur 
       (+ val (bit-shift-left 
               (if (.testBit bitnum (first offseq))
                 1 
                 0) 
               pow))
       (rest offseq)
       (inc pow)))))


(defn to-keyword [s]
  (keyword (clojure.string/replace (clojure.string/trim (clojure.string/lower-case (second (re-find #"<?\s([\w|\s|\d]+):*" s)))) #"[\s.]+" "-")))

(comment (to-key-word "< IMPORTANT grammar term1 : bit >"))

(comment (-> (bit-seq (byte-array [(byte 0x11) (byte 0x00)]))
             (read-num :typen 4)
             (read-num :length 7)
             (read-bit :annika)
             (read-bit :pippi)))

;;in which direction should we really read?

(comment (BigInteger. (byte-array [ (byte 0) (byte 2)])))

