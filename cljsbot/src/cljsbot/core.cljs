(ns cljsbot.core
  (:require [ajax.core :as ajax]
            [cljs.core.async :as a]
            [chord.client :as chord])
  (:require-macros [cljs.core.async.macros :refer (go)]))

(enable-console-print!)

(println "This text is printed from src/cljsbot/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(def slack-endpoint "https://slack.com/api")

(defonce next-massage-id (atom 0))

(defn get-next-message-id []
  (swap! next-message-id inc))

(defn slack-api [{:keys [path request-method token]
                  :or {request-method :get}
                  :as args}]
  (let [method (condp = request-method
                 :get ajax/GET
                 :post ajax/POST
                 (throw (js/Error. "unrecognized :request-method" (get args :request-method))))]
    (method
     (str slack-endpoint path)
     (-> args
         (merge {:format :json
                 :response-format :json
                 :keywordize? true})
         (assoc-in [:params :token] token))
     :params {:token token})))

(defn rtm-start
  "Connect to slack. Returns a channel containing the websocket channel"
  [{:keys [token] :as args}]
  (let [ret-chan (a/chan)]
    (slack-api {:path "/rtm.start"
                :token token
                :handler (fn [resp]
                           (go
                             (let [url (get resp "url")
                                   {:keys [ws-channel error]}
                                   (a/<! (chord/ws-ch url
                                                      {:format :json-kw}))]
                               (if-not error
                                 (do
                                   (a/put! ret-chan ws-channel)
                                   (a/close! ret-chan))
                                 (println "Error:" (pr-str error))))))})
    ret-chan))

(defn print-all-handler [ws-chan]
  {:pre [ws-chan]}
  (go
    (loop []
      (when-let [e (a/<! ws-chan)]
        (js/console.log "event:" e)
        (recur)))))

(defn list-channels [token]
  (slack-api {:path "/channels.list"
              :token token
              :handler (fn [resp]
                         (println "channels" resp))}))

(defn send-message
  [ws-chan {:keys [channel text] :as msg}]
  {:pre [channel text]}
  (go
    (a/>! ws-chan (merge msg {:id (get-next-message-id)
                              :type "message"}))))

(defn init-websocket [appstate]
  (go
    (let [ws-chan (a/<! (rtm-start {:token "TOKEN VALUE"}))]
      (print-all-handler ws-chan)
      (send-message ws-chan {:channel "CHANNEL ID" :text "hello slack world"}))))
