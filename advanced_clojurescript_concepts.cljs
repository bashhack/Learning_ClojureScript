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

(def inc-xform (map inc))

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


;; Lisp (including Clojure) macros are core features of the language(s),
;; unique in that that they allow code itself to be transformed and rewritten.

;; --------------
;; --------------
;; `read` / `eval`
;; --------------
;; --------------

;; Lisp is special among languages in that a program's AST (abstract syntax
;; tree) is no different than the data the language operates on.

;; Since we know the data the language operates on is accessible at runtime,
;; we can then know we can access and operate on the AST of the program, too!

;; We can write macros, then, that transform and manipulate both data and code:

;; cljs.user=> (cljs.reader/read-string "(+ 1 3)")
;; => (+ 1 3)

;; Now, we can treat this like any other data structure:

;; cljs.user=> (conj (cljs.reader/read-string "(+ 1 3)") "apples")
;; => ("apples" + 1 3)

;; Now, let's pass a string through the reader to the evaluator:

;; cljs.user=> (eval (cljs.reader/read-string "(+ 1 3)") "apples")
;; => WARNING: Use of undeclared Var cljs.user/eval at line 1...

;; `eval` throws an error here because it is not available during the
;; ClojureScript runtime, though it is available in the JVM Clojure process.

;; It is possible that in the future `eval` might be available to us, as
;; ClojureScript was announced to be a self-compiling language. Self-compiling
;; CLojureScript means it becomes possible to evaluate ClojureScript at runtime.

;; For more info on the the self-hosted version of ClojureScript, check out:
;; `https://github.com/clojure/clojurescript/wiki/Bootstrapping-the-Compiler`

;; Because `eval` isn't available to the current ClojureScript runtime, this
;; directly affects how macros work in ClojureScript. And because `eval` is
;; only available at compile time, vs run time, macros can only be written
;; in a `.clj` (normal Clojure) file or a `.cljc` (reader conditional Clojure)
;; file.

;; What does this really mean? It means that ClojureScript macros need to be
;; evaluated before we call them anywhere. To accomplishthis, we define them
;; in another namespace than the one in which they will be called. They are
;; then imported into the calling namespace using the special `:require-macros`

;; Ex.
(ns my.namespace
  (:require-macros [my.macros :as my]))

;; A ClojureScript namespace can require macros from a namespace with the same
;; name (ex. `my/namespace.cljs` could require macros from `my/namespace.clj`).
;; There's a gotcha, however - if you have a macro and a function with the
;; same name, ClojureScript will resolve the symbol to a macro if it's in a
;; calling position, and to a function if it's not. For example, if `+` were
;; both a macro and a function, it would be a macro in `(+ 1 1)` and a
;; function in `(reduce + [1 1])`. Best advice here, don't get in this
;; particular position!

;; ----------------
;; ----------------
;; Your first macro
;; ----------------
;; ----------------

;; Let's create a namespace (ex. in a file `macros.clj`):

(ns experiment.macros)

;; In another file (ex. `consumers.cljs`), let's add a namespace declaration:

(ns experiment.consumers
  (:require-macros [experiment.macros :as m]))

;; Finally, let's make sure we can access everything in the browser by adding
;; a `:require` section to our main namespace, `experiment.core`:

(ns experiment.core
  (:require [experiment.consumers :as consumers]))

;; Let's build a simple macro:

(defmacro incrementByOne
  "Given a form, increment it by 1 and return"
  [x]
  (+ x 1))

(experiment.macros/incrementByOne 2)
; => 3

(experiment.macros/incrementByOne (+ 1 2))
; => clojure.lang.ExceptionInfo: clojure.lang.PersistentList cannot...

;; Woah! Why did that not work?
;; `defmacro` reveals the full form of the value passed in before
;; the evaluator has a chance to turn the form into its underlying
;; value

;; What opens the door to some seriously powerful macros is syntax-quoting.
;; Let's rewrite the original increment macro so it won't choke when passed
;; a form that needs to be evaluated:

