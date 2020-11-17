(ns weather.core
  (:require [mount.lite :as m]
            [dovetail.core :as log]
            [cyrus-config.core :as cfg]
            [weather.utils :as u]
            [weather.api])
  (:gen-class))


(cfg/def LOG_LEVEL)


(defn -main [& args]
  (log/disable-console-logging-colors)
  (log/set-level! :info)
  (log/set-log-level-from-env! LOG_LEVEL)
  (log/info "Starting weather version %s" (u/implementation-version))
  (log/info "States found: %s" @m/*states*)
  (try
    (cfg/validate!)
    (log/info (str "Config loaded:\n" (cfg/show)))
    (m/start)
    (log/info "Application started")
    ;; Prevent -main from exiting to keep the application running
    @(promise)
    (catch Exception e
      (log/error e "Could not start the application.")
      (System/exit 1))))


(log/set-ns-log-levels!
  {"weather.*" :debug
   "com.zaxxer.hikari.*" :warn
   :all :info})


(log/set-output-fn! log/default-log-output-fn)


(comment
  (log/set-level! :info)
  (log/set-level! :debug)
  ;; Starting and stopping the application during NREPL access
  (m/start)
  (m/stop)
  ;; Override some environment variables
  (cfg/reload-with-override! {"HTTP_PORT" 8888}))
