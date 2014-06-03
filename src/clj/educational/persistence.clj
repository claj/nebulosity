(ns educational.persistence
"persistence layer for PostgreSQL"
(:require [clojure.java.jdbc :as sql]))

(def sql-settings "postgresql://linus:valkmon@localhost:5432/nebulosity")

(defn create-db []
  (sql/db-do-commands sql-settings "create table vectorspace ( vector_id serial primary key, vector double precision []);"))

(defn new-vector [double-vector]
  {:pre [ (every? #(= (class %) java.lang.Double) double-vector) (= (count double-vector) 1000)]}
  (sql/db-do-commands sql-settings 
                      (doall
                       (apply 
                        str 
                        "insert into vectorspace (vector) values ('{" 
                        (apply str (interpose ", " 
                                              (map #(Double/toString %) double-vector))) 
                        "}');"))))

(new-vector (vec (repeat 1000 0.99)))

(sql/db-do-commands sql-settings "insert into vectorspace (vector) values ('{1.0, 2.0}')")

;;TODO: store taskblob

;;TODO: versioning?

;;TODO: store student

;;TODO: make it possible to update the vectors


