(ns weather.db
  (:require [cheshire.core :as json]
            [clojure.java.io :as io]))

;; Not needed for now.
#_(defonce cities
  (json/parse-string (slurp (io/resource "clist.json")) keyword))


;; For our purposes an atom serves fine as our db.
(def db (atom {}))

(defn upsert-weather-data [data]
  (reset! db (merge @db data)))
