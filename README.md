# nrebl.middleware

[![Clojars Project](https://img.shields.io/clojars/v/com.github.rynkowsg/nrebl.middleware.svg)](https://clojars.org/com.github.rynkowsg/nrebl.middleware)

The start of an nREPL middleware that will spy on an nREPL connection
and capture the results of evaluation for browsing in
[REBL](https://github.com/cognitect-labs/REBL-distro).

**NOTE: REBL requires a commercial license if it's to be used for commercial work**

## Mechanism

- When middleware is installed and nREPL starts, it launches REBL window automatically.
- More windows can be launched using `(cognitect.rebl/ui)` (when namespace is required) or
  `((requiring-resolve,'cognitect.rebl/ui))`.
- During the nREPL server activity, all the forms sent to nREPL server will be sent also to REBL.
- The only forms that are not sent to REBL are Cursive's forms that are filtered out.

## Installation

### Prerequisites

1. Install [clojure](https://clojure.org/)
2. Install [Cognitect dev-tools](https://cognitect.com/dev-tools/) (contains REBL).
3. Include some aliases either in your `~/.clojure/deps.edn` or your projects `deps.edn`
   respectively

### In a nutshell

1. Add to your dependencies the latest version:

    | leiningen | deps.toosls |
    --- | ---
    | `[com.github.rynkowsg/nrebl.middleware "0.4.0"]` | `com.github.rynkowsg/nrebl.middleware {:mvn/version "0.4.0"}` |

2. Add nREPL middleware:

    | leiningen | deps.toosls |
    --- | ---
    | `:repl-options {:nrepl-middleware [nrebl.middleware/wrap-nrebl]}` | `--middleware [nrebl.middleware/wrap-nrebl]` |

### More on this middleware and REBL installation

Below examples shows how to install the middleware as well as how to run REBL.
The `rebl-8` and `rebl-11` aliases/profiles are for running either on Oracle's JDK 8
(which bundles JavaFX) or OpenJDK 11 (which doesn't bundle JavaFX so we include them).<br>
Rest of libraries (`core.async`, `data.csv`, `data.json`, `snakeyaml`) are optional
but it is recommended to include them to support datafication
([REBL docs](https://docs.datomic.com/cloud/other-tools/REBL.html#datafied-files)).

#### deps.tools

Add the following to either project's `deps.edn` or home `~/.clojure/deps.edn`:
```clojure
;; deps.edn
{
 ;; ...
 :aliases {;; rebl-8 & rebl-11 are only an example aliases, showing how REBL could be aliased
           :rebl-8                                          ;; only Oracle JDK 1.8
           {:extra-deps {org.clojure/core.async {:mvn/version "1.3.618"}
                         ;; deps for file datafication (REBL 0.9.149 or later)
                         org.clojure/data.csv   {:mvn/version "1.0.0"}
                         org.clojure/data.json  {:mvn/version "2.4.0"}
                         org.yaml/snakeyaml     {:mvn/version "1.29"}
                         ;; assumes you've installed the latest Cognitect dev-tools:
                         com.cognitect/rebl     {:mvn/version "0.9.242"}}
            :main-opts  ["-m" "cognitect.rebl"]}
           :rebl-11
           {:extra-deps {org.clojure/core.async      {:mvn/version "1.3.618"}
                         ;; deps for file datafication (REBL 0.9.149 or later)
                         org.clojure/data.csv        {:mvn/version "1.0.0"}
                         org.clojure/data.json       {:mvn/version "2.4.0"}
                         org.yaml/snakeyaml          {:mvn/version "1.29"}
                         ;; assumes you've installed the latest Cognitect dev-tools:
                         com.cognitect/rebl          {:mvn/version "0.9.242"}
                         ;; JavaFX deps
                         org.openjfx/javafx-fxml     {:mvn/version "16"}
                         org.openjfx/javafx-controls {:mvn/version "16"}
                         org.openjfx/javafx-swing    {:mvn/version "16"}
                         org.openjfx/javafx-base     {:mvn/version "16"}
                         org.openjfx/javafx-web      {:mvn/version "16"}}
            :main-opts  ["-m" "cognitect.rebl"]}

           ;; this alias' :main-opts shows how to use this nREPL middleware
           :nrepl-rebl
           {:extra-deps {nrepl/nrepl                          {:mvn/version "0.8.3"}
                         com.github.rynkowsg/nrebl.middleware {:mvn/version "0.4.0"}}
            :main-opts  ["-m" "nrepl.cmdline" "-i" "--middleware" "[nrebl.middleware/wrap-nrebl]"]}}
 ;; ...
 }
```

To run nREPL with REBL on JVM 1.8 (only Oracle JDK 1.8)
```shell
clj -M:rebl-8:nrepl-rebl
```
To run nREPL with REBL on JVM 11:
```shell
clj -M:rebl-11:nrepl-rebl
```

#### Leiningen

Add the following to either project's `project.clj` or home `~/.lein/profiles.clj`:
```clojure
;; project.clj
;; ...
:profiles {;; ....
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
           :nrepl-rebl {:dependencies [[com.github.rynkowsg/nrebl.middleware "0.4.0"]]
                        :repl-options {:nrepl-middleware [nrebl.middleware/wrap-nrebl]}}
           ;; ...
           }
;; ...
```
To run nREPL with REBL on JVM 1.8 (only Oracle JDK 1.8)
```shell
lein with-profile default,rebl-8,nrepl-rebl repl
```
To run nREPL with REBL on JVM 11:
```shell
lein with-profile default,rebl-11,nrepl-rebl repl
```

## Links

- https://github.com/cognitect-labs/REBL-distro
- https://docs.datomic.com/cloud/other-tools/REBL.html
- https://cognitect.com/dev-tools/

## License

Copyright © 2018-2021 Rick Moynihan, Eden Ferreira, Grzegorz Rynkowski

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
