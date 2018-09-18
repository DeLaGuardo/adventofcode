(ns adventofcode.knot-hash-test
  (:require [adventofcode.knot-hash :as sut]
            [clojure.spec.alpha :as s]
            [clojure.test :as ct]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]))

(defspec result-is-positive-integer-in-desired-range
  100
  (prop/for-all [x (s/gen (s/coll-of (s/int-in 0 255)))]
                (ct/is (<= 0 (sut/solve x) (* 255 254)))))

(ct/deftest solve-knot-hash

  (ct/are [x y] (= x (sut/solve y))
    2 [3 4 1 5]
    64770 [128 256]
    64770 [256 0]
    0 [0]
    23715 [94 84 0 79 2 27 81 1 123 93 218 23 103 255 254 243]))
