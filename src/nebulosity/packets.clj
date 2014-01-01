(ns nebulosity.packets
"parse common network packet formats with ztellman/gloss

* IPv4 works, missing extra IHL options after header, no CRC check"
(:require [gloss.core :as g] [gloss.io]))

;;;;;;;;;;;;;;;;;;;; IP version 4 ;;;;;;;;;;;;;;;;

;;IPv4 Header specification:

;;octet(s), field(s)
;;0 version 4-bit, IHL 4-bit
;;1 DCSP 6-bit, ECN 2-bit
;;2-3 Total Length
;;4-7 Identification
;;7-8 Flags 3-bit, Fragment Offset 13-bit
;;9 Time To Live
;;10 Protocol
;;11 Header Checksum
;;12-15 Source IP
;;16-19 Dest IP
;;20-160 options (if IHL>5)

;;http://en.wikipedia.org/wiki/IPv4

(def ipv4-frame (g/compile-frame 
                 (g/ordered-map 
                  :header (g/bit-map :ip-version 4
                                     :ihl-length 4
                                     :dcsp 6
                                     :ecn 2)
                  :total-length :int16 
                  :identification :int16 
                  :flags (g/bit-map :reserved 1
                                    :dont-fragment 1
                                    :more-fragments 1
                                    :fragment-offset 13)
                  :time-to-live :byte
                  :protocol (g/enum :byte {:icmp 1
                                           :igmp 2
                                           :tcp 6
                                           :udp 17
                                           :ipv6-encapsulation 41
                                           :ospf 89
                                           :sctp 132})
                  :header-checksum :int16 
                  :source-ip (g/bit-seq 8 8 8 8) 
                  :destination-ip (g/bit-seq 8 8 8 8))))

;;WARNING WARNING WARNING
;;cannot take IHL>5 ATM




