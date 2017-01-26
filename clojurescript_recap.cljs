;; ClojureScript Recap
;; ===================

;; Briefly reviewing ClojureScript fundamentals here, though
;; I don't anticipate a great deal of "new" information per se
;; as I am coming to ClojureScript with prior knowledge of both
;; Clojure and JavaScript.


;; -------------------------------------
;; -------------------------------------
;; Understanding ClojureScript Functions
;; -------------------------------------
;; -------------------------------------


;; ---------
;; Functions
;; ---------

;; Functions are first-class entities in ClojureScript

(+ 1 2)
; => 3

(def inc2 (fn [x] (+ x 1)))
(inc2 8)
; => 9

(defn inc3
  [x] (+ x 1))
(inc3 4)
; => 5

(defn inc4
  "Returns a number one greater than the number passed in"
  [x]
  (inc x))


;; ---------------------------------
;; Multiarity and Variadic Functions
;; ---------------------------------

;; We can use pattern matching to handle multiarity:

(defn inc5
  "Returns a number one greater than the number passed in. If
  two numbers are provied, sums them both and increments that
  sum by 1."
  ([x] (inc x))
  ([x y] (inc (+ x y))))

(inc5 3)
; => 4

(inc5 3 5)
; => 9

;; We can also write functions that take any number of arguments:

(defn sum
  "Given any number of numbers, sum them together"
  [& args]
  (apply + args))

(sum 5 4 3 2 1)
; => 15


;; -------------------
;; Anonymous Functions
;; -------------------

((fn [x] (println "The argument to this function is:" x)) "Bonkers!")
; => The argument to this function is: Bonkers!
; => nil

