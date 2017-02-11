(ns ds.core
  (:require [datascript.core :as d]))

(enable-console-print!)

(println "This text is printed from src/ds/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(defn datascript-test []
  (let [schema {:movie/actors {:db/cardinality :db.cardinality/many
                               :db/valueType :db.type/ref}
                :movie/director {:db/valueType :db.type/ref}}
        conn (d/create-conn schema)]
    ;; (d/transact! conn [{:db/id -1
    ;;                     :movie/title "Top Gun"
    ;;                     :movie/year 1986}])
    (d/transact! conn [{:db/id -1
                        :person/name "Tom Cruise"}
                       {:db/id -2
                        :person/name "Anthony Edwards"}
                       {:db/id -3
                        :person/name "Tony Scott"}
                       {:db/id (d/tempid :user)
                        :movie/title "Top Gun"
                        :movie/year 1986
                        :movie/actors [-1 -2]
                        :movie/director -3}
                       {:db/id -4
                        :person/name "Arnold Schwarzenegger"}
                       {:db/id (d/tempid :user)
                        :movie/title "Terminator"
                        :movie/actors -4}
                       {:db/id -5
                        :person/name "Mel Brooks"}
                       {:db/id (d/tempid :user)
                        :movie/title "Spaceballs"
                        :movie/actors -5
                        :movie/director -5}
                       {:db/id -6
                        :person/name "Clint Eastwood"
                        :person/birth-year 1930}
                       {:db/id -7
                        :person/name "Morgan Freeman"}
                       {:db/id -8
                        :person/name "Gene Hackman"}
                       {:db/id -9
                        :person/name "Eli Wallach"}
                       {:db/id (d/tempid :user)
                        :movie/title "The Good, The Bad and The Ugly"
                        :movie/actors [-6 -9]}
                       {:db/id (d/tempid :user)
                        :movie/title "Unforgiven"
                        :movie/actors [-6 -7 -8]
                        :movie/director -6}])
    (-> (d/datoms @conn :eavt)
        (seq)
        (first)
        (println))))

(datascript-test)

;; NOTE: To create a basic query, we could write:
;; (d/q '[:find ?e :in $ ?name :where [?e :movie/title ?name]] @conn "Top Gun")
