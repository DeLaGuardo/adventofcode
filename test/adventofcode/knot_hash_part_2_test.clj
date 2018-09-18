(ns adventofcode.knot-hash-part-2-test
  (:require [adventofcode.knot-hash :as sut]
            [clojure.spec.alpha :as s]
            [clojure.test :as ct]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]))

(defspec result-is-hex-string-of-32-characters
  100
  (prop/for-all [x (s/gen string?)]
                (let [r (sut/solve-part-2 x)]
                  (ct/is (string? r))
                  (ct/is (= 32 (count r)))
                  (ct/is (re-matches #"[0-9a-z]+" r)))))

(ct/deftest solve-knot-hash

  (ct/are [x y] (= x (sut/solve-part-2 y))
    "a2582a3a0e66e6e86e3812dcb672a272" ""
    "33efeb34ea91902bb2f59c9920caa6cd" "AoC 2017"
    "3efbe78a8d82f29979031a4aa0b16a9d" "1,2,3"
    "63960835bcdc130f0b66d7ff4f6a5a8e" "1,2,4"
    "541dc3180fd4b72881e39cf925a50253" "94,84,0,79,2,27,81,1,123,93,218,23,103,255,254,243"))

(ct/deftest solve-knot-hash-with-trailing-spaces

  (ct/are [x] (= "3efbe78a8d82f29979031a4aa0b16a9d" (sut/solve-part-2 x))
    "1,2,3"
    " 1,2,3"
    "1,2,3 "
    " 1,2,3 "))
