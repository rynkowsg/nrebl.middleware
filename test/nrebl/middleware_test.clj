(ns nrebl.middleware-test
  (:require
   [clojure.test :refer [deftest is]]
   [nrebl.middleware :refer :all]))

(deftest test-cursive?
  (is (not (cursive? {:op "eval" :code "1"})))
  (is (not (cursive? {:op "eval" :code "(+ 1 1)"})))
  (is (not (cursive? {:op "eval" :code "nil"})))
  (is (cursive? {:op "eval" :code "(cursive.repl.runtime/completions 'clojure.pprint)"})))
