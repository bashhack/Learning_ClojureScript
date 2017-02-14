(ns ssr.server
  (:require [ssr.core :as ssr]
            [foam.core :as foam]
            [foam.dom :as dom]
            [hiccup.core :as html]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.resource :as resource]))

(defn handler [req]
  {:status 200
   :body (base-html (foam-html))})

(def handler (-> handler
                 (resource/wrap-resource "public")))

(defn base-html [body]
  (html/html
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport"
            :content "width=device-width"
            :initial-scale 1}]
    [:link {:href "css/style.css" :rel "stylesheet" :type "text/css"}]]
   [:body
    [:div {:id "app"}
     (if body
       body
       [:2 "Server html response"])]
    ;; [:script {:src "js/compiled/ssr.js" :type "text/javascript"}]
    ]))

(defn app-state []
  (atom {:text "Hello from Clojure!"}))

(defn foam-html []
  (let [state (app-state)
        cursor (foam/root-cursor state)
        com (foam/build ssr/home cursor {})]
    (dom/render-to-string com)))

(defn main []
  (jetty/run-jetty handler {:port 8080
                            :host "0.0.0.0"
                            :join? false}))
