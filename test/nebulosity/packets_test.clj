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

(deftest read-version-correctly
  (= 4 (get-in 
        (gloss.io/decode nebulosity.packets/ipv4-frame (nio/byte-buffer (nio/mmap "resources/ipheader.bytes")))
        [:header :ip-version])))

