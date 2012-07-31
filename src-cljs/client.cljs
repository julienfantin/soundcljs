(ns soundcljs.client
  (:require [soundcljs.resources :as resources]
            [soundcljs.metrics :as metrics]
            [soundcljs.queue :as queue]
            ;; dev
            [clojure.browser.repl :as repl]
            [clojure.reflect :as reflect])
  (:use [soundcloud :only [initialize]])
  (:require-macros [clojure-playground.async :as async]))

(repl/connect "http://localhost:9000/repl")

(def ^:private client-id "cab243f44bc34d57e99fb4e254d7ca3b")
(def ^:private callback-url "http://0.0.0.0:3000/callback.html")

(def api js/SC)

(defn- ensure-vector [vec-or-x]
  (if (vector? vec-or-x) vec-or-x (vector vec-or-x)))

(defn- convert-api-response [resource response]
  (let [f (:result-mapping-fn resource)]
    (reduce (fn [coll res]
              (conj coll (f res)))
            []
            (ensure-vector (js->clj response :keywordize-keys true)))))

(defn get-request [resource continuation]
  (.get api (:endpoint resource)
        (.log js/console "Getting: " (:endpoint resource))
        (fn [response error]
          (if error
            (.log js/console error)
            (continuation (convert-api-response resource response))))))

(def ^:export me (atom nil))
(def ^:export track (atom nil))

(def on-connect
  (fn []
    (async/let-series
     [[details & _] (fn [c]
                      (let [resource (resources/->Resource "/me" nil resources/map->User)]
                        (get-request resource #(c %))))
      [fav & _ :as favs] (fn [c]
                           (let [resource (resources/subresource details)]
                             (get-request resource #(c %))))]
     (reset! me details)
     (reset! track fav)
     (.log js/console  details)
     (.log js/console fav))))


(defn ^:export connect []
  (when-not (.isConnected api)
    (.initialize api (js-obj "client_id" client-id
                             "redirect_uri" callback-url))
    (.connect api on-connect)))
