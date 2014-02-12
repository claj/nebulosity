(ns nebulosity.dialogue-thesis
  (:require [clojure.core.logic.pldb :as pldb]
            [clojure.core.logic :as l]))



(pldb/db-rel parent p)
;;figure 3.5:

(def facts (pldb/db [parent 'Dave 'Kaylen]
                    [parent 'Frank 'Dave]))

;;solve the whole relation in one run-statement first

(pldb/with-db facts
  (l/run* [q] 
          (l/fresh [x y z]
                   (parent x y)
                   (parent y z)
                   (l/== q [x z]))))
;; => ([Frank Kaylen])

;; original. can the pldb cope with this parent symbol as is?

(defn grandparent [x y]
  (l/fresh [z]
           (parent x z)
           (parent z y)))

(pldb/with-db facts
  (l/run* [q] 
          (l/fresh [x y] 
                   (grandparent x y) 
                   (l/== q [x y]))))

;; => Frank Kaylen.


;;figure 3.7
(l/run* [q] 
        (l/fresh [x y z]
               (l/== x 1)
               (l/== y 2)
               (l/project [x y]
                        (l/== z (+ x y))
                        (l/== q {'x x 'y y 'z z}))))

;; the project macro takes any number of existing logical variables
;; and "opens them up" to reveal the data they contain


;; information hiding with pldb
(pldb/db-rel parent x y)
(pldb/db-rel afunc f)
(pldb/db-rel funcname f n)

(defn grandparent [x y]
  (l/fresh [z] (parent x z) (parent z y)))

(defn ancrel [p x y q]
  (fresh [n]
         (funcname p n)
         (l/project [p]
                    (p x y)
                    (== q {'func n 'x x 'y y}))))


(def afunc-facts (pldb/db [grandparent] [parent]))



;; Declaring facts
;; (facts afunc
;; [[grandparent]
;; [parent]])
;; (facts funcname
;; [[grandparent 'grandparent]
;; [parent 'parent]])
;; (facts parent
;; '[[dave kaylen]
;; [frank dave]])
;; ;; Interactive prompt
;; user> (run* [q]
;; (fresh [p x y]
;; (afunc p)
;; (ancrel p x y q)))
;; ({func parent, x dave, y kaylen}
;; {func parent, x frank, y dave}
;; {func grandparent, x frank, y kaylen})


;; fig 4.2 Knights and Knaves:

(pldb/db-rel paths x y)
(pldb/db-rel mind-f x p)

(pldb/db-rel opposite x y)
(defn knightanswer [a b] (l/== a b))
(defn knaveanswer [a b] (opposite a b))

(def kk-facts (pldb/db [opposite 'Left 'Right]
                    [opposite 'Right 'Left]
                    [opposite 'Death 'Freedom]
                    [opposite 'Freedom 'Death]
                    [opposite 'Jack 'Bill]
                    [opposite 'Bill 'Jack]
                    [paths 'Left 'Freedom]
                    [paths 'Right 'Death]
                    [mind-f 'Bill knightanswer]
                    [mind-f 'Jack knaveanswer]))

(pldb/with-db kk-facts 
  (l/run* [q] 
          (l/fresh [x p] 
                 (paths x 'Freedom)
                 (mind-f 'Jack p)
                 (l/project [p] (p x q)))))

;;Right!

;;asking the solution, if i asked the other guy, what would he say?
;;doesn't work with this system

(pldb/with-db kk-facts
  (l/run* [q]
          (l/fresh [x y p p2]
                   (paths x 'Freedom)
                   (mind-f 'Bill p)
                   (mind-f 'Jack p2)
                   (l/project [p p2]
                              (p2 x y)
                              (p y q)))))

;; Right


;; a way to implement the secret function and a lot of ...
;; this looks much like already implemented in pldb


;;type definitions:

;;(t-def heya ["Heya"] ::Greeting)
;; oh-hi ["Oh hi"] ::Greeting
;; visit-museum ["Would you like to visit the museum with me this evening?"] ::Offer
;; love-to ["I'd love to"] ::Acc-Rej
;; jerkface ["No way, jerkface! Museums are for nerds!"] ::Acc-Rej
;; phone-there ["Your phone is over there!"] ::Inform
;; i-know ["I know"] ::Knowledge

(def agent-mood (atom nil))
(defmulti respond (fn [o] (get (meta o) :type)))
(defmethod respond ::Greeting [msg] oh-hi)
(defmethod respond ::Offer [msg] 
  (cond 
   (= @agent-mood :good) love-to
   (= @agent-mood :bad) jerkface))

(defmethod respond ::Inform [msg]
  i-know)

(defn initialize [mood]
  (reset! agent-mood mood))

(fn [q]
  (fresh [n]
         (*agent-name* n)
         (mother n 'Anna)))

;;Yes, Anna is my mother

;;assertion
;;retraction
;;change


;;we want to combine all the components

;; get receiver metadata <--- Message queue
;;  v
;; agent context
;;
;;    is predicate ---> yes --> thought function
;;        |                           |
;;       no                           |
;;        |                           |
;;   [ rules ] <---------------- modifiers
;;
;;
;;   < dispatch fn >    -_ response functions *


;; lovecraft country:

;;example data structure

(def example-Mechanic {:job MechJob
                       :described-events #{}
                       :emotion-thresholds {Stop-Talking #(> % -0.8)
                                            Breakdown #(> % -2.0)
}


                       ;;a set of general descriptors
                       :general-descs {1 {:desc [Horrible Last-Night-Event]
                                          :e-val -0.1}
                                       2 {:desc [Bloody Last-Night-Event]
                                          :e-val -0.1}
                                       3 {:desc [Supernatural Last-Night-Event] :e-val -0.1}}

:event-log {[21 00 00] {:desc [Drove-To Farmhouse] :e-val -0.1}
            [21 1 00] {:desc [Quiet Farmhouse] :e-val -0.1}
            [21 2 00] {:desc [Quiet Cicada] :e-val -0.1}
            [21 5 00] {:desc [Broke Engine] :e-val -0.1}
            [21 7 00] {:desc [Came-From Glowing-Thing Sky] :e-val -0.2}
            [21 8 00] {:desc [Afraid Self] :e-val -0.15}
            [21 9 00] {:desc [Near Farmhouse Bodies] :e-val -0.3}
            [21 10 00] {:desc [Inhuman Bodies] :e-val -0.3}} 
            :emotion-level 0.0})


;;vill alltsa dra ut saker vid ratt tillfallen utan att det blir konstigt

;; kan vi se nagot mer i

;; men 


(require '[clojure.core.logic.fd :as fd])

(pldb/db-rel says who emotion)
(pldb/db-rel beensaid who what)
(pldb/db-rel action who what when)

;; och da kunde man enkelt lagga upp en modell for vad andra antagligen visste om mig.

(def lovecraft-facts (pldb/db [says :mech :wanto "want to go now" 6 ]
                                [says :mech :idont "I... don't... " 1.5]
                                [says :mech :normal "Everything looks perfectly normal to me" 10]
                                [says :mech :greenmen "There where green men" 2]
                                [beensaid :mech :greenmen]))

;;messing up time!
;;would like to follow the thing all through!

(def lovecraft-retract (-> lovecraft-facts (pldb/db-retraction says :mech :normal "Everything looks perfectly normal to me" 10)))


lovecraft-retract
;;hur lagrar vi undan vad vi redan sagt da?
;;man kunde faktiskt l'gga till och dra ifran har efter vad det passade.

(require '[clojure.core.logic.arithmetic :as arithmetics])

(pldb/with-db lovecraft-retract
  (l/run* [q] 
          (l/fresh [kw what emlev]
                   (says :mech kw what emlev)
                   (arithmetics/> emlev 1.6)
                   (l/== q [:mech kw what]))))

(pldb/with-db facts
  (l/run* [q] 
          (l/fresh [x y z]
                   (parent x y)
                   (parent y z)
                   (l/== q [x z]))))

;;then we don't need the dreaded multimethods either!

;; is it possible to do it with floats?, yes through arithmetics
;; or do we just want to have a continous value-range function then? like a parabel or what ever?

;;but we do really want to know if some things already has been said (ie makes sense)

;; we need some set of needed prerequisties
;; as well as some already said things.

;;can one

;;


(def an-agent (atom {:facts (pldb/db [says :hi 0.5 100]
                                     [says :fuckoff -0.1 -10]
                                     [says :trendemouswheatherindeed 0.8 40]
                                     [says :extraordinaryhatyouwear 0.7 40]
                                     [says :timeforfishing 0.7 15]
                                     [says :timeforhunting 0.3 10]
                                     [says :howdoyoudo 0.5 90]
                                     [says :ilovecraft 0.7 30])
                      :emotional 2.0}))



(let [thing (first 
             (sort-by last > 
                      (pldb/with-db (:facts @an-agent) 
                        (l/run* [q] (l/fresh [saywhat emolevel order]
                                             (says saywhat emolevel order)
                                             (arithmetics/> (:emotional @an-agent) emolevel)
                                             (l/== q [saywhat emolevel order]))))))]
  (swap! an-agent #(assoc % :facts (apply pldb/db-retraction (:facts %) says thing) ))
  (first thing))

@an-agent

(swap! an-agent assoc :emotional 0.6)
