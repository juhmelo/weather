(ns weather.config-test
  (:require [weather.config :refer :all]
            [clojure.test :refer :all]))

(deftest test-config
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
