(ns adventofcode.high-entropy-passphrases
  (:require [clojure.string :as string]
            [clojure.spec.alpha :as s]))

(defn- split-into-words
  "Split a text into separate into vector of passphrases.
  Each passphrase is splited by space character into individual words."
  [passphrases]
  (->> passphrases
      string/split-lines
      (map string/trim)
      (map #(string/split % (re-pattern " ")))))

(defn- valid-passphrase?
  "Check passphrase validity by
  comparing amount of words in passphrase with amount of distinct words in the same passphrase"
  [passphrase]
  (= (count passphrase)
     (count (distinct passphrase))))

(defn- has-same-word?
  "Checks if word appears as an anagrams in passphrase."
  [word passphrase]
  (some (fn [w]
          (if (= w word) ;; The same word, skip.
            false
            (= (sort w)
               (sort word))))
        passphrase))

(defn- valid-passphrase-part-2?
  "Check passphrase validity by
  find any word which letters can be rearranged to form any other word in the passphrase."
  [passphrase]
  (and (valid-passphrase? passphrase)
       (not (some #(has-same-word? % passphrase)
                  passphrase))))

(defn solve
  "Count how many passphrases are valid.

  Valid passphrase must contain no duplicate words.

  For example:

    - `aa bb cc dd ee` is valid.
    - `aa bb cc dd aa` is not valid - the word aa appears more than once.
    - `aa bb cc dd aaa` is valid - aa and aaa count as different words."
  [passphrases]
  (->> passphrases
      split-into-words
      (filter valid-passphrase?)
      count))

(defn solve-part-2
  "Count how many passphrases are valid.

  Valid passphrase must contain no two words that are anagrams of each other - that is, a passphrase is invalid if any word's letters can be rearranged to form any other word in the passphrase.

  For example:

    - `abcde fghij` is a valid passphrase.
    - `abcde xyz ecdab` is not valid - the letters from the third word can be rearranged to form the first word.
    - `a ab abc abd abf abj` is a valid passphrase, because all letters need to be used when forming another word.
    - `iiii oiii ooii oooi oooo` is valid.
    - `oiii ioii iioi iiio` is not valid - any of these words can be rearranged to form any other word."
  [passphrases]
  (->> passphrases
      split-into-words
      (filter valid-passphrase-part-2?)
      count))
