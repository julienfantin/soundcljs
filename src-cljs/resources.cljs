(ns soundcljs.resources)

(defrecord Track [id])
(defrecord User [id])
(defrecord Resource [endpoint parent-resource result-mapping-fn])

(defprotocol Subresources
  "A Protocol for managing resources and their associated subresources."
  (subresource [this]))

(extend-protocol Subresources
  Track
  (subresource [this]
    (->Resource (str "/tracks/" (:id this) "/favoriters") this map->User))
  User
  (subresource [this]
    (->Resource (str "/users/" (:id this) "/favorites") this map->Track))
  js/Object
  (subresource [this] nil))
