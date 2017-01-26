;; Advanced ClojureScript Concepts
;; ===============================


;; -------------------------------
;; -------------------------------
;; Functional programming concepts
;; -------------------------------
;; -------------------------------


;; -------------------
;; -------------------
;; Loops and iteration
;; -------------------
;; -------------------

;; In ClojureScript, the pattern of iteration is overwhelmingly used for
;; the execution of side effects. There are many functions for iteration,
;; some like `loop`, `recur`, `for`, and `doall` have specific return values
;; we can control, but there are others like `doseq` and `dotimes` explicitly
;; return `nil`.

;; ------------------------
;; Loop and recur functions
;; ------------------------

;; Let's translate a typical JavaScript loop:

;; x = [1, 2, 3, 4, 5];
;; for (var i = 0; i < x.length; i++) {
;;   console.log(x[i]);
;; };

;; In ClojureScript, we might write it like this:

(def x [1 2 3 4 5])
(loop [i 0]
  (println (nth x i))
  (when (< (inc i) (count x))
    (recur (inc i))))

;; While this works and will compile, it's not idiomatic in the least.
;; In ClojureScript, we aim for functional programming over imperative.

;; Now, for a functional approach:

(def x1 [1 2 3 4 5])
(defn looper
  [i]
  (println (nth x1 i))
  (when (< (inc i) (count x1))
    (recur (inc i))))

(looper 0)

;; ---
;; for
;; ---

;; Let's look at an example of iteration without mutation in JavaScript:

;; x = [1, 2, 3, 4, 5]
;; for (var i in x) {
;;   console.log(x[i]);
;; };

;; Now, in ClojureScript:

(def x2 [1 2 3 4 5])
(for [i x]
  (println i))

;; The `for` function takes up to three modifiers: `:when`, `:let`, and `:while`
;; The `:let` allows you to bind additional local variables within the `for`
;; block, `:when` only executes the body when the predicate matches
;; (and continues to iterate), and `:while` terminates iteration when the
;; provided predicate function returns `false`:

(def x3 [1 2 3 4 5])
(for [i x3
      :let [y (* i 2)]
      :when (odd? i)
      :while (< i 4)]
  (println i y))
; => 1 2
; => 3 6
; ==> nil

;; -------
;; dotimes
;; -------

;; We could even do away with the original array using `dotimes`, which
;; will execute a body of code `n` times (presumably for side effects):

(dotimes [n 5] (println (inc n)))

;; -----
;; doseq
;; -----

;; `doseq` differentiates itself from other iteration methods in that it
;; operates on a provided sequence, rather than on an integer:

(doseq [a (range 5)]
  (println a))

;; `doseq` also accepts binding multiple seqs at once and operates over a
;; Cartesian cross of their values - in other words, it executes once for
;; each possible sequence value combination:

(doseq [a ["a" "b" "c"]
        b (range 3)]
  (println a b))
; => a 0
; => a 1
; => a 2
; => b 0
; => b 1
; => b 2
; => c 0
; => c 1
; => c 2
; ==> nil

;; -----
;; doall
;; -----

;; Wile the purpose of `doseq` is to hold in memory (and invoke side effects)
;; for one element in a sequence at a time, the purpose of `doall` is the
;; opposite. `doall` seeks to realize every element (and, consequently,
;; invoke every side effect), load it into memory, and return it.

;; For us new developers, we have to be careful when working with lazy
;; evaluation, and can find ourselves confused by discovering our code
;; hasn't evaluated. If in this situation (and know that the collection
;; or sequence isn't too large to fit into memory), try calling `doall`
;; on it to see if it evaluates.

(def x [1 2 3 4 5])
(do
  (map println x)
  true)

;; Because `map` is evaluated lazily, the `do` block returns `true` but
;; never calls `println` on the individual elements of x. If we want
;; to force the `map println` to evaluate, we could call `doall`:

(do
  (doall (map println x))
  true)
; => 1
; => 2
; => 3
; => 4
; => 5
; ==> true

;; ----------------------
;; ----------------------
;; Higher-order functions
;; ----------------------
;; ----------------------

;; Higher-order functions are idiomatic due to the immutability of the
;; language's core data structures. We use higher-order functions
;; to perform transformations on the input sequence and return new,
;; transformed sequences for us to use.

