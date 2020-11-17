(ns weather.http-test
  (:require [weather.http :refer :all]
            [clojure.test :refer :all]))

(deftest http-test
  (testing "format-response"
    (is
     (=
      (format-response :id {:body
                            {:city {:name "city name"}
                             :list [{:main {:temp_max 1}} {:main {:temp_max 2}}]}})
      {:id {:max-temp 2 :city-name "city name"}})
     (=
      (format-response :id {:body
                            {:city {:name "city name"}
                             :list [{:main {:temp_max -1}} {:main {:temp_max -2}}]}})
      {:id {:max-temp -1 :city-name "city name"}}))))

