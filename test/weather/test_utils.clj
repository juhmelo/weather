(ns weather.test-utils
  (:require [clojure.test :refer :all]
            [mount.extensions.namespace-deps :as mnd]
            [cyrus-config.core :as cfg]))


(defn start-with-env-override [env-override & args]
  (cfg/reload-with-override! env-override)
  (apply mnd/start args))
