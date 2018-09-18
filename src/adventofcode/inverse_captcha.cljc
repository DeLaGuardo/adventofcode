(ns adventofcode.inverse-captcha
  (:require [clojure.spec.alpha :as s]))

(defn- append-cyclic-part
  "Turn a string into `cyclic` buffer."
  [captcha step]
  (let [cnt (count captcha)
        lidx (range cnt)
        ridx (concat (range step cnt)
                     (range step))]
    (map #(list (nth captcha %1)
                (nth captcha %2))
         lidx
         ridx)))

(defn solve
  "Find the sum of all digits that match the next digit in the list.
  The list is circular, so the digit after the last digit is the first digit in the list.

  For example:

    * `1122` produces a sum of `3` (`1` + `2`) because the first digit (`1`) matches the second digit and the third digit (`2`) matches the fourth digit.
    * `1111` produces `4` because each digit (all `1`) matches the next.
    * `1234` produces `0` because no digit matches the next.
    * `91212129` produces `9` because the only digit that matches the next one is the last digit, `9`."
  [captcha]
  (->> (append-cyclic-part captcha 1)
      (filter #(= (first %) (second %)))
      (map #(Integer/parseInt (str (first %))))
      (reduce + 0)))

(s/fdef solve
  :args (s/cat :string-of-numbers
               (s/and string? #(re-matches #"[0-9]*" %)))
  :ret pos-int?
  :fn #(= (solve (-> % :args :string-of-numbers))
          (-> % :ret)))

(defn solve-part-2
  "Same as `solve` but now `next` means `halfway around` the circular list.

  For example:

    * `1212` produces `6`: the list contains `4` items, and all four digits match the digit `2` items ahead.
    * `1221` produces `0`, because every comparison is between a `1` and a `2`.
    * `123425` produces `4`, because both `2`s match each other, but no other digit has a match.
    * `123123` produces `12`.
    * `12131415` produces `4`."
  [captcha]
  (->> (append-cyclic-part captcha (int (/ (count captcha) 2)))
      (filter #(= (first %) (second %)))
      (map #(Integer/parseInt (str (first %))))
      (reduce + 0)))

(s/fdef solve-part-2
  :args (s/cat :string-of-numbers
               (s/and string? #(re-matches #"[0-9]*" %) #(even? (count %))))
  :ret pos-int?
  :fn #(= (solve-part-2 (-> % :args :string-of-numbers))
          (-> % :ret)))
