(ns adventofcode.high-entropy-passphrases-test
  (:require [adventofcode.high-entropy-passphrases :as sut]
            [clojure.test :as ct]))

(ct/deftest solve-test

  (ct/are [x y] (= x (sut/solve y))
    1 "aa bb cc dd ee"
    2 "aa bb cc dd ee
       aa bb cc dd ee ff"
    1 "aa bb cc dd ee
       aa bb cc dd ee aa"
    2 "aa bb cc dd ee
       aa bb cc dd ee aaa"))
