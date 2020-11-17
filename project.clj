(defproject weather "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [cyrus/dovetail "0.3.0"]
                 [cheshire "5.10.0"]
                 [functionalbytes/mount-lite "2.1.5"]
                 [cyrus/config "0.3.1"]
                 [aleph "0.4.6"]
                 [compojure "1.6.2"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.0"]
                 [amalloy/ring-gzip-middleware "0.1.4"]
                 [overtone/at-at "1.2.0"]
                 [org.zalando/swagger1st "0.27.0" :exclusions [org.flatland/ordered]]
                 [clj-http "3.10.3"]
                 [org.flatland/ordered "1.5.9"]]
  :main ^:skip-aot weather.core
  :target-path "target/%s"
  :uberjar-name "weather.jar"
  :manifest {"Implementation-Version" ~#(:version %)}
  :plugins [[lein-cloverage "1.0.13"]
            [lein-set-version "0.4.1"]
            [lein-ancient "0.6.15"]]
  :profiles {:uberjar {:aot :all}
             :dev     {:repl-options   {:init-ns user}
                       :source-paths   ["dev"]
                       :resource-paths ["test/resources"]
                       :dependencies   [[org.clojure/tools.namespace "0.2.11"]
                                        [org.clojure/java.classpath "0.3.0"]]}})
