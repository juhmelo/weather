(ns weather.db-test
  (:require [weather.db :refer :all]
            [clojure.test :refer :all]
            [mount.lite :as m]
            [weather.test-utils :as tu]))

(use-fixtures
  :each (fn [f]
          (tu/start-with-env-override '{} #'db)
          (f)
          (m/stop)))

(deftest db-test
  (testing "upsert-weather-data"
    (is
     (=
      (do
        (upsert-weather-data {"id" :data1})
        (upsert-weather-data {"id" :data2})
        @@db)
      {"id" :data2}))
     (=
      (do
        (upsert-weather-data {"id" :data})
        (upsert-weather-data {"id2" :data2})
        @@db)
      {"id" :data, "id2" :data2})))

