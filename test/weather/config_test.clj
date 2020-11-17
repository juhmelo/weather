(ns weather.config-test
  (:require [weather.config :refer :all]
            [clojure.test :refer :all]
            [clojure.spec.alpha :as s]))

(deftest test-config

  (testing "spec"
    (is
     (not (s/valid? :weather.config/city-config {})))
    (is
     (not (s/valid? :weather.config/city-config {:id 1 :limit 1})))
    (is
     (not (s/valid? :weather.config/city-config {:id "12" :limit nil})))
    (is
     (s/valid? :weather.config/city-config {:id "12" :limit 0}))
    (is
     (s/valid? :weather.config/city-config {:id "12" :limit -1}))
    (is
     (s/valid? :weather.config/city-config {:id "12" :limit 1})))

  (testing "ids"
    (is
     (=
      (with-redefs [LOCATION_CONFIG []]
        (ids))
      []))
    (is
     (=
      (with-redefs [LOCATION_CONFIG nil]
        (ids))
      []))
    (is
     (=
      (with-redefs [LOCATION_CONFIG [{:id "1" :limit 1}]]
        (ids))
      ["1"]))
    (is
     (=
      (with-redefs [LOCATION_CONFIG [{:id "1" :limit 1} {:id "2" :limit 2}]]
        (ids))
      ["1" "2"]))))
