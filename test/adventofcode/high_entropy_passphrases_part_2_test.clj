(ns adventofcode.high-entropy-passphrases-part-2-test
  (:require [adventofcode.high-entropy-passphrases :as sut]
            [clojure.test :as ct]))

(ct/deftest solve-part-2-test

  (ct/are [x y] (= x (sut/solve-part-2 y))
    1 "abcde fghij"
    0 "abcde xyz ecdab"
    1 "a ab abc abd abf abj"
    1 "iiii oiii ooii oooi oooo"
    0 "oiii ioii iioi iiio"
    3 "abcde fghij
       abcde xyz ecdab
       a ab abc abd abf abj
       iiii oiii ooii oooi oooo
       oiii ioii iioi iiio"))
