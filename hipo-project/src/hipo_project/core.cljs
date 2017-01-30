(ns hipo-project.core
  (:require [hipo.core :as hipo]))

(enable-console-print!)

(println "This text is printed from src/hipo-project/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(defn create-menu-v
  [items]
  [:ul#my-menu
   (for [x items]
     [:li {:id x} x])])

(def menu (hipo/create (create-menu-v ["it1" "it2" "it3"])))

(defn add-menu!
  []
  (.appendChild js/document.body menu))

(defn reconcile-new-menu!
  []
  (hipo/reconciliate! menu (create-menu-v ["new it1" "new it2" "new it3"])))

(add-menu!)
