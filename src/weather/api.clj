(ns weather.api
  (:require [mount.lite :as m]
            [aleph.http]
            [aleph.netty]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.middleware :refer [wrap-canonical-redirect]]
            [ring.middleware.defaults :refer [wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params wrap-json-body]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.util.response :as resp]
            [manifold.deferred :as md]
            [dovetail.core :as log]
            [cyrus-config.core :as cfg]
            [weather.lib.http :as httplib]
            [weather.db :as db]
            [weather.config :as config]
            [io.sarnowski.swagger1st.core :as s1st]
            [io.sarnowski.swagger1st.executor :as s1stexec]))


(cfg/def HTTP_PORT "Port for HTTP server to listen on."
  {:spec    int?
   :default 8090})

(defn city-payload [config forecast]
  "Construct payload for single city"
  (let [{:keys [id limit]}           config 
        {:keys [max-temp city-name]} forecast]
    (merge
     {:id      id
      :name    city-name
      :limit   limit}
     (if max-temp
       {:forecast_max_temp max-temp
        :limit_exceeded         (> max-temp limit)}
       {:message "Forecast data not available."}))))

(defn cities-payload []
  "Construct payload for all configured cities"
  (let [forecasts        @@db/db
        location-configs (config/config)
        cities           (for [{:keys [id] :as config} location-configs]
                           (city-payload config (get forecasts id)))]
    {:forecast forecasts
     :limits   location-configs
     :cities   cities}))

(defn get-cities [_ _]
  (log/info "Request for cities")
  {:status  200
   :body    (cities-payload)})

(defn get-city [{:keys [id]} _]
  (log/info (str "Request for city id " id))
  (let [config   (->>
                  (config/config)
                  (filter #(= id (:id %)))
                  first)
        forecast (get @@db/db id)]
    (if-not config
      {:status 404
       :body {:error (str "City not found with given id " id)}}
     {:status 200
      :body   (city-payload config forecast)})))

;;
;; boilerplate below this point
;;

(defn remove-trailing-slash
  "Remove the trailing '/' from a URI string, if it exists, unless the URI is just '/'"
  [^String uri]
  (if (= "/" uri)
    uri
    (compojure.middleware/remove-trailing-slash uri)))


(def api-defaults
  (-> ring.middleware.defaults/api-defaults
      (assoc-in [:security :hsts] true)))

(defn resolve-operation
  "Calls operationId function with flattened request params and raw request map."
  [request-definition]
  (when-let [operation-fn (s1stexec/operationId-to-function request-definition)]
    (fn [request]
      (operation-fn (apply merge (vals (:parameters request))) request))))


(m/defstate handler
  :start (-> (s1st/context :yaml-cp "api.yaml")
             (s1st/discoverer :definition-path "/api/swagger.json" :ui-path "/api/ui/")
             ;; Given a path, figures out the spec part describing it
             (s1st/mapper)
             ;; Extracts parameter values from path, query and body of the request
             (s1st/parser)
             ;; Now we also know the user, replace request info
             (s1st/ring httplib/wrap-request-log-context)
             ;; Calls the handler function for the request. Customizable through :resolver
             (s1st/executor :resolver resolve-operation)))

;; Middleware rule of thumb: Request goes bottom to top, response goes top to bottom
(defn make-handler []
  (-> (routes
        (GET "/.health" _ {:status 200})
        ;; Swagger1st API implementation: https://github.com/zalando-stups/swagger1st
        (-> (routes
              (ANY "/api" req (resp/redirect "/api/ui/" 301))
              (ANY "/api/" req (resp/redirect "/api/ui/" 301))
              (ANY "/api/ui" req (resp/redirect "/api/ui/" 301))
              (ANY "/api/*" req (@handler req)))
            (wrap-json-response)
            (wrap-defaults api-defaults))
        (route/not-found nil))
      ;; It never hurts to gzip
      (wrap-gzip)
      (httplib/wrap-request-log-context)))


(m/defstate server
  :start (do
           (log/info "Starting HTTP server")
           (let [started-server (aleph.http/start-server (make-handler) {:port HTTP_PORT})]
             (log/info "HTTP server is listening on port %s" (aleph.netty/port started-server))
             started-server))
  :stop (.close @server))