(defmacro incrementByOne
  "Given a form, increment it by 1 and return"
  [x]
  `(+ 1 ~x))

;; Here, the backtick is known as a `syntax-quote` character, the tilde
;; is known as an `unquote` character, and there is a final character `~@`
;; which is the `unquote-splicing` marker.

;; For all forms that are NOT a symbol, list, vector, set, or map -
;; `syntax-quoting` a form is the same as quoting it. Syntax-quoting a symbol
;; resolves the symbol within its current context. If the symbol isn't
;; namespace qualified and ends in a pound or hash, it will resolve to
;; a generated symbol with a unique ID so that all references to that
;; symbol within the larger syntax-quoted expression will resolve to the
;; same generated symbol.

;; Most importantly - `syntax-quoting` a list, vector, set, or map creates
;; a template of the data structure. Within the template, ordinary forms
;; act as if they, too, have been syntax-quoted, but forms can be exempted
;; from this by unquoting or unquote-splicing them.

;; Okay, that's all fine and good - but what does that mean?
;; In our `incremement` example, we've syntax-quoted the form `(+ 1 ~x)` and
;; unquoted `x`. This means that the macro will try to evaluate whatever is
;; passed in as `x` and then replace that result with `x` in the template.
;; In this case, the net effect of all this magic is just to get us back to
;; having our macro behave like a normal function:

(defn incrememnt-func
  "Increment y by 1"
  [y]
  (m/incrementByOne y))

;; cljs.user=> (experiment.consumers/increment-func (+ 1 2))
;; ==> 4

;; ----------------------------
;; ----------------------------
;; Writing more advanced macros
;; ----------------------------
;; ----------------------------

;; Let's create a macro that take a normal function body and replaces the
;; function in the calling position with a provided function. We want to
;; construct a new form with the provided function at the front and then
;; to have the rest of the original body, without its original function.

(defmacro fnswap
  "Replace the form in the calling position of body with the function f,
  evaluate and return"
  [f body]
  `(f ~@(rest body)))

;; Here, we're syntax-quoting the form and returning a new form with our
;; function, unquoted, at the front. We're using the `rest` function to extract
;; the remaining elements for the form that's passed in. The new syntactical
;; element here that we haven't used is the `unquote-splicing` marker - it
;; takes the contents of the form being provided and un-nests them from the
;; form they're in (usually a list) into the form outside of it.

;; Step by step:

`(~f ~@(rest (+ 1 2)))

`(~f ~@(1 2))

`(~f 1 2)

`(- 1 2)
; => -1

;; -----------------------------------
;; -----------------------------------
;; Gensyms and local binding in macros
;; -----------------------------------
;; -----------------------------------

;; Creating local binding within a syntax-quoted block doesn't work the way we
;; might expect - let's investigate:

