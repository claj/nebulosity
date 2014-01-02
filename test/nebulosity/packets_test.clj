(ns nebulosity.packets-test
  (:require [clojure.test :refer :all]
            [nebulosity.packets :refer :all]
            [gloss.io]
            [nio.core :as nio]))
;;read a byte file of an IP-header
;;with the following specs
;;Header length 20 bytes
;;Internet Protocol Version 4, 
;;Src: 145.254.160.237
;;Dst: 65.208.228.223
;;Total length 48
;;Identification 0x0f41 (3905)
;;flags
;; reserved: not set
;; don't fragment: set
;; more fragments: not set
;; fragment offset: 0
;; ttl: 128
;; protocol: TCP (6)
;;header checksum: 0x91eb = OK!

;;how to read a byte buffered file then_

(deftest read-ip-version
  (= 4 (get-in 
        (gloss.io/decode nebulosity.packets/ipv4-frame (nio/byte-buffer (nio/mmap "resources/ipheader.bytes")))
        [:header :ip-version])))

;;src port: 80
;;dest port: 3372
;;tip2: 3372
;;seq 1
;;ack 480
;;len 1380


(deftest read-tcp-header
  (let [parsed-data (gloss.io/decode nebulosity.packets/tcp-frame (nio/byte-buffer (nio/mmap "resources/tcpheader.bytes")))]
    (is (= 80 (:source-port parsed-data)))
    (is (= 3372 (:destination-port parsed-data)))
    (is (= 0 (-> parsed-data :flags :reserved))))
   
  ;;{:urgent-pointer 0, 
  ;; :checksum 11018, 
  ;; :window-size 6432, 
  ;; :flags {:data-offset 5, 
  ;;         :reserved 0, 
  ;;         :ns false,
  ;;         :psh false,
  ;;         :fin false,
  ;;         :ack true, :urg false, :ece false, :rst false, :syn false, :cwr false}, 
  ;; :ack-number 951058419, 
  ;; :sequence-number 290218380, 
  ;; :destination-port 3372, 
  ;; :source-port 80}
)

;;User Datagram Protocol, Src Port: 53, Dst Port: 3009

(deftest read-udp-header
 (let [parsed-data (gloss.io/decode nebulosity.packets/udp-frame (nio/byte-buffer (nio/mmap "resources/udpheader.bytes")))]
    (is (= 53 (:source-port parsed-data)))
    (is (= 3009 (:destination-port parsed-data)))
    (is (= 154 (:length parsed-data)))
))
