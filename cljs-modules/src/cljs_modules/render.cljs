(ns cljs-modules.render
  (:require [cljs-modules.modules :as modules]
            [om.core :as om]
            [om.dom :as dom])
  (:require-macros [cljs.core.async.macros :refer (go)]))

(def routes ["/" {"" :outer/outer
                  "app" :inner/inner}])

(defmulti active-component identity)

(defmethod active-component :default [_]
  nil)

(defn handler-module [handler]
  (namespace handler))

(defn require-module! [app module]
  (when modules/modules?
    (modules/require-module app module)))

(defn render [app owner opts]
  (reify
    om/IRender
    (render [_]
      (let [c (get-in @app [:active-component])
            module (handler-module c)
            loaded? (modules/loaded? module)]
        (dom/div nil
                 (if loaded?
                   (let [cfn (active-component c)]
                     (om/build cfn app {:opts opts}))
                   "Loading..."))))
    om/IWillMount
    (will-mount [_]
      (let [c (get-in @app [:active-component])
            module (handler-module c)
            loaded? (modules/loaded? module)]
        (require-module! app module)))
    om/IWillReceiveProps
    (will-receive-props [_ next-props]
      (let [next-component (get-in next-props [:active-component])
            next-module (handler-module next-component)]
        (require-module! app next-module)))))
