(ns figwheel-node.core
  (:require [cljs.nodejs :as nodejs]))

(nodejs/enable-util-print!)

(defn -main []
  (println "Hello world - from a Node project!!!"))

(set! *main-cli-fn* -main)
