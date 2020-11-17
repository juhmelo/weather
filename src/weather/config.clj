(ns weather.config
  (:require [clojure.java.io :as io]
            [cyrus-config.core :as cfg]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(s/def ::id string?)
(s/def ::limit number?)

(s/def ::city-config (s/keys :req-un [::id ::limit]))

(s/def ::city-configs (s/coll-of ::city-config))

(cfg/def LOCATION_CONFIG
  {:spec ::city-configs})

(defn config []
  LOCATION_CONFIG)

(defn ids []
  (map :id LOCATION_CONFIG))

