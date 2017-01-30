(ns domina-project.core
  (:require [domina :as dom]
            [domina.css :as css]
            [domina.events :as events]))

(enable-console-print!)

(println "This text is printed from src/domina-project/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(def the-div (css/sel "#a-div"))
(def the-href (dom/html-to-dom "<a></a>"))
(def the-btn (dom/html-to-dom "<button></button>"))

(defn add-dom-elts!
  []
  (doto the-href
    (dom/set-text! "Wikipedia")
    (dom/set-attr! :href "http://en.wikipedia.org"))

  (dom/append! the-div the-href)
  (doto the-btn
    (dom/set-text! "Click me!")
    (dom/set-attr! :type "button"))

  (events/listen!
   the-btn :click
   (fn [evt]
     (let [my-name (-> evt events/current-target dom/text)]
       (js/alert (str "hello world! from : " my-name) ))))

  (dom/append! the-div the-btn))
