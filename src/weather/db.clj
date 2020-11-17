(ns weather.db
  (:require [mount.lite :as m]))

;; For our purposes an atom serves fine as our db.
(m/defstate db
  :start (atom {})
  :stop nil)

(defn upsert-weather-data [data]
  (reset! @db (merge @@db data)))