;; Higher-order functions are valued for their simplicity and elegance,
;; and because they express intent better than `for` et al.

;; ---
;; map
;; ---

;; `map` takes a collection and returns a single new, transformed collection.
;; The provided function is called on each member of the collection,
;; and in the event that `map` is passed zero collections `map` returns
;; a transducer.

;; `map` can take one or more collections, in which case it returns a lazy
;; sequence consisting of the result of applying the input function to the
;; first items in each provided collection, followed by the result of
;; applying the input function to the second items in each collection, etc,
;; until one of the collections has been exhausted.

(map println [1 2 3 4 5])
; => 1
; => 2
; => 3
; => 4
; => 5
; ==> (nil nil nil nil nil)

(map inc [1 2 3 4 5])
; => (2 3 4 5 6)

(map #(+ % 1) [1 2 3 4 5])
; => (2 3 4 5 6)

(map * [1 2 3 4] [2 5 8])
; => (2 10 24)

(map identity {:a 1 :b 2 :c 3})
; => ([:a 1] [:b 2] [:c 3])

;; ---------------
;; filter / remove
;; ---------------

(filter even? [1 2 3 4])
; => (2 4)

(filter #(<= % 2) [1 2 3 4 5])
; => (1 2)

;; IMPORTANT: As `map` and `filter` are lazy, the functions you provide to them
;;            should be free of side effects!

;; `filter` has a twin - `remove` - it removes any items in the input
;; collection for which the predicate evaluates as truthy:

(remove even? [1 2 3 4 5])
; => (1 3 5)

(remove #(<= 2) [1 2 3 4 5])
; => (3 4 5)

;; ------
;; reduce
;; ------

;; `reduce` incrementally builds a new value up from a collection.
;; It takes as its arguments a reducer function, an optional initial value,
;; and a collection. The supplied reducing function must be able to accept two
;; args: the first, the value being reduced into, and the second, the current
;; value to reduce.

;; `reduce` behaves quite differently depending on whether it's been provided
;; an optional initial value

;; If no initial value is supplied, `reduce` returns the result of applying
;; the input function to the first two arguments in the collection,
;; then applying the function to that result and the third item, and so forth.

(reduce + [1 2 3 4 5])
; 1 + 2
; 3 + 3
; 6 + 4
; 10 + 5
; ==> 15

;; If the collection has only one item, it is returned and the supplied function
;; never called:

(reduce (.abs js/Math) [-5])
; => -5

;; If the collection has no items and an initial value is supplied, `reduce`
;; just returns the initial value without calling the supplied function:

(reduce str 10 [])
; => 10

;; -----------
;; -----------
;; Transducers
;; -----------
;; -----------

;; Transducers are a relatively new addition to ClojureScript, and a fairly
;; advanced functional programming concept. Essentially, a transducer is a
;; function that takes one reducing function and returns another.

;; In this context, a "reducing function" is a function that can be passed
;; to `reduce`.

;; Transducers in general are not an intuitive thing to reason about, so
;; the best thing to do at first is look at examples:

;; NOTE: By convention, transducers are referred to as `xform`

(sequence [1 2 3 4 5 6])
; => (1 2 3 4 5 6)

;; When calling `sequence` with just a collection, you can provide a
;; transducer to it, and it'll generate a sequence with the transformation
;; applied. As an example, here is a simple transducer, `(map inc)`

(sequence (map inc) [1 2 3 4 5])
; => (2 3 4 5 6)

;; The `->>` macro functions much like the preceding:
(->> [1 2 3 4 5] (map inc))

;; The latter is a little different though because the type being returned is a
;; `LazySeq`, whereas the use of a transducer causes the return type to be
;; a `LazyTransformer`.

;; TRANSDUCERS REPRESENT TRANSFORMATIONS ON DATA!!

;; The most important differentiating characteristics of transducers are that
;; they don't care about the source of inputs - other higher-order functions
;; expect to be tied to the collection protocol.

;; A great use of transducers is applying a transformation to everything that
;; goes through a `core.async` channel:

(ns experiment.async
  (:require [cljs.core.async :as async])
  (:require-macros [cljs.core.async.macros :as async-macros]))

(defn sample-transducer-channel
  "A simple example of a transduced channel. Increment by one
  all values that pass through this channel. For demonstration
  purposes we'll just hard-code the number 5 for now."
  []
  (let [c (async/chan 1 (map inc))]
    (async-macros/go (async/>! c 5))
    (async-macros/go (.log js/console (async/<! c)))))

;; Transducers can be used for reducing with a transformation:

(def inc-xfrom (map inc))

(transduce inc-xform + 0 [1 3 5])
; => 12
;; The order here is equivalent to:
;; (reduce + 0 (apply inc (1 3 5)))

(into [] inc-xform '(1 1 2 3 5 8))
; => [2 2 3 4 6 9]


;; ------------
;; ------------
;; Control flow
;; ------------
;; ------------

;; ---------
;; if / when
;; ---------

;; `if` is a special form rather than a function or macro - it takes a
;; predicate, a form that is evaluated and yielded if the predicate
;; returns `true`, and an optional form that is evaluated and yielded if
;; the predicate returns `false`

(if (= 1 1)
  "One equals one!"
  "One does not equal one :(")

(if (= 1 2)
  "One equals two!") ;; implicit nil return value when false

;; `when` is a combination of `if` and an implicit `do` block if the predicate
;; returns `true`:

(when true
  (println "I'm a side effect!")
  ["apples" "bananas"])
; => "I'm a side effect!"
; ==> ["apples", "bananas"]

;; -----------------
;; if-let / when-let
;; -----------------

;; When you need to bind return values to a local variable and continue to
;; evaluate a code block with that local variable:

(if-let [x false]
  "I'm true!"
  "I'm false!")
; => "I'm false!"

(when-let [x "true"]
  (println "Oh hi!")
  x)
; => Oh hi!
; ==> "true"

;; ------------
;; cond / condp
;; ------------

;; Both of these functions are used to run through many possible predicates and
;; to return the body associated with the first to evaluate as truthy (i.e.,
;; think conditional / ternary operator in JavaScript for comparison)

(cond
  false "Nope"
  nil "Not happening."
  :else "I'm a default value!")
; => "I'm a default value!"

;; If none of the predicates evaluate to `true`, `cond` returns `nil`.
;; Calling `cond` by itself, as an example, returns `nil`.

;; `condp` is similar, but instead of evaluating a different predicate
;; at each tier, it takes a predicate and an initial value and compares the
;; result agains various possible values. It takes a single optional default,
;; that - unlike `cond` - does not need an accompanying `:else` or similar
;; truthy value preceding it):

(condp = [1 2 3 4 5]
  "a string?" false
  'another-value false
  :keyword false
  [1 2 3 4 5] true
  "finally, a single default value")
; => true

;; ----
;; case
;; ----

;; `case` is a special case of `condp` where the predicate is `=`

(case [1 2]
  [] "empty vec"
  (vec '(1 2)) "my vec"
  "default")
; => "my vec"

;; You can include any number of test constants that evaluate to the same
;; result, for instance:

(case [1 2]
  [] "empty vec"
  ([1 2] [3 4]) "my vec"
  "default")
; => "my vec"

;; IMPORTANT: In general, use `cond` or `condp` over `case`!


;; ------------------
;; ------------------
;; Exception handling
;; ------------------
;; ------------------


;; Nothing unexpected here, really....

(throw (js/Error. "Warning: An error occurred!"))

(try
  (throw (js/Error. "I'm an error!"))
  (catch js/Error e
    (println "Error message: " e))
  (finally
    (println "A successful result!")))
; => Error message: #object[Error Error: I'm an error!]
; => A successful result!
; ==> nil

;; Sometimes we need more general exception handling:

(try
  (throw "Exception")
  (catch js/Error err
    ;; whatever error handling you might want
    (println "error was of type js/Error"))
  (catch :default err
    ;; perhaps some more general error handling here
    (println "Some non-error type was thrown"))
  (finally
    (println "Done!")))
; => Some non-error type was thrown.
; => Done!
; ==> nil


;; --------------------------------
;; --------------------------------
;; Writing macros for ClojureScript
;; --------------------------------
;; --------------------------------
