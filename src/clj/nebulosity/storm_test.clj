(ns nebulosity.storm-test
  (:import [backtype.storm StormSubmitter LocalCluster])
  (:use [backtype.storm clojure config])
  (:gen-class))
;; import backtype.storm.LocalCluster;

;; LocalCluster cluster = new LocalCluster();

;; You can then submit topologies using the submitTopology method on the LocalCluster object. Just like the corresponding method on StormSubmitter, submitTopology takes a name, a topology configuration, and the topology object. You can then kill a topology using the killTopology method which takes the topology name as an argument.

;; To shutdown a local cluster, simple call:

;; cluster.shutdown();

;; Common configurations for local mode

;; You can see a full list of configurations here.

;;     Config.TOPOLOGY_MAX_TASK_PARALLELISM: This config puts a ceiling on the number of threads spawned for a single component. Oftentimes production topologies have a lot of parallelism (hundreds of threads) which places unreasonable load when trying to test the topology in local mode. This config lets you easy control that parallelism.
;;     Config.TOPOLOGY_DEBUG: When this is set to true, Storm will log a message every time a tuple is emitted from any spout or bolt. This is extremely useful for debugging.


;; hur vet jag att min topologi kör?
;;måste jag trigga den från terminal?  

(defspout sentence-spout ["sentect"]
  [conf context collector]
  (let [sentences ["a little brown dog"
                   "the man petted the dog"
                   "four score and seven years ago"
                   "an apple a day keeps the doctor away"]]
    (spout
     (nextTuple []
                (Thread/sleep 100)
                (emit-spout! collector [(rand-nth sentences)]))
     (ack [id]) ;;only for reliable spouts
)))


(defspout sentence-spout-parameterized ["word"] {:params [sentences] :prepare false}
  [collector]
(Thread/sleep 500) 
(emit-spout! collector [(rand-nth sentences)]))

(defbolt split-sentence ["word"] [tuple collector]
  (let [words (.split (.getString tuple 0) " ")]
    (doseq [w words]
      (emit-bolt! collector [w] :anchor tuple))
    (ack! collector tuple)))


(defbolt word-count ["word" "count"] {:prepare true}
  [conf context collector]

  ;;counts är förstås word-counts lilla statusgrej!
  (let [counts (atom {})]
    (bolt
     (execute [tuple]
       (let [word (.getString tuple 0)]
         (swap! counts (partial merge-with +) {word 1})
         (emit-bolt! collector [word (@counts word)] :anchor tuple)
         (ack! collector tuple)
         )))))

(defn mk-topology []
  (topology
   {"1" (spout-spec sentence-spout)
    "2" (spout-spec (sentence-spout-parameterized
                     ["the cat jumped over the door"
                      "greetings from a faraway land"])
                     :p 2)}
   {"3" (bolt-spec {"1" :shuffle "2" :shuffle}
                   split-sentence
                   :p 5)
    "4" (bolt-spec {"3" ["word"]}
                   word-count
                   :p 6)}))


(defn run-local! []
  (let [cluster (LocalCluster.)]
    (.submitTopology cluster "word-count" {TOPOLOGY-DEBUG true} (mk-topology))
    (Thread/sleep 10000)
    (.shutdown cluster)))

(defn submit-topology! [name]
  (StormSubmitter/submitTopology
   name
   {TOPOLOGY-DEBUG true
    TOPOLOGY-WORKERS 3}
   (mk-topology)))

(defn -main
  ([]
   (run-local!))
  ([name]
   (submit-topology! name)))

(comment (-main))
