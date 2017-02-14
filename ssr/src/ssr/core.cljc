(ns ssr.core
  (:require #?@(:clj [[foam.core :as om]
                      [foam.dom :as dom]]
                :cljs [[om.core :as om]
                       [om.dom :as dom]])))

(enable-console-print!)

(println "This text is printed from src/ssr/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

;; (defonce app-state (atom {:text "Hello from CLJS"}))

(defn home [app owner opts]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
               (:text app)))))
(defn main []
  #?(:cljs
     (om/root
      home
      app-state
      {:target (. js/document (getElementById "app"))})))

(main)
