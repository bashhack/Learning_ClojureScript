(ns om-tut.core
  (:require [accountant.core :as accountant]
            [ajax.core :as ajax]
            [bidi.bidi :as bidi]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(println "This text is printed from src/om-tut/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(def api-server "http://localhost:8080")

(defonce app-state (atom {:text "Hello world!"
                          :todos []}))

(defn settings [app owner opts]
  (reify
    om/IRender
    (render [_]
      (dom/div
       nil
       (dom/a #js {:href "/"} "Back")
       (dom/h1 nil "Settings")))))

(defn load-todos [todos]
  (ajax/GET (str api-server "/todos")
            {:handler (fn [response]
                        (om/update! todos response))}))

(defn new-todo [todos]
  (let [todo {:text "New Todo" :done? false}]
    (ajax/POST (str api-server "/todos")
               {:params todo
                :handler (fn [resp]
                           (let [todo (assoc todo :id resp)]
                             (om/transact! todos [] (fn [todos]
                                                      (conj todos todo)))))})))

;; (defonce app-state (atom {:text "Hello world!"
;;                           :todos [{:id 1
;;                                    :done? true
;;                                    :text "buy book"}
;;                                   {:id 2
;;                                    :done? false
;;                                    :text "learn ClojureScript"}]}))

;; (defn todo-item
;;   [cursor owner]
;;   (reify om/IRender
;;     (render [this]
;;       (dom/span #js {:className (when (:done? cursor) "done")} (:text cursor)))))

(defn todo-item
  [cursor owner]
  (reify om/IRender
    (render [this]
      (dom/div
       nil
       (dom/input #js {:type "checkbox"
                       :checked (:done? cursor)
                       :onChange (fn [e]
                                   (om/update! cursor [:done?] (-> e .-target .-checked)))})
       (dom/span #js {:className (when (:done? cursor) "done")}
                 (:text cursor))))))

;; (defn todo-list
;;   [cursor owner]
;;   (reify om/IRender
;;     (render [_]
;;       (dom/div nil
;;                (dom/h1 nil (:text cursor))
;;                (om/build-all todo-item (:todos cursor) {:key :id})))))

(defn todo-list [cursor owner]
  (reify om/IRender
    (render [_]
      (dom/div nil
               (dom/a #js {:href "/settings"} "Settings")
               (dom/h1 nil (:text cursor))
               (om/build-all todo-item (:todos cursor) {:key :id})
               (dom/button #js {:onClick (fn [e]
                                           (new-todo (:todos cursor)))} "New")))
    om/IWillMount
    (will-mount [this]
      (load-todos (:todos cursor)))))

(def routes ["/" {"" todo-list
                  "settings" settings}])

(defn nav-handler [cursor path]
  (om/update! cursor [:active-component] (:handler (bidi/match-route routes path))))

(defn render-component [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div
       nil
       (if-let [c (:active-component app)]
         (om/build c app {})
         (dom/p nil "no active component"))))))

(defn main []
  (om/root
   render-component
   app-state
   {:target (. js/document (getElementById "app"))})

  (let [cursor (om/root-cursor app-state)]
    (accountant/configure-navigation!
     {:nav-handler (fn [path]
                     (nav-handler cursor path))
      :path-exists? (fn [path]
                      (boolean (bidi/match-route routes path)))})
    (accountant/dispatch-current!)))

(main)

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
