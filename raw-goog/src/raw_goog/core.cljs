(ns raw-goog.core
  (:require [goog.dom :as dom]
            [goog.events :as events]))

(enable-console-print!)

(println "This text is printed from src/raw-goog/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(def cnt-holder (dom/getElement "clicks"))

(def reset-btn (dom/getElement "reset-btn"))

(def cnt (atom 0))

(defn inc-clicks!
  []
  (dom/setTextContent cnt-holder (swap! cnt inc)))

(defn reset-clicks!
  []
  (dom/setTextContent cnt-holder (reset! cnt 0)))

(events/listen cnt-holder "click" inc-clicks!)
(events/listen reset-btn "click" reset-clicks!)
