(ns adventofcode.knot-hash
  (:require [clojure.string :as string]
            [clojure.spec.alpha :as s]))

(defn- subvec-to-be-reversed
  "Extract subvector from circular vector of integers starting from current position and of desired length."
  [xs current-position length]
  (let [xs (vec (concat xs xs))]
    (subvec xs current-position (+ current-position length))))

(defn- encryption-step
  "Main encryption step:

    * Extract subvector of desired length starting from current position
    * Reverse subvector
    * Construct result preserving circular structure:
      * tail of reversed part become head of result `1, 2), 3, ([4], 5`
      * reversed part is in the middle of resulted vector."
  [list-to-encrypt current-position length]
  (let [reversed-part (vec (reverse (subvec-to-be-reversed list-to-encrypt current-position length)))]
    (if (>= (+ current-position length) (count list-to-encrypt))
      (concat (subvec reversed-part (- (count list-to-encrypt) current-position))
              (subvec list-to-encrypt (- (+ current-position length) (count list-to-encrypt)) current-position)
              (subvec reversed-part 0 (- (count list-to-encrypt) current-position)))

      (concat (subvec list-to-encrypt 0 current-position)
              reversed-part
              (subvec list-to-encrypt (+ current-position length))))))

(defn- new-position
  "Calculate next current-position in circular vector."
  [current-position salt-element skip-size encryption-base]
  (let [position (+ current-position salt-element skip-size)]
    (if (>= position encryption-base)
      (mod position encryption-base)
      position)))

(defn- string->codes
  "Convert string into vector of ASCII characters."
  [st]
  (apply concat (repeat 64
                        (concat (map int (string/trim st))
                                '(17 31 73 47 23)))))

(defn- solve* [lengths]
  (let [encryption-base 256]
    (loop [list-to-encrypt (vec (range encryption-base))
           salt-element (first lengths)
           salt (rest lengths)
           current-position 0
           skip-size 0]
      (if salt-element
        (let [list-to-encrypt (vec (encryption-step list-to-encrypt current-position salt-element))
              next-position (new-position current-position
                                          salt-element
                                          skip-size
                                          encryption-base)]
          (recur list-to-encrypt
                 (first salt)
                 (rest salt)
                 next-position
                 (inc skip-size)))
        list-to-encrypt))))

(defn solve
  "This hash function simulates tying a knot in a circle of string with 256 marks on it. Based on the input to be hashed, the function repeatedly selects a span of string, brings the ends together, and gives the span a half-twist to reverse the order of the marks within it. After doing this many times, the order of the marks is used to build the resulting hash.

  ```
    4--5   pinch   4  5           4   1
   /   \\  5,0,1  /\\/\\  twist  /\\ /\\
  3      0  -->  3      0  -->  3   X   0
  \\    /        \\ /\\/        \\ /\\ /
    2--1           2  1           2   5
  ```

  To achieve this, begin with a list of numbers from 0 to 255, a current position which begins at 0 (the first element in the list), a skip size (which starts at 0), and a sequence of lengths. Then, for each length:

    - Reverse the order of that length of elements in the list, starting with the element at the current position.
    - Move the current position forward by that length plus the skip size.
    - Increase the skip size by one.
    - The list is circular; if the current position and the length try to reverse elements beyond the end of the list, the operation reverses using as many extra elements as it needs from the front of the list. If the current position moves past the end of the list, it wraps around to the front. Lengths larger than the size of the list are invalid."
  [lengths]
  (apply * (take 2 (solve* lengths))))

(s/fdef solve
  :args (s/cat :vector-of-integers
               (s/coll-of (s/int-in 0 255) :kind vector?))
  :ret (s/and integer? #(>= % 0) #(<= % (* 255 254)))
  :fn #(solve (-> % :args :vector-of-integers)))

(defn solve-part-2
  "input should be taken not as a list of numbers, but as a string of bytes instead. Unless otherwise specified, convert characters to bytes using their ASCII codes. This will allow you to handle arbitrary ASCII strings, and it also ensures that your input lengths are never larger than 255. For example, if you are given 1,2,3, you should convert it to the ASCII codes for each character: 49,44,50,44,51.

  Once you have determined the sequence of lengths to use, add the following lengths to the end of the sequence: 17, 31, 73, 47, 23. For example, if you are given 1,2,3, your final sequence of lengths should be 49,44,50,44,51,17,31,73,47,23 (the ASCII codes from the input string combined with the standard length suffix values).

  Second, instead of merely running one round like you did above, run a total of 64 rounds, using the same length sequence in each round. The current position and skip size should be preserved between rounds. For example, if the previous example was your first round, you would start your second round with the same length sequence (3, 4, 1, 5, 17, 31, 73, 47, 23, now assuming they came from ASCII codes and include the suffix), but start with the previous round's current position (4) and skip size (4).

  Once the rounds are complete, you will be left with the numbers from 0 to 255 in some order, called the sparse hash. Your next task is to reduce these to a list of only 16 numbers called the dense hash. To do this, use numeric bitwise XOR to combine each consecutive block of 16 numbers in the sparse hash (there are 16 such blocks in a list of 256 numbers). So, the first element in the dense hash is the first sixteen elements of the sparse hash XOR'd together, the second element in the dense hash is the second sixteen elements of the sparse hash XOR'd together, etc.

  The standard way to represent a Knot Hash is as a single hexadecimal string; the final output is the dense hash in hexadecimal notation. Because each number in your dense hash will be between 0 and 255 (inclusive), always represent each number as two hexadecimal digits (including a leading zero as necessary).

  Examples:

    - The empty string becomes `a2582a3a0e66e6e86e3812dcb672a272`.
    - `AoC 2017` becomes `33efeb34ea91902bb2f59c9920caa6cd`.
    - `1,2,3` becomes `3efbe78a8d82f29979031a4aa0b16a9d`.
    - `1,2,4` becomes `63960835bcdc130f0b66d7ff4f6a5a8e`."
  [lengths]
  (apply str
         (->> lengths
             string->codes
             solve*
             (partition 16)
             (map #(apply bit-xor %))
             (map #(format "%02x" %)))))

(s/fdef solve-part-2
  :args (s/cat :string string?)
  :ret (s/and string? #(re-matches #"[0-9a-z]{32}" %))
  :fn #(solve-part-2 (-> % :args :string)))
