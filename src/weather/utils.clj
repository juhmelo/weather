(ns weather.utils)


(defn implementation-version []
  (or
    ;; When running in a REPL
    (System/getProperty "weather.version")
    ;; When running as `java -jar ...`
    (-> (eval 'weather.core) .getPackage .getImplementationVersion)))
