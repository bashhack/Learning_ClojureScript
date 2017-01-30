(ns dommy-project.core
  (:require [dommy.core :as dommy :refer-macros [sel sel1]]))

(enable-console-print!)

(println "This text is printed from src/dommy-project/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(defn set-borders!
  []
  (let [all-ps (sel [:#a-div :p])]
    (->> all-ps
         (map #(dommy/remove-class! % :changeme))
         (map #(dommy/add-class! % :border))
         (map #(dommy/set-text! % "I now have a border!")))))

(defn add-btn!
  []
  (let [the-div (sel1 :#a-div)
        a-btn (dommy/create-element "button")]
    (dommy/set-text! a-btn "Click me!")
    (dommy/listen! a-btn :click
                   (fn [e] (js/alert "you clicked me!")))
    (-> the-div
        (dommy/append! a-btn))))
