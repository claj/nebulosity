(ns educational.persistence
  "persistence layer for PostgreSQL

using the quite ugly double precision numbers in vectors in PSQL.

TODO: thread safety at creation time.
TODO: use only increase for manipulating vectors to be safe
TODO: add a blob-store or something for tasks, 
that can keep vector indexes for the vectors assigned to the task
TODO: add something for storing students similar to the tasks, with login details 
TODO: add some logging thing to be able to rerun everyhting when things go haywire
TODO: put the connection string somewhere else.
TODO: add PLSQL function that creates a zeroed out double precision array in some specified row
TODO: figure out how to use RETURNING with clojure.java.jdbc"
  (:require [clojure.java.jdbc :as sql] 
            [clojure.edn :as edn]))

(def sql-settings "postgresql://linus:valkmon@localhost:5432/nebulosity")

(defn create-task-db 
  "creates a table for storing tasks as edn blobs"
  []
  (sql/db-do-commands sql-settings 
                      (sql/create-table-ddl 
                       :tasks 
                       [:id :serial :primary :key] 
                       [:ednblob :text :not :null])))
(create-task-db)



(defn store-task [task]
  ;;validate the task!
  (sql/insert! sql-settings :tasks {:ednblob (with-out-str (prn task))}))

(defn read-task 
  "reads and deserializes a task data structure given its database id"
  [id]
  (-> (sql/query sql-settings 
                 (str 
                  "SELECT tasks.ednblob FROM tasks WHERE tasks.id = " id))
      first
      :ednblob
      edn/read-string))

(comment (sql/insert! sql-settings :tasks {:ednblob (with-out-str (prn {:text "jello"}))}))
(comment (read-task 1))

(defn create-vector-db 
  "creates a table for string vectors, auto-increasing ID -> Double[] vector"
  []
  (sql/db-do-commands sql-settings "CREATE TABLE vectorspace ( vector_id SERIAL PRIMARY KEY, vector DOUBLE PRECISION []);"))

(defn new-vector 
  "takes a double vector, length must be 1000.
INSERT an empty row thing to get the id out.
UPDATE the vector with a (string of) all the doubles.

should be in transaction, but isn't ATM"
  [double-vector]
  {:pre [ (every? #(= (class %) java.lang.Double) double-vector) (= (count double-vector) 1000)]}
  (let [vector_id (:vector_id (first (sql/insert! sql-settings :vectorspace {:vector nil})))] ;;returns the vector id. yay!
    (assert vector_id)
    (sql/db-do-commands sql-settings 
                        (apply 
                         str 
                         "UPDATE vectorspace SET vector='{" 
                         (apply str (interpose "," 
                                               (map #(Double/toString %) double-vector))) 
                         "}' WHERE vector_id=" vector_id ";"))))

(defn gentle-set!
  "sets a the value (zero indexed!) in the vector-id vector to value"
  [vector-id key val]
  {:pre [(<= 0 key 999) (number? val)] }
  (let [insane-indexing (inc key)]
    (sql/db-do-commands sql-settings (apply str "UPDATE vectorspace SET vector[" insane-indexing "]=" val " WHERE vector_id=" vector-id ";"))))

(defn gentle-increase 
  "increases a value in vector-id, position key (zero indexed) and increases it with increase"
  [vector-id key increase]
  {:pre [(<= 0 key 999) (number? increase)]}
  (let [insane-indexing (inc key)]
    (sql/db-do-commands sql-settings 
                        (apply str 
                               "UPDATE vectorspace SET vector[" insane-indexing "]= vector[ "  insane-indexing "] + "
                               increase " WHERE vector_id=" vector-id ";"))))

(comment (gentle-increase 29 4 10))
(comment (gentle-set! 29 2 34.0))


