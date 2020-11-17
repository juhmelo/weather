(ns weather.http
  (:require [clj-http.client :as client]
            [weather.db :as db]
            [cyrus-config.core :as cfg]
            [clojure.string :as str]
            [weather.config :as config]
            [dovetail.core :as log]))

(cfg/def API_KEY "To use with calls to weather service."
  {:spec string?})

(cfg/def FORECAST_URL "Weather service url."
  {:spec string? ;; Could also regex to see if it is a properly formed address.
   :default "https://api.openweathermap.org/data/2.5/forecast"})

(defn format-response [id {:keys [body]}]
  "Gets currently relevant information out of response body."
  (let [max-temps (map #(-> % :main :temp_max) (:list body))
        max-temp  (reduce #(if (> %1 %2) %1 %2) max-temps)]
    {id {:max-temp  max-temp
         :city-name (-> body :city :name)}}))

(defn get-weather-data [ids]
  "Gets weather forecast data ascynchronously and persists into db"
  (doseq [id ids]
    (client/get FORECAST_URL
                {:query-params {:id    id
                                :units ["metric"]
                                :APPID API_KEY}
                 :async        true
                 :as           :json
                 :exclude      "current,minutely,hourly,alerts"}
                (fn [response]
                  (log/debug (str "Persisting weather data for " id))
                  (db/upsert-weather-data (format-response id response)))
                (fn [exception]
                  ;; Currently no retrying if we fail
                  (log/error exception "Retrieving weather data failed.")))))
