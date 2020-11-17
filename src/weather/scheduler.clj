(ns weather.scheduler
  (:require [weather.http :as http]
            [weather.config :as config]
            [mount.lite :as m]
            [overtone.at-at :as at-at]
            [cyrus-config.core :as cfg]))

(cfg/def POLL_INTERVAL_MS "How often to poll weather data."
  {:spec    int?
   :default 10000})

(def jobs (at-at/mk-pool))

(defn get-weather-data []
  "Get weather data for all configured cities."
  (http/get-weather-data (config/ids)))

(m/defstate scheduler
  :start (let [interval-ms POLL_INTERVAL_MS]
           (at-at/every interval-ms get-weather-data jobs))
  :stop (at-at/stop-and-reset-pool! jobs))
