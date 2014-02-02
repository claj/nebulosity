(ns nebulosity.distributed-configuration 
"There's a lot of various problems to solve in distributed systems

Regarding configuration I have identified three for one particular case:

- Directory services (which servers, users and resources are currently availiable?)
- Static configuration (what do we want the system to do?)
- The systems nescessary adaptations to it's environment, 'state'

The state is often both small options (booleans) and large (cached lists of domain names) 
in terms of memory size, there's also different behaviours on how much the state must be
dispersed into the system

the static configuration will change, but slowly (ie user induced)

the directory service needs some kind of push mechanism to not
drown the system in update requests")

;; checkout: aphyrs work (some asa goodess)
;; checkout: how storm does
;; checkout: how datomic does
;; checkout: osgi


;;Storm: uses the topology concept
;;









