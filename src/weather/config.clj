(ns weather.config
  (:require [clojure.java.io :as io]
            [cyrus-config.core :as cfg]))

(cfg/def LOCATION_CONFIG
  {:spec vector?})

(defn config []
  LOCATION_CONFIG)

(defn ids []
  (map :id LOCATION_CONFIG))

