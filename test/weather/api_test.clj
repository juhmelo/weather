(ns weather.api-test
  (:require [clojure.test :refer :all]
            [mount.lite :as m]
            [clj-http.client :as http]
            [weather.api :refer :all]
            [weather.test-utils :as tu]
            [weather.db :as db]
            [weather.config :as config]))


(use-fixtures
  :each (fn [f]
          (tu/start-with-env-override '{HTTP_PORT 8080} #'server)
          (tu/start-with-env-override '{} #'db/db)
          (f)
          (m/stop)))

(deftest test-api
  (testing "Health endpoint returns 200"
    (is (= 200 (:status (http/get "http://localhost:8080/.health")))))

  (testing "Cities REST API endpoint returns correct content type, and returns a map as it's body"
    (is (= [200 clojure.lang.PersistentArrayMap "application/json; charset=utf-8"]
           ((juxt :status #(-> % :body type) #(get-in % [:headers "Content-Type"]))
            (http/get "http://localhost:8080/api/cities" {:as :json}))))))

(deftest test-payload
  (testing "city-payload"
    (is
     (=
      (city-payload {:id "123" :limit 1.2} {:max-temp 10.36 :city-name "A city"})
      {:id               "123"
       :name             "A city"
       :limit            1.2
       :forecast_max_temp 10.36
       :limit_exceeded   true}))
    (is
     (=
      (city-payload {:id "123" :limit 1.2} {:max-temp 0 :city-name "A city"})
      {:id               "123"
       :name             "A city"
       :limit            1.2
       :forecast_max_temp 0
       :limit_exceeded   false}))
    (is
     (=
      (city-payload {:id "123" :limit -1.2} {:max-temp -10.36, :city-name "A city"})
      {:id               "123",
       :name             "A city"
       :limit            -1.2
       :forecast_max_temp -10.36
       :limit_exceeded   false}))
    (is
     (=
      (city-payload {:id "123" :limit 0} {:max-temp 0 :city-name "A city"})
      {:id               "123"
       :name             "A city"
       :limit            0
       :forecast_max_temp 0
       :limit_exceeded   false}))
    (is
     (=
      (city-payload {:id "123" :limit 1.2} nil)
      {:id "123" :name nil :limit 1.2 :message "Forecast data not available."})))
  (testing "cities-payload"
    (is
     (=
      (with-redefs [db/db         (atom (atom {"658225" {:max-temp 10.36 :city-name "Helsinki"},
                                              "2960"   {:max-temp 18.71 :city-name "‘Ayn Ḩalāqīm"}}))
                    config/config (fn [] [{:id "658225" :limit 13.2} {:id "2960" :limit 3.7}])]
        (cities-payload))
      {:forecast
       {"658225" {:max-temp 10.36 :city-name "Helsinki"},
        "2960" {:max-temp 18.71 :city-name "‘Ayn Ḩalāqīm"}}
       :limits [{:id "658225" :limit 13.2} {:id "2960" :limit 3.7}],
       :cities
       [{:id "658225"
         :name "Helsinki"
         :limit 13.2
         :forecast_max_temp 10.36
         :limit_exceeded false}
        {:id "2960"
         :name "‘Ayn Ḩalāqīm"
         :limit 3.7
         :forecast_max_temp 18.71
         :limit_exceeded true}]}))
    (is
     (=
      (with-redefs [db/db         (atom (atom {}))
                    config/config (fn [] [{:id "658225" :limit 13.2} {:id "2960" :limit 3.7}])]
        (cities-payload))
      {:forecast {}
       :limits [{:id "658225" :limit 13.2} {:id "2960" :limit 3.7}]
       :cities
       [{:id "658225"
         :name nil
         :limit 13.2
         :message "Forecast data not available."}
        {:id "2960" :name nil :limit 3.7 :message "Forecast data not available."}]}))
    (is
     (=
      (with-redefs [db/db         (atom (atom {}))
                    config/config (fn [] nil)]
        (cities-payload))
      {:forecast {}, :limits nil, :cities []}))))
