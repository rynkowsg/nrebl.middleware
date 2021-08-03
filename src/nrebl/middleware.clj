(ns nrebl.middleware
  (:require
   [clojure.string :as str]
   [nrepl.middleware :refer [set-descriptor!]]
   [nrepl.middleware.print :refer [wrap-print]])
  (:import
   [nrepl.transport Transport]))

;; ----- require REBL fns -------------

(defn ^:private require-fn
  [sym]
  (try
    (requiring-resolve sym)
    (catch Exception _
      (throw "REBL not available"))))

(def ^:private rebl-ui (require-fn 'cognitect.rebl/ui))
(def ^:private rebl-submit (require-fn 'cognitect.rebl/submit))

;; --------- cursive check ------------

(def ^:private cursive-commands
  ["(binding [*print-meta* true] (pr-str (cursive.riddley/macroexpand-all"
   "(cursive.repl"
   "(clojure.core/with-redefs [clojure.test/do-report (clojure.core/fn"
   "(try (clojure.lang.Compiler/load (java.io.StringReader. ((clojure.core/deref"
   "(do (clojure.core/println (clojure.core/str \"Clojure \" (clojure.core/clojure-version)))"
   "(get *compiler-options* :disable-locals-clearing)"
   "(do (do (do (clojure.core/println (clojure.core/str"])

(defn ->bool [p] (if p true false))

(defn cursive?
  "Takes an nREPL request and returns true if a noisy cursive eval request."
  [{:keys [code op] :as _request}]
  (->bool (and (= op "eval")
               (some #(str/starts-with? code %) cursive-commands))))

;; -------- nrepl middleware ----------

(defn send-to-rebl!
  [{:keys [code] :as _req} {:keys [value] :as resp}]
  (when (and code (contains? resp :value))
    (rebl-submit (read-string code) value))
  resp)

(defn- wrap-rebl-sender
  "Wraps a `Transport` with code which prints the value of messages sent to
  it using the provided function."
  [{:keys [transport] :as request}]
  (reify Transport
    (recv [_]
      (.recv transport))
    (recv [_ timeout]
      (.recv transport timeout))
    (send [this resp]
      (.send transport (if (cursive? request) resp (send-to-rebl! request resp)))
      this)))

(defn wrap-nrebl
  [handler]
  (fn [{:keys [op] :as request}]
    (if (= op "start-rebl-ui")
      (rebl-ui)
      (handler (assoc request :transport (wrap-rebl-sender request))))))

(set-descriptor! #'wrap-nrebl
                 {:requires #{#'wrap-print}
                  :expects  #{"eval"}
                  :handles  {"start-rebl-ui" "Launch the REBL inspector and have it capture interactions over nREPL"}})

;; ----------- comments ---------------

(comment
 (require '[nrepl.core :as nrepl]
          '[nrepl.server :as ser])

 ;; start nREPL server
 (def nrep (ser/start-server :port 55808 :handler (ser/default-handler #'wrap-nrebl)))
 ;; stop nREPL server
 (ser/stop-server nrep)

 ;; send "start-rebl-ui" to nREPL
 (with-open [conn (nrepl/connect :port 55808)]
   (-> (nrepl/client conn 1000)                             ; message receive timeout required
       (nrepl/message {:op "start-rebl-ui"})
       nrepl/response-values))

 ;; send sample form to nREPL
 (with-open [conn (nrepl/connect :port 55808)]
   (-> (nrepl/client conn 1000)                             ; message receive timeout required
       ;(nrepl/message {:op "inspect-nrebl" :code "[1 2 3 4 5 6 7 8 9 10 {:a :b :c :d :e #{5 6 7 8 9 10}}]"})
       (nrepl/message {:op "eval" :code "(do {:a :b :c [1 2 3 4] :d #{5 6 7 8} :e (range 20)})"})
       nrepl/response-values)))
