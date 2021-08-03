(defproject rickmoynihan/nrebl.middleware "0.3.2-EDEN-SNAPSHOT"
  :description "An nREPL middleware for capturing and browsing data in REBL."
  :url "https://github.com/rynkowsg/nrebl.middleware"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :profiles {:provided   {:dependencies [[org.clojure/clojure "1.10.3"]
                                         [org.clojure/core.async "1.3.618"]
                                         [com.cognitect/rebl "0.9.242"]]}

             ;; example of profiles needed to use REPL
             :rebl-8     {:dependencies [[org.clojure/core.async "1.3.618"]
                                         ;; deps for file datafication (REBL 0.9.149 or later)
                                         [org.clojure/data.csv "1.0.0"]
                                         [org.clojure/data.json "2.4.0"]
                                         [org.yaml/snakeyaml "1.29"]
                                         ;; assumes you've installed the latest Cognitect dev-tools:
                                         [com.cognitect/rebl "0.9.242"]]}
             :rebl-11    {:dependencies [[org.clojure/core.async "1.3.618"]
                                         ;; deps for file datafication (REBL 0.9.149 or later)
                                         [org.clojure/data.csv "1.0.0"]
                                         [org.clojure/data.json "2.4.0"]
                                         [org.yaml/snakeyaml "1.29"]
                                         ;; assumes you've installed the latest Cognitect dev-tools:
                                         [com.cognitect/rebl "0.9.242"]
                                         ;; JavaFX deps
                                         [org.openjfx/javafx-fxml "16"]
                                         [org.openjfx/javafx-controls "16"]
                                         [org.openjfx/javafx-swing "16"]
                                         [org.openjfx/javafx-base "16"]
                                         [org.openjfx/javafx-web "16"]]}

             ;; example of config to use the middleware
             :nrepl-rebl {:repl-options {:nrepl-middleware [nrebl.middleware/wrap-nrebl]}}}

  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :min-lein-version "2.9.0")                                ;; nrepl 0.6.0
