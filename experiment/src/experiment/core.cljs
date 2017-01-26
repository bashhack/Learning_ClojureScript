(ns experiment.core
  ;; (:require [experiment.utils])
  (:require [experiment.utils :as utils]
            [experiment.greeting :refer [hello]]
            [t]))

(enable-console-print!)

(println "This text is printed from src/experiment/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(defn adder-multiplier
  [x y z]
  ;; (* z (experiment.utils/adder x y))
  (str "Value of hello method call: " (hello (* z (utils/adder x y)))))

(println (str ">> Printing to browser console: " (adder-multiplier 1 2 3)))

(defn render
  []
  (.render (js/treeact)))

(render)
