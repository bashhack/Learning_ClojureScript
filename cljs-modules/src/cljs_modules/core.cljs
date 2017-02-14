(ns cljs-modules.core
  (:require [accountant.core :as accountant]
            [bidi.bidi :as bidi]
            [om.core :as om]
            [om.dom :as dom :include-macros true]
            [cljs-modules.modules :as modules]
            [cljs-modules.render :as render]))

(enable-console-print!)

(println "This text is printed from src/cljs-modules/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(defonce app-state (atom {:active-component :outer/outer}))

(defn nav-handler [cursor path]
  (om/update! cursor [:active-component] (:handler (bidi/match-route render/routes path))))

(defn main []
  (om/root
   render/render
   app-state
   {:target (. js/document (getElementById "app"))})

  (let [cursor (om/root-cursor app-state)]
    (accountant/configure-navigation!
     {:nav-handler (fn [path]
                     (nav-handler cursor path))
      :path-exists? (fn [path]
                      (boolean (bidi/match-route render/routes path)))})
    (accountant/dispatch-current!)))

(main)
