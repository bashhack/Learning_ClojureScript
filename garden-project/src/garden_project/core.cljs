(ns garden-project.core
  (:require [garden.core :refer [css]]
            [goog.style]))

(enable-console-print!)

(println "This text is printed from src/garden-project/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(defn modify-css!
  []
  (goog.style/installStyles (css
                             [:div {:border-style "solid"}
                              [:&:hover {:border-style "dashed"}]
                              [:p {:background-color "cyan"}]])))