(#(println "The arguments are:" %&) "Bonkers!" "a2")
; => The arguments are: (Bonkers! a2)
; => nil

;; NOTE: If you can't easily fit a function on a single line, you're better
;;       off sticking to the default function declaration syntax.



;; ------------
;; Side Effects
;; ------------

;; Any code inside of parentheses, as well as any data literal, that can
;; be evaluated is known as a form. Sometimes, we want to execute code for
;; its side effects, things that might include updating state, saving
;; a value to a database, popping up an alert, or printing a value out
;; to the JavaScript console.

;; Most often, when evaluating a form that only performs side effects, we
;; are expecting a return value of `nil`. If, for instance, we type the
;; following in the Figwheel REPL:
(js/console.log "I am a side effect")
; => nil
;; In our browser's JavaScript console, however, we will see the following:
> I am a side effect

;; In the event we wanted to evaluate several side effects as part of
;; a function or to evaluate one or more side effects and return a different
;; value. For that, we use the special `do` form - this allows us to evaluate
;; multiple forms, returning the last one.

(do
  (println "I am a side effect")
  (+ 1 2)
  "This is the returned string")
; => I am a side effect
; => "This is the returned string"

;; The `do` form is going to be a frequent tool in our programming arsenal,
;; it will be of use either directly or indirectly in many of our programs.


;; ---------------
;; Local Variables
;; ---------------

(let [x 10] (println x))
; => 10
; => nil

(let [a 1
      b 2]
  {a b})
; => {1 2}

;; Within the function form, we can redefine an already defined local variable:
(defn foo [x] (let [x true] x))

;; This would be as if we wrote:
;; var foo = function (x) {
;;   x = true;
;;   return x;
;; };

(foo 5)
;; => true

;; The `let` macro acts as an implicit `do` block:
(let [a 5]
  (println a)
  (+ a 3))
; => 5
; => 8


;; ---------------------------------
;; ---------------------------------
;; The ClojureScript Data Structures
;; ---------------------------------
;; ---------------------------------


;; ------------
;; Scalar Types
;; ------------


;;; Numbers
;;; -------
;;; ClojureScript numbers are just JavaScript numbers
(type 3)
; => #object[Number "function Number() { [native code] }"]

(type 1.1)
; => #object[Number "function Number() { [native code] }"]

(+ 1 2)
; => 3

(- 2 1)
; => 1

(* 1 2)
; => 2

(/ 1 2)
; => 0.5

;;; NOTE: There is no % modulo function in ClojureScript, as it
;;;       is a reserved character for use in anonymous functions.
;;;       Instead, use the `rem` function:

(rem 10 3)
; => 1

;;; Strings and characters
;;; ----------------------
;;; ClojureScript strings are just JavaScript strings, as well:
(type "A String")
; => #object[String "function String() { [native code] }"]

(type \a)
; => #object[String "function String() { [native code] }"]

;;; To create a string, utilize the `str` or `name` functions:
(str {})
; => "{}"

(str 1)
; => "1"

(name :sandwich)
; => "sandwich"

;;; NOTE: To concat a string
(str "this" "is" "all" "one" "word")
; => "thisisalloneword"

;;; NOTE: To get the length of a string
(count "my-string")
; => 9

;;; NOTE: To slice a string into a substring
(subs "parents just don't understand" 8 18)
; => "just don't"

;;; NOTE: For more, the `clojure.string` namespace has tons of useful functions!

;;; Nil
;;; ---
;;; ClojureScript's implementation of `nil` is built on JavaScript's `null`
;;; `nil` is a special type for ClojureScript, not accessible at runtime.
;;; `nil` is also important in checking for the presence of something, or
;;; to perform logical gating with Booleans

;;; Boolean values and truthiness
;;; -----------------------------
;;; ClojureScript Booleans are just JavaScript Booleans. Everything which
;;; is neither `false` or `nil` evaluates to `true` when in a conditional
;;; expression. Unlike other Lisps, this manner of evaluation means that
;;; empty lists are not considered to be "falsey", as they are neither
;;; explicity `false` nor are they `nil`
(if (js-obj "field" "value") "js object is true" "js object is false")
; => "js object is true"

(if 5 "a number is true" "a number is false")
; => "a number is true"

(if [] "an empty vector is true" "an empty vector is false")
; => "an empty vector is true"

(if {} "an empty map is true" "an empty map is false")
; => "an empty map is true"

(if nil "nil is true" "nil is false")
; => "nil is false"

(if false "false is true" "false is false")
; => "false is false"

;;; ClojureScript has a special predicate `nil?` which tests if some
;;; given quantity is `nil` or not
(nil? '()) ; "Is an empty collection nil?"
; => false

;;; The inverse of `nil?` is `some?` - it explicity checks that the thing
;;; in question is NOT nil.
(some? '()) ; "Is an empty collection not nil?"
; => true

(nil? nil)
; => "Is nil not nil?"

;;; `true?` and `false?` round out our Boolean/truthiness utilities
(true? 5)
; => false

(true? true)
; => true

(false? nil)
; => false

(false? false)
; => true

;;; Keywords
;;; --------
;;; Keywords denote quantities that evaluate to themselves - they are a type
;;; that don't have a direct analogy in most languages.

:some-keyword
; => :some-keyword

;;; NOTE: To get the literal representation of a keyword, we use the double
;;;       colon `::`
::some-ns-keyword
; => :cljs.user/some-ns-keyword

;;; Keywords are typically used to represent tags, or different values
;;; that a given quantity could take. Since they can be evaluated, they're
;;; frequently used as functions on maps.

;;; Symbols
;;; -------
;;; Symbols function sort of like pointers or refs, they resolve to other values.
;;; We can write code that points to and manipulates the symbols themselves via
;;; macros.
(def a 4)

(type (quote a))
; => cljs.core/Symbol


;; -------------------------
;; -------------------------
;; ClojureScript Collections
;; -------------------------
;; -------------------------


;;; ClojureScript collections are, in reality, like a series of interfaces,
;;; dictating the funtions we can use with a given data type and how those
;;; functions work.

;;; We can also talk about interfaces in either the abstract idea or the
;;; specific implementation as a protocol.


;; -----
;; Lists
;; -----

;;; Lists are the heart of the language. In fact, it is the very expressions
;;; we use declare inside of our `()` that are not only code, but also data
;;; - they are lists.
(+ 1 2)
; => 3

(type (+ 1 2))
; => #object[Number "function Number() { [native code] }"]

;;; NOTE: We were expecting `list` here, right? Well, the `type` function
;;;       received the evaluated value from the list: (+ 1 2).

;;; NOTE: Since lists are unique among ClojureScript collections in that
;;;       they are evaluated automatically as function calls. To keep the
;;;       list in its original form and not be evaluated, we use the
;;;       `quote` function:
(quote (+ 1 2))
; => (+ 1 2)

(type (quote (+ 1 2)))
; => cljs.core/List

;;; NOTE: We can create lists without having to call `quote` (which is a special
;;;       form that works with the ClojureScript compiler) by using the `list`
;;;       function directly:
(list + 1 2)
; =>
;;  (#object[cljs$core$_PLUS_ "function cljs$core$_PLUS_() {
;;    var args6946 = [];
;;    var len__5181__auto___6952 = arguments.length;
;;    var i__5182__auto___6953 = 0;
;;    while (true) {
;;      if (i__5182__auto___6953 < len__5181__auto___6952) {
;;        args6946.push(arguments[i__5182__auto___6953]);
;;        var G__6954 = i__5182__auto___6953 + 1;
;;        i__5182__auto___6953 = G__6954;
;;        continue;
;;      } else {
;;      }
;;      break;
;;    }
;;    var G__6951 = args6946.length;
;;    switch(G__6951) {
;;      case 0:
;;        return cljs.core._PLUS_.cljs$core$IFn$_invoke$arity$0();
;;        break;
;;      case 1:
;;        return cljs.core._PLUS_.cljs$core$IFn$_invoke$arity$1(arguments[0]);
;;        break;
;;      case 2:
;;        return cljs.core._PLUS_.cljs$core$IFn$_invoke$arity$2(arguments[0], arguments[1]);
;;        break;
;;      default:
;;        var argseq__5200__auto__ = new cljs.core.IndexedSeq(args6946.slice(2), 0);
;;        return cljs.core._PLUS_.cljs$core$IFn$_invoke$arity$variadic(arguments[0], arguments[1], argseq__5200__auto__);
;;    }
;;  }"] 1 2)

;; -------
;; Vectors
;; -------

;;; Essentially, immutable JavaScript arrays - can be instantiated using
;;; brackets or the vector constructor function:
["a" "vector" "of" "strings"]
; => ["a" "vector" "of" "strings"]

(vector "another" "vector" "of" "awesome" "strings")
; => ["another" "vector" "of" "awesome" "strings"]

(vec (list "yet" "another" "vector" "of" "strings"))
; => ["yet" "another" "vector" "of" "strings"]

;;; NOTE: Vectors are great for ordered lists of things we might want to index

;;; NOTE: New elements are added to the END of vectors

(conj [1 2 3] 4)
; => [1 2 3 4]

(get ["item" "from" "vector" "of" "strings"] 3)
; => of

(get ["item" "from" "vector" "of" "strings"] 5)
; => nil

(get ["item" "from" "vector" "of" "strings"] 5 "default value")
; => "default value"

(["item" "from" "vector" "of" "strings"] 2)
; => "vector"

(seq ["sequence" "from" "vector"])
; => ("sequence" "from" "vector")

;;; NOTE: We can convert JavaScript arrays to ClojureScript vectors
;;;       via the invocation of the js interop namespace

(js/Array. 1 2 3) ; the (.) invokes the JS method with new keyword
; => #js [1 2 3]

(def arr (js/Array. 1 2 3))
; => #'cljs.user/arr

(def v [1 2 3])
; => #'cljs.user/v

(= arr v)
; => false

;;; REMEMBER: To convert values between cljs and js, we use the following:

(clj->js v)
; => #js [1 2 3]

(js->clj arr)
; => [1 2 3]

(= (clj->js v) arr)
; => false

;;; REMEMBER: Two native JS arrays with the same values are NOT equal!

(= arr arr)
; => true

(= (clj->js v) (clj->js v))
; => false
;;; NOTE: Casting from ClojureScript to JavaScript will always create a new
;;;       JavaScript array each time, which means we cannot check equality
;;;       for the same ClojureScript vecotr if cast it to an array multiple
;;;       times. BUT, this isn't true the opposite direction!

(= (js->clj arr) v)
; => true

;;; When should I use lists versus vectors?
;;; ---------------------------------------
;;; Though lists and vectors seem similar, they have very different algorithmic
;;; properities. New elements are added to the end of vectors in constant time,
;;; while new elements are added to the beginning of lists in constant time.
;;; Getting specific elements or updating specific elements from a vector occurs
;;; in constant time, whereas accessing the nth element of a list requires
;;; linear time and cannot be changed without instantiating and allocating
;;; memory for a new list.

;;; Under the hood, vectors are very similar to arrays, while lists can be
;;; thought of as linked lists.

;;; In practice, since ClojureScript lists are used primarily as data to be
;;; passed to the compiler, and due to prepend efficiency being only
;;; occassionally useful, lists are usually used when writing or manipulating
;;; code - while vectors tend to be used when you're writing and manipulating
;;; data (which is most of the time)

;; ----
;; Maps
;; ----

;;; Essentially, ClojureScript maps are immutable versions of JavaScript objects

{:name "Marc" :age 29}
; => {:name "Marc", :age 29}

(hash-map :type :book :title "Learning Clojurescript")
; => {:type :book, :title "Learning ClojureScript"}

{:address {:city "San Francisco"} {:family "parents"} {:father "Geoff"}}
; => {:address {:city "San Francisco"}, {:family "parents"} {:father "Geoff"}}

;;; NOTE: Adding values to a map (due to the need of an addition to have both
;;;       a key and a value), we require a `seq`

(conj {} {:a 1})
; => {:a 1}

(conj {} [:a 1])
; => {:a 1}

;;; NOTE: To remove, we use `dissoc`

(dissoc {:a 1 :b 2} :b)
; => {:a 1}

{:a {:a :b}}
; => :b

;;; NOTE: Just as with vectors, to retrive items, we can use the `get` function
;;;       or retrieve them directly

(get {1 :a} 1)
; => :a

({1 :a} 1)
; => :a

;;; NOTE: Calling `seq` on a map will return a sequnce where each element
;;;       is a vector pair with a single key and its associated value

(seq {:b 1 :c 2})
; => ([:b 1] [:c 2])

;;; NOTE: As with JavaScript arrays and ClojureScript vectors, you can easily
;;;       cast ClojureScript maps to JavaScript objects

(def m {:name "Marc"})
; => #'cljs.user/m

(clj->js m)
; => #js {:name "Marc"}

;;; Different types of maps
;;; -----------------------
;;; Most of the time, we will be working with either ArrayMaps (`array-maps`)
;;; or HashMaps (`hash-maps`). ArrayMaps are very efficient at small sizes
;;; (typically those with less than eight keys), whereas HashMaps are more
;;; efficient at larger sizes. ClojureScript automatically converts ArrayMaps
;;; to HashMaps as your map grows.
;;; Finally, we have `sorted-maps` which preserve keys in sorted order.

;; ----
;; Sets
;; ----

;;; NOTE: Each item in a `set` must be unique

(hash-set 1 1 2 3)
; => #{1 2 3}

#{1 1}
; => clojure.lang.ExceptionInfo: Duplicate key: 1 {:type :reader-exception, :line 1, :column 7, :file "NO_SOURCE_FILE"}

(def s #{1 3 2})

(conj s 4)
; => #{1 3 2 4}

(conj s 1)
; => #{1 3 2}

;;; NOTE: We remove items from sets using `disj`
(disj s 2)
; => #{1 3}

(get #{1 2 3} 1)
; => 1

(#{1 2 3} 2)
; => 2

;;; NOTE: We check for membership in a `set` using the `contains?` function
(contains? #{1 2 3} 1)
; => false

(filter #{1 2 3} [1 3 5])
; => (1 3)

;;; NOTE: We can get access to a host of useful operations via an external
;;;       namespace, `clojure.set`

(clojure.set/union #{1 3} #{1 2})
; => #{1 3 2}
(clojure.set/difference #{1 3} #{1 2})
; => #{3}
(clojure.set/intersection #{1 3} #{1 2})
; => #{1}

;;; NOTE: Although ES6 supports sets, not all browser vendors do - so when calling
;;;       the `clj->js` function on a set, the return value is a JavaScript array

(clj->js s)
; => #js [1 3 2]

;; ---------
;; Sequences
;; ---------

;; Sequences are abstractions rather than a specific data type.
;; They are one fo the most important abstractions in both Clojure
;; and ClojureScript - and can be understood, in a sense, as a "view"
;; on top of a concrete underlying data structure.
;; At its core, a sequence is a logical list with a head (the first item
;; in the sequnce) and a remainder (the remaining elements of the list).
;; We refer to sequences as `seqs`.
;; Attempting to create a sequence with no elements returns `nil` and
;; since `nil` is falsey, this is the idiomatic method for testing whether
;; a collection has no elements:

(seq {})
; => nil

(if (seq {}) true false)
; => false

;; Sequences are associated with the concrete protocol `ISeq`, which requires
;; three functions: `first`, `rest`, and `cons`

;;; NOTE: `cons` => at the 's' start :)

(conj (seq [1 2 3]) 3)
; => (3 1 2 3)

(cons 3 (seq [1 2 3]))
; => (3 1 2 3)

;;; NOTE: All sequences are collections, but not all collections are sequences!

;; Sequences and most of their related functions are akin to iterators like `for`
;; and `foreach` in JavaScript, although function differently. As sequences are
;; immutable, they aren't stateful cursors into a collection, but rather are
;; persistent and immutable views.

;; --------
;; Laziness
;; --------

;; Most sequences are lazy. New functions that return sequences can be written
;; to return lazy sequences by wrapping the body in the `lazy-seq` macro.

(defn lazy-func [x] (println "Printed" x))

(take 2 (map lazy-func (seq [1 2 3 4 5])))
Printed 1
Printed 2
; => (nil nil)

;; Even though we passed five elements, `lazy-func` was only evaluated twice
;; because both `seq` and `map` are lazy functions.

;; One of the powerful things about lazy sequences is we can process
;; infinitely long sequnces - like `range`. To avoid being in a situation
;; where we genreate an infinite sequence and attempt to display it in the REPL,
;; we can safeguard ourselves:

(set! *print-length* 5)
; => 5

(range)
; => (0 1 2 3 4 ...)

(take 5 (range))
(range 5)
; => (0 1 2 3 4)


;; --------------------
;; --------------------
;; Collection Protocols
;; --------------------
;; --------------------

;; ----------
;; Sequential
;; ----------

;; The sequential protocol requires that the core functions of seqs are supported
;; (first, rest, cons), but also that the collection retains linear ordering

(first [1 2 3])
; => 1

(rest [1 2 3])
; => (2 3)

(cons 4 [1 2 3])
; => (4 1 2 3)

(sequential? [])
; => true

(sequential? {})
; => false

(sequential? #{})
; => false

(sequential? (list 1 2 3))
; => true

;; -----------
;; Associative
;; -----------

;; This sequence protocol supports key-value lookups. They implement the
;; following methods:
;;   -lookup - returns the value at the given key
;;   -assoc - stores a new value at the given key

(contains? {:a 1 :b 2} :a)
; => true

(contains? ["apple" "pear" "banana"] 1)
; => true

(contains? ["apple" "pear" "banana"] 3)
; => false

;; NOTE: `contains?` doesn't "look" at the content at the index, only the index
(contains? ["apple" "pear" "banana"] "apple")
; => false

;; Lastly, we have `assoc` as a way to set new values at a given key:
(assoc {:a 3} :b 2)
; => {:a 3, :b 2}

(assoc {:a 3} :a 2)
; => {:a 2}

(assoc ["apple" "pear" "banana"] 0 "peach")
; => ["peach" "apple" "pear" "banana"]

;; NOTE: Be careful with adding "new" key to a vector! If you set a value
;;       at an index more than one beyond the range of the vector

(assoc ["apple" "pear" "banana"] 4 "peach")
; => #object[Error Error: Index 4 out of bounds [0,3]] Error: Index 4 out of bounds [0,3]

(associative? [])
; => true

(associative? {})
; => true

(associative? #{})
; => false

;; ------
;; Sorted
;; ------

;; Sorted collections must support fast insertion and retrieval while
;; maintaining a sorted order. `sorted-maps` and `sorted-sets` satisfy
;; the sorted protocol.

(sorted? (sorted-map :a 1 b 2))
; => true

(sorted? (hash-map :a 1 :b 2))
; => false

;; NOTE: Casting `seq` on a sorted collection will return a seq that will not
;; necessarily be returned in sorted order

;; -------
;; Counted
;; -------

;; Lists, maps, sets, sequences, and vectors all satisfy the counted protocol.
;; Lazy sequences, however, do not follow the protocol.

(count (map lazy-func (seq [1 2 3 4 5])))
; Printed 1
; Printed 2
; Printed 3
; Printed 4
; Printed 5
; Printed 6
; => 5

;; NOTE: Be careful - calling `count` on a lazy seq will execute its side effects

;; NOTE: Be very, very careful - don't call `count` on infinite seqs, you will
;;       quickly run out of memory!

;; ----------
;; Reversible
;; ----------

;; The reversible protocol has only a single method - `rseq`. Vectors,
;; sorted-maps, and sorted-sets are all reversible in constant time.
;; The method works just like `seq`, except it returns a seq in reverse
;; sequential order:

(rseq (sorted-map :c 3 :b 2 :a 1))
; => ([:c 3] [:b 2] [:a 1])

(rseq [1 2 3])
; => (3 2 1)


;; ---------------------------
;; ---------------------------
;; Object-oriented Programming
;; ---------------------------
;; ---------------------------


;; Urgh - gross - but okay...here goes nothing...

;; ClojureScript shares the same tooling for OOP methodology as Clojure -
;; namely, notions of protocols, types, and records.

;; We've already touched on protocols, but we'll explore them in more depth now:

;; ---------
;; ---------
;; Protocols
;; ---------
;; ---------

;; As we alreay learned, protocols define the function interface an object
;; must support. In the case of the protocol `ISeq`, the function interface
;; requires support for three functions: `first`, `rest`, and `cons`

;; If we were to create our own protocol, `IMonster`, we might define it like:

(defprotocol IMonster
  (roar [this])
  (scare [this other]))

;; For any object o then satisfy the monter protocol, it must have
;; implementations of the two methods `roar` and `scare`. It's also generally
;; a good idea for your implementations to all take and return the same types.

;; -----
;; -----
;; Types
;; -----
;; -----

;; A type in ClojureScript is a bit like a class in most other languages.

(deftype Human [name age]
  Object
  (getName [this] name)
  (getAge [this] age)
  (panic [this] (println "Aaaaagh!")))

(new Human "Marc" 29)
; => #object[cljs.user.Human]

(def marc (new Human "Marc" 29))
(.getName marc)
; => "Marc"
(.getAge marc)
; => 29
(.panic marc)
; => "Aaaaagh!"

(deftype Troll [name]
  IMonster
  (roar [this] (println "ROAAAAR!!!")))
; => cljs.user/Troll

(satisfies? IMonster (new Troll "Bork"))
; => true

;; Well, that was odd!
;; Even though `Troll` doesn't satisfy the `IMonster` protocol (as it doesn't
;; have an implementation for scare). Since we told the compiler `Troll` was
;; of type IMonster, it 'trusts' us to implement the full spec. To correct
;; this error, we should probably approach things like this:

(deftype Troll [name]
  IMonster
  (roar [this] (println "ROAAAAR!!!"))
  (scare [this other] (.panic other)))

(def wilhelm (new Troll "Wilhelm"))
(scare wilhelm hazel)
; => "Aaaaagh!"

;; -------
;; -------
;; Records
;; -------
;; -------

;; Records are similar to types, but instead of being barebones objects,
;; they are extensions of a base class that provides built-in `hash-map`
;; features for fast and easy access to attributes.

(defrecord Lair [place])
(def hideout (new Lair "cave"))
(:place hideout)
; => "cave"

;; Records satisfy the `map` protocol, and can be treated as maps:

(map? hideout)
; => true

(assoc hideout :atmosphere "dark and wet")
; => #cljs.user.Lair{: place "cave", :atmosphere "dark and wet"}

;; But, with a record, if we try to `dissoc` a required key we'll
;; get back a plain map, not an instance of the given record type:

(dissoc (assoc hideout :atmosphere "dark and wet") :place)
; => {:atmosphere "dark and wet"}

;; Like types, records also satisfy protocols:

(defrecord Vampire [name]
  IMonster
  (roar [this] (println "Actually, we vampires are rather quiet."))
  (scare [this other] (.panic other)))

(def drac (new Vampire "Dracula"))
(roar drac)
; => "Actually, we vampires are rather quiet."
(:name drac)
; => "Dracula"

;; -----------------------------
;; -----------------------------
;; Extending types and protocols
;; -----------------------------
;; -----------------------------

;; We'll create a new record:

(defrecord WereWolf [name])

;; Now, we'll extend that new record to satisfy the `IMonster` protocol:
(extend-type WereWolf
  IMonster
  (roar [this] (println "Growl!"))
  (scare [this other] (println "*silent panic*")))

(roar (WereWolf. "james")) ; REMINDER: The (.) invokes JS new keyword
; => "Growl!"

;; Another way to extend is like this:

(defprotocol ISecretive
  (hide [this]))

(extend-protocol ISecretive
  Vampire
  (hide [this] (println "..."))
  WereWolf
  (hide [this] (println "rustle rustle")))

(hide drac)
; => "..."

;; -----
;; -----
;; Reify
;; -----
;; -----

;; If you only need a single instance of an anonymous type, we can avoid
;; overhead by using `reify` which does not create factories for new
;; instances of its provided type:

(def mouse
  (reify ISecretive
    (hide [this] (println "Squeak!"))))
(hide mouse)
; => "Squeak!"


;; -------------------------
;; -------------------------
;; Other ClojureScript Types
;; -------------------------
;; -------------------------


;; -------------------
;; -------------------
;; Regular expressions
;; -------------------
;; -------------------

;; ClojureScript regexes are just JavaScript `RegExp` instances.

;; We can use ClojureScript's regex literal syntax:
(type #"^Clojure")

;; Or, we can use JS interop:

(js/RegExp. "^Clojure$")
; => #"^Clojure$"

;; We can use any and all of Clojure's core regex methods, as well:
;; - `re-find` - returns first match of a regex in a string
;; - `re-matches` - returns the match of regex in a string if it fully matches
;; - `re-pattern` - compiles a regex from a string

;; -----
;; -----
;; Atoms
;; -----
;; -----

;; Atoms are mutable data structures that suit themselves to managing
;; changing state in an application. They are metastructures that can hold
;; any other data structure. All we have to do to create an atom is use the
;; `atom` function:

(def an-atom (atom 5))

(def another-atom (atom {:a "value"}))

;; We extract the value from an atom, or dereference it, using `deref`:

@an-atom
; => 5

@another-atom
; => {:a "value"}

;; To update an atom's value(s), we have to options: `swap!` or `reset!`.
;; `reset!` is easier because it directly stores a new value in an existing
;; atom, while `swap!` applies a function ot the existing contents. Both
;; return the updated value being stored in the atom:

(reset! an-atom 3)
; => 3

(swap! an-atom inc)
; => 4

(swap! another-atom assoc :b "Other")
; => {:a "value", :b "Other"}

;; We can observe changes to an atom using a `watch` function, which is
;; called whenever the state of the atom changes:

(def new-atom (atom {}))

(defn watcher-fn [key the-atom old-value new-value]
  (println key the-atom old-value new-value))

(add-watch new-atom :watcher-key watcher-fn)

(reset! new-atom {:a 2})
; => {:a 2}

;; The `add-watch` parameter requires three functions: the atom to watch, a key
;; and the `watcher` function. The key value is so you can remove a watcher
;; later, if needed:

(remove-watch new-atom :watcher-key)
(reset! new-atom {:b 4})
; => {:b 4}


;; ------------
;; ------------
;; Immutability
;; ------------
;; ------------

;; NOTE: I feel really solid about the benefits here, so I'm going to just
;;       skim this section....

(def x [1])

(conj x 2)
; => [1 2]

(def x (conj x 2))

;; cljs.user=> x
;; => [1 2]

;; In contrast, here's JavaScript:

; let x = [1];
; => [1]

; x.push(2)
; => 2

; x
; => [1, 2]

;; To accomplish the type of immutable actions in JavaScript, we'd have to:

;; let x = [1];
;; => [1]

;; x.concat(2)
;; => [1, 2]

;; x
;; => [1]

;; x = x.concat(2)
;; => [1, 2]

;; x
;; => [1, 2]

(def m {:key :lock})

(assoc m :color "gold")
; => {:key :lock, :color "gold"}

;; cljs.user=> m
;; => {:key :lock}


;; -------------------------------------
;; -------------------------------------
;; Advanced Destructuring and Namespaces
;; -------------------------------------
;; -------------------------------------


;; -------------
;; -------------
;; Destructuring
;; -------------
;; -------------

(let [[a b] [1 2]]
  (+ a b))
; => 3

(let [[[a b] c] [[1 2] 3]] (+ a b c))
; => 6

;; We can also using binding:

(let [[a b] [[1 2] 3]
      c (first a)]
  (println a)
  c)
; => [1 2]
; => 1

;; Destructuring allows us to bind many values in a small amount of code,
;; without having to bind every element as we might otherwise, as shown
;; in the following example:

(let [a (first [1 2 3])
      b (second [1 2 3])
      c (nth [1 2 3] 2)] [a b c])
; => [1 2 3]

(let [[a] [1 2 3]] [a])
; => [1]

(let [[a b c] [1] [a b c]])
; => [1 nil nil]

(let [[a & b] [1 2 3]] [a b])
; => [1 (2 3)]

;; We can also bind the original data structure:

(let [[a & b :as one-two-three] [1 2 3]] one-two-three)
; => [1 2 3]

(defn my-func [[a :as original]] original)
(my-func [1 2 3])
; => [1 2 3]

;; We can destructure other data types, as well:

(let [[zero one two & more] (range 5)]
  (list zero one two more))
; => (0 1 2 (3 4))

(let [{n :name} {:name "David" :age "28"}] n)
; => "David"

;; What happens when we attempt to bind a key that isn't in the relevant data:
(let [{c :city} {:name "David" :age "28"}] c)
; => nil

;; We can set a default (so we don't just get `nil` back:
(let [{c :city :or {c "San Francisco"}} {:name "David" :age "28"}] c)
; => "San Francisco"

(let [{c :city n :name a :age :or {c "San Francisco" age "30"}}
      {:name "David" :age "28"}] [c n a])
; => ["San Francisco" "David" "28"

(let [{c :city :as original} {:name "David" :age "28"}]
  original)
; => {:name "David" :age "28"}

;; Although we've been using keywords as examples of the keys we're accessing,
;; we can destructure anything that is a key in the map. Here, we'll
;; destructure a map with integers as keys:

(let [{one 1} {1 "One" 10 "Ten"}] one)
; => "One"

;; Clojure's usual idiom for binding keys that have keyword accessors is to
;; bind them to a variable with the same name as the key. ClojureScript has
;; a shorthand, `:keys`, that lets us do this without hassle:

(let [{:keys [a b c]} {:a "one" :b "two" :c "three"}] (list a b c))
; => ("one" "two" "three")

;; There are also shorthand functions for string and symbol keys, `:strs`
;; and `:syms`:

(let [{:strs [a b c]} {"a" "one" "b" "two" "c" "three"}] (list a b c))
; => ("one" "two" "three")

(let [{:syms [a b c]} {'a "one" 'b "two" 'c "three"}] (list a b c))
; => ("one" "two" "three")

;; Remember, we can destructure in a nested fashion:

(let [{[a b] :name} {:name ["David" "Jarvis"]}] (str a " " b))
; => "David Jarvis"


;; ----------
;; ----------
;; Namespaces
;; ----------
;; ----------


;; Namespaces in ClojureScript are similar to Python or Ruby or JavaScript ES6
;; modules, or Java classes. They're containers for `vars` and group
;; functionality in a modular and reusable way. We create a namespace with the
;; `ns` macro.

(ns app.core "Main app logic goes here")

(def app "I'm actually just a string, whoops!")

;; The default namespace in the Clojure/ClojureScript REPLs is `cljs.user`

;; To practice namespacing, I've created a new project in the directory
;; titled `experiment`


;; ---------------------------
;; ---------------------------
;; JavaScript Interoperability
;; ---------------------------
;; ---------------------------


;; Typically, we want to rely on ClojureScript's data structures over
;; native JavaScript. However, it's necessary to know how to use
;; JavaScript interop when calling JS libraries from ClojureScript.

;; ----------------------
;; ----------------------
;; JavaScript collections
;; ----------------------
;; ----------------------

;; ------
;; Arrays
;; ------

(def a (array 1 2 3))

;; cljs.user=> a
;; => #js [1 2 3]

;; We can also use the `#js` reader macro:

;; cljs.user=> #js [4 5 6]
;; => #js [4 5 6]

(js->clj a)
;; => [1 2 3]

;; To retrieve values from JavaScript arrays, we use the `aget` function,
;; which is indexing into the JavaScript array like `a[0]`:

(aget a 0)
; => 1

;; Because JavaScript arrays are mutable, we can update a value at an index
;; using the `aset` function:

(asset a 1 "banana")
; => "banana"

;; cljs.user=> a
;; => #js [1 "banana" 3]

;; -------
;; Objects
;; -------

;; To create JavaScript objects we use the `js-obj` function and provide
;; key/value pairs:

(def obj (js-obj "name" "marc" "age" 30))

;; cljs.user=> obj
; => {:name "Marc", :age 30}

;; We retrieve values by calling the key as a property of the object:

(.-name obj)
; => "Marc"

;; We can also use `aget` to get values from an object:

(aget obj "name")
;; => "Marc"

(aset obj "job" "software developer")
; => "software developer"

;; cljs.user=> obj
;; => {:name "Marc", :age 30, :job "software developer"}

;; We can again use the `#js` reader macro to create a JSON object:

;; cljs.user=> #js {"Key" "Value"}
;; => #js {:Key "Value"}

(js->clj obj)
; => {"name" "Marc", "age" 30, "job" "software developer"}

(js->clj obj obj :keywordize-keys true)
; => {:name "Marc", :age 30, :job "software developer"}

;; -----------------
;; JS interop syntax
;; -----------------

;; New instances of a particular object can be created with `(new Type ...`
;; or `(Type ...)`, with the latte being preferred:

(js/String. "Magic!")
; => #object [String Magic!]

(js/console.log "Show me the money!")

(js/Math.PI)
; => 3.141592653589793

;; The preferred style, however, is to use a more idiomatic syntax:

;; - for methods -
(.log js/console "Show me the money!")

;; - for attributes -
(.-PI js/Math)

(def v #js {})
(set! (.-foo v) "bar")
;; cljs.user=> v
;; => #js {:foo "bar"}


;; -------------------------------------------------------------------
;; -------------------------------------------------------------------
;; The Google Closure Compiler and Using External JavaScript Libraries
;; -------------------------------------------------------------------
;; -------------------------------------------------------------------


;; ClojureScript has an extremely close relationship with the Google Closure
;; Compiler, so close that the Google Closure Compiler is bundled with
;; ClojureScript itself. It is easily imported and referenced, much as one
;; would any other normal ClojureScript code:

(ns experiment.goog
  (:import goog.history.Html5History))
(defonce hist (Html5History.))

;; This `ns` syntax, `:import` is used only when we want to load a
;; particular Google Closure Library.

;; Just like Grunt/Grulp/Webpack, etc. - we can configure dev and prod
;; builds for our ClojureScript applications using Google Closure Library.
;; As with these other tooling options, the Closure compiler can be passed
;; options (like `:optimizations`) which can handle dead code elimation,
;; minification, and optimization of code, and error notifications during
;; the compilation process.

;; Generally, during development we might use: `:optimizations :none`
;; For production, we'd likely explore using: `:optimizations :advanced`

;; As a consequence of how the Closure compiler works, most external
;; JavaScript libraries aren't usable by default when compiling for production.
;; Thankfully, there are ways to work with JS libraries that can work with
;; the mechanics of the Closure compiler.

;; -------------------------------------------
;; -------------------------------------------
;; Referencing external libraries with externs
;; -------------------------------------------
;; -------------------------------------------

;; The most basic way of bundling an external JavaScript library with
;; your ClojureScript application is not to bundle it with the app,
;; but simply reference it directly.

;; See the `experiment` directory for usage of the key `externs` in the
;; `:compiler` map


;; ---------------------------
;; ---------------------------
;; Bundling External Libraries
;; ---------------------------
;; ---------------------------


;; In addition to referencing external libraries, you can also bundle them
;; as part of your application. This method has the benefit of putting
;; everything in a single file and allowing the Google Closure Compiler
;; to optimize all source code and dependencies for production.

;; ---------------------------------------
;; ---------------------------------------
;; Google Closure Compiler compatible code
;; ---------------------------------------
;; ---------------------------------------

;; Any library written to be compatible with the Google Closure Compiler
;; (that is, they expose their namespaces with `goog.provide`) are easy
;; to add as dependencies. All you have to do is add the library to
;; the `project.clj` file's `cljsbuild` configuration with the `:lib` key.

;; For example, to include jQuery:

{:cljsbuild {:compiler {:libs ["jQuery.js"]} ... }}

;; ------------------
;; ------------------
;; Foreign JavaScript
;; ------------------
;; ------------------

;; Libraries that aren't yet written to be compatible with the Google Closure
;; Compiler can still be used in ClojureScript's applications. You'll
;; simply use the externs file like the one we used in our `experiment` example,
;; but we'll also need to add a `:foreign-libs` map to our `:compiler` options
;; for the `min` profile.

;; As an example, rather than having our JavaScript in our `index.html` file,
;; it makes sense to separate our concerns and abstract our `treeact` work into
;; a `treeact.js` file at the root of our project.

;; See the `experiment` directory


;; ------
;; ------
;; CLJSJS
;; ------
;; ------


;; Even though we've already covered a few ways of including external JavaScript
;; libraries, all the options have taken some effort.

;; There's a better way, that works for many libraries, and it's known as the
;; CLJSJS.

;; The CLJSJS project (`cljsjs.github.io`) is a community-driven effort a la
;; NPM which serves as a package repository for the most common and popular
;; JavaScript libraries in a way that's easily consumable by ClojureScript
;; applications and is compatible with the Google Closure Compiler.

;; For example, Facebook's React, can be added via the `:dependencies` key:

[cljsjs/react "15.4.2-0"]

;; With this added, we can then include `js/React` in our application.