(defmacro bad-binding
  "An example of how local binding in macros does not work"
  []
  `(let [x 5]
     x))

;; cljs.user=> (experiment.macros/bad-binding)
;; ==> clojure.lang.ExceptionInfo: Invalid local name:...

;; Instead, we need to use a generated symbol, or `gensym`:

(defmacro good-binding
  "An example of how local binding in macros does not work"
  []
  `(let [x# 5]
     x#))

;; cljs.user=> (experiment.macros/good-binding)
;; ==> 5

;; ----------------------
;; ----------------------
;; Don't repeat yourself!
;; ----------------------
;; ----------------------

;; Often, you'll passing as an argument to the macro an entire body of code
;; that you want to be evaluated. Maybe your macro rewrites the body
;; before evaluating it, or maybe it sets some additional local bindings
;; before the body is evaluated, etc. THe key here is to recognize that it's
;; possible to inadvertently end up evaluating the body that is passed in
;; multiple times.

;; This might not be a big deal for some examples, but let's imagine a scenario
;; where we're working on some hypothetical app where we need a macro that does
;; some analytics and logging for database queries or API calls. If we had:

(defmacro db-metrics
  "Analyze and log the query"
  [body]
  `(do
     (analyze ~body)
     (log ~body)))

;; We might use it like:

(defn store-data
  "Write data to our data store"
  [data]
  (db-metrics
   (db/store data)))

;; Oops - now we've called the body twice!

;; ----------------
;; ----------------
;; Threading macros
;; ----------------
;; ----------------

;; Many times we will be using macros without knowing it - forms like
;; `when` and `and` are macros rather than functions, for example.

;; One set of macros we encounter all the time are known as the
;; `threading` macros. These macros take the evaluated value of one form
;; and immediately hand it over to the next form for evaluation.

;; The first `->` takes the first form, evaluates it, and inserts it as
;; the second item in the next form, and takes that result and does the
;; same with the next form, etc:

(-> 3
    inc
    (+ 4)
    (str "...is the final result"))
; => "8...is the final result"

;; The second `->>` works similarly, but instead of inserting each stage's
;; result as the second argument, it inserts it as the last argument:

(->> 3
     inc
     (* 4)
     (- 15)
     (str "The final reuslt is: "))
; => "The final result is: -1"


(-> 3
    inc
    (* 4)
    (- 15)
    (str "...is the final result"))
; => 1...is the final result

(->> 3
    inc
    (* 4)
    (- 15)
    (str "...is the final result"))
; => ...is the final result-1

;; There are two related threading macros, `some->` and `some->>` which
;; behave in the same way as `->` and `->>` but stop evaluation as soon as
;; any function returns `nil`

(defn always-nil
  "Just return nil"
  [& args]
  nil)

(defn example
  "An example of early form termination"
  []
  (some->> 3
           inc
           always-nil
           (println "I should never be evaluated.")
           true))

(experiment.consumers/example)
; => nil

;; These latest threading macros are great to use in cases where the final
;; function they're supposed to be passed to can't take a `nil` value. We
;; can think of these two macros as lazy evaluation for sequences, but
;; applied to program logic.

;; ------------------------
;; ------------------------
;; A closing note on macros
;; ------------------------
;; ------------------------

;; Most of the time, you can accomplish what you're trying to do using a plain
;; function, not a macro. Functions are easier to write, easier to read, and,
;; perhaps most importantly, easier to understand and reason about.

;; As with most things as powerful as macros, use sparingly and with caution!

;; -------------------------------------------
;; -------------------------------------------
;; Concurrent design patterns using core.async
;; -------------------------------------------
;; -------------------------------------------

;; Like its host language, JavaScript, and its parent language, Clojure,
;; ClojureScript has a full-featured set of concurrency-oriented design
;; patterns that are available by default.

;; ClojureScript is even more of a functional language than JavaScript,
;; and the use of design patterns that orient around callbacks and promises
;; are both common and frequently seen. You can even say that since
;; ClojureScript compiles to JavaScript, they represent the default option
;; for concurrent program design.

(ns foo
  (:require [axax.core :refer [GET]]))

(defn handler [res]
  (.log js/console (str res)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "Error: " status " " status-text)))

(GET "http://www.example.com/hello-world"
     {:handler handler
      :error-handler error-handler})

;; Here, we passed to possible callback functions in an option map to `GET`,
;; which will call one of the two functions based on what ultimate response code
;; it receives from the target server.

;; --------------------------------------------------------
;; --------------------------------------------------------
;; The Communicating Sequential Processes concurrency model
;; --------------------------------------------------------
;; --------------------------------------------------------

;; Communicating sequential processes, or `CSP`, is a member of a family of
;; different concurrency theories that are oriented around the notion of message
;; passing via channels.

;; In the context of ClojureScript, a CSP concurrency model means that rather
;; than designing programs off of a single message queue (the JS event loop),
;; the creation and maintenance of an arbitrary number of message queues is
;; used instead. These queues, known as channels, can have messages enqueued
;; by any part of a program and dequeued anywhere else, and allow for programs
;; and systems to be designed with a stronger separation of concerns between
;; the producers of data and their consumers and processors.

;; Among ClojureScript's core.async benefits is the lack of JS callback hell,
;; and performance. David Nolen's blog post speaks to the 2-5x sped increase
;; compared to JS promises:
;; `http://swannodette.github.io/2013/08/23/make-no-promises/`

;; --------------------------------
;; --------------------------------
;; Gettings started with core.async
;; --------------------------------
;; --------------------------------

;; We could write, in a file at `src/experiment/async.cljs`:

(ns experiment.async
  (:require [cljs.core.async :as async])
  (:require-macros
   [cljs.core.async.macros :as async-macros]))

(def channel (async/chan 5))

(defn enqueue-val
  "Enqueue a new value into our channel"
  [v]
  (async-macros/go
    (async/>! channel v)))

(defn retrieve-val
  "Retrieve a new value from our channel and log it."
  []
  (async-macros/go
    (js/console.log (async/<! channel))))

(defn enqueue-and-retrieve
  "Enqueue a value into a channel, and then test that we can retrieve it"
  [v]
  (enqueue-val v)
  (retrieve-val))

;; Then, in our main application, we could require it:

(ns experiment.core
  (:require [experiment.consumers :as consumers]
            [experiment.async]))

;; We use `>!` to put a value onto the channel, and we use `<!` to take it

;; --------------------
;; --------------------
;; Background Listeners
;; --------------------
;; --------------------

;; In our previous example, it's all a bit contrived. Let's simplify:

(defn listen
  "Listen to our channel for any events and log them to the console"
  []
  (async-macros/go
    (while true
      (.log js/console (async/<! channel)))))

;; Though this seems like it would create an infinite loop, it does not - as
;; within a `go` block, functions like `<!` are essentially blocking. As long
;; as `<!` doesn't find a new value on the channel, `listen` won't loop.

;; ---------------------
;; ---------------------
;; Errors and core.async
;; ---------------------
;; ---------------------

;; core.async and its emphasis on separation of concerns means we must account
;; for errors - doing so outside of our main producing function keeps our
;; code clean and easy to reason about

;; In the following example, we'll imagine our application polls a kitten
;; factory to make sure all is well:

(ns experiment.kitten.factory
  "Logic for handling messages from the kitten factory"
  (:require [cljs.core.async :as async]
            [ajax.core :refer [GET]])
  (:require-macros
   [cljs.core.async.macros :as async-macros]))

(def channel (async/chan 5))
(def error-channel (async/chan 5))

(defn enqueue-val
  "Enqueue a new value into our channel"
  [c v]
  (async-macros/go
    (async/>! c v)))

(defn success-fn
  [v]
  (enqueue-val channel v))

(defn error-fn
  [e]
  (enqueue-val error-channel e))

(defn kitten-factory
  []
  (GET "/kitten-factory"
       {:handler success-fn
        :error-handler error-fn}))

(defn listen
  "Listen for the latest message from the kitten factory channels"
  []
  (async-macros/go
    (while true
      (let [[v ch] (async/alts! [channel error-channel])]
        (case ch
          channel
          (do #_ (send-success-report-cat-hq v))
          (do #_ (send-error-report-to-cat-sq v)))))))

(listen)

;; Now, let's change things up a bit and use an alternative pattern for the
;; error-handling:
;; `http://swannodette.github.io/2013/08/31/asynchronouse-error-handling/`

;; To start, we need to create two new namespaces:

(ns experiment.kitten.helpers)

(defn error?
  [x]
  (instance? js/Error x))

(defn throw-err
  [x]
  (if (error? x)
    (throw x)
    x))

;; And in another macro file (use `.clj` not `.cljs`):

(ns experiment.kitten.macros)

(defmacro <?
  "Actively throw an exception if something goes wrong
  when waiting on a channel message"
  [expr]
  `(experiment.kitten.helpers/throw-err (cljs.core.async/<! ~expr)))

;; Finally, we could refactor our factory file:
(ns experiment.kitten.factory
  "Logic for handling messages from the kitten factory"
  (:require [cljs.core.async :as async]
            [ajax.core :refer [GET]]
            [experiment.kitten.helpers])
  (:require-macros
   [cljs.core.async.macros :as async-macros]
   [experiment.macros :as m]))

(defn channel (async/chan 5))

(defn enqueue-val
  "Enqueue a new value into our channel"
  [c v]
  (async-macros/go
    (async/>! c v)))

(defn kitten-factory
  []
  (GET "/kitten-factory"
       {:handler (fn [res] (enqueue-val channel res))
        :error-handler (fn [err] (enqueue-val channel (js/Error. err)))}))

(defn listen
  "Listen for the latest message from the kitten factory channel. If message
  is an error, throw and catch."
  []
  (async-macros/go
    (while true
      (try
        (let [v (m/<? channel)]
          ;; (send-success-report-to-cat-hq)
          )
        (catch js/Error e
          ;; (send-error-report-to-cat-hq e)
          )))))

;; Here, our `<?` macro just checks if the value pulled off the channel is an error
;; and, if it is throws an error. Otherwise, it continues passing the value.

;; Now, this is better than our original - but we can utilize ClojureScript/Clojure's
;; transducers to refactor again:

(ns experiment.kitten.factory
  "Logic for handling messages from the kitten factory"
  (:require [cljs.core.async :as async]
            [ajax.core :refer [GET]])
  (:require-macros
   [cljs.core.async.macros :as async-macros]))

(defn error?
  [x]
  (instance? js/Error x))

(defn throw-err
  [x]
  (if (error? x)
    (throw x)
    x))

;; Next, our transducer created by passing `map` no coll, only a fn
(def channel (async/chan 5 (map throw-err)))

(defn enqueue-val
  "Enqueue a new value into our channel."
  [c v]
  (async-macros/go
    (async/>! c v)))

(defn kitten-factory
  []
  (GET "/kitten-factory"
       {:handler (fn [res] (enqueue-val channel res))
        :error-handler (fn [err] (enqueue-val channel (js/Error. err)))}))

(defn listen
  "Listen for the latest message from the kitten factory channel."
  []
  (async-macros/go
    (while true
      (try
        (let [v (async/<! channel)]
          (js/console.log "All good!")
          )
        (catch js/Error e
          ;; (send-error-report-to-cat-hq e)
          )))))

(listen)
