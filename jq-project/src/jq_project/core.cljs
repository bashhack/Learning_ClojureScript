(ns jq-project.core
  (:use [jayq.core :only [$ css html]]))

(enable-console-print!)

(println "This text is printed from src/jq-project/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(def $main-div ($ :#main-div))

(defn change-the-div!
  []
  (-> $main-div
      (css {:background "cyan"})
      (html "Changed Inner HTML")))
