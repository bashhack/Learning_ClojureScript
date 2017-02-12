;; Building Richer Web Applications
;; ================================


;; In this section, we'll cover:
;; - Real-time communication with websockets
;; - Improving load times with CLJS modules
;; - Server-side rendering


;; ---------------------------------------
;; ---------------------------------------
;; Real-time communication with websockets
;; ---------------------------------------
;; ---------------------------------------


;; WebSockets are a modern browser feature that makes it easer and more
;; efficient to work with real-time streaming data, without the hacky
;; long-polling HTTP connections.

;; We're going to create our own Slack chatbot to learn more!


;; ------------------------------------
;; ------------------------------------
;; Understanding the websocket protocol
;; ------------------------------------
;; ------------------------------------


;; WebSockets are a separate protocol from HTTP (though they happen to
;; run on TCP port 80).

;; They provide bidirectional messages with lower overhead than HTTP-based
;; hacks, such as long polling or Bidirectional-streams Over Synchronous HTTP
;; (BOSH).

;; The lower overhead of WebSockets is, in part, due to their use of binary
;; streams rather than base64 encoded XML. WebSockets also have their own
;; URL schema starting with the unsecured `ws://` and the secured `wss://`.
;; The initial request still happens over HTTP, but once the server accepts,
;; the TCP socket "converts" to sending WebSocket messages.

;; Our new project will live in the `cljsbot` directory


;; ----------------
;; ----------------
;; Using Datascript
;; ----------------
;; ----------------


;; Datascript is an in-memory DB for ClojureScript, modeled after Datomic, the
;; commercial Clojure database produced by Cognitect.

;; -----------------------------------------------
;; -----------------------------------------------
;; Understanding the Datascript/Datomic data model
;; -----------------------------------------------
;; -----------------------------------------------


;; Datascript is a consistent (CP, in CAP terms), ACID, non-SQL DB built around
;; the concept of a datom (an ordered list of elements) composed of
;; entity/attribute/value/time, commonly abbreviated as EAVT

;; Let's imagine a simple example, like a database of movies like IMDB:

{:movie/title "Top Gun"
 :movie/year 1986}

;; In Datomic, this map would be broken into a series of datoms, with each
;; datom representing a single key-value pair:
[1 :movie/title "Top Gun" 1234]
[1 :movie/year 1986 1234]

;; Let's map these to our EAVT:
;; NOTE: <tx-id> is our time/transactionID
[<e-id> <attribute>      <value>          <tx-id>]
...
[167    :person/name     "James Cameron"      102]
[234    :movie/title     "Die Hard"           102]
[234    :movie/year      1987                 102]
[235    :movie/title     "Terminator"         102]
[235    :movie/director  167                  102]

;; Tuples with the same `e-id` are facts about the same entity, while tuples
;; with the same transaction ID were introduced in the same transaction.

;; To work with Datascript, we'll create a new project in our `ds` directory


;; ---------------
;; ---------------
;; Query arguments
;; ---------------
;; ---------------


;; To create a query for our new DB, we could write:
(d/q '[:find ?e :in $ ?name :where [?e :movie/title ?name]] @conn "Top Gun")

;; Here, we have a query which is passed as two arguments, the DB (@conn) and
;; the string "Top Gun". These are both bound in the query in the `:in` clause
;; in positional order, so the DB takes the special value `$`, and `?name` is
;; bound to the value passed in to the function. The DB name of `$` is special,
;; and queries can operate across multiple DBs in which case we would use `$1`,
;; `$2`, and so on with names based on the order they're passed into the
;; function.

;; Compare this less readable version to the second, more readable version:
(d/q '[:find ?e :in $ ?name :where [?e :movie/title "Top Gun"]] @conn )

(d/q '[:find ?e :in $ ?name :where [?e :movie/title ?name]] @conn "Top Gun")


;; ------
;; ------
;; Schema
;; ------
;; ------


;; Datascript uses a schema to alter the types of attributes. Unlike Datomic,
;; Datascript does not require specifying the type of every attribute.

;; Attributes can story any value, though we want to specify two types of
;; attributes, refs and cardinality, which we'll look at more later.


;; -------------------------
;; -------------------------
;; Understanding db.type/ref
;; -------------------------
;; -------------------------


;; Refs are a datatype used for storing references to other entity IDs.
;; We can think of them as being analogous to foreign keys in SQL, though
;; they are a special datatype rather than just plain ints.


;; -----------
;; -----------
;; Cardinality
;; -----------
;; -----------


;; SQL DBs are row oriented and typically don't represent one-to-many
;; relationships without resorting to join tables.
;; Datomic and Datascript natively support these types of relationships
;; in the database schema. If an attribute is declared with
:db/cardinality :db.cardinality/many
;; the attribute will store a set of values rather than a single value.

(let [schema {:movie/actors {:db/cardinality
                             :db.cardinality/many
                             :db/valueType :db.type/ref}
              :movie/director {:db/valueType :db.type/ref}}
      conn (d/create-conn schema)])

@(d/transact conn [[:db/add 1 :movie/actors 2]
                   [:db/add 1 :movie/actors 3]])

;; Here, because `:movie/actors` is declared with a "many" relationship,
;; this adds actors 2 adn 3 to the `set` of `:movie/actors`.
;; Likewise, as `:movie/director` is not declared with a "many" relationship,
;; it defaults to `:db.cardinality/one`. This means that calling `:db/add`
;; on `:movie/director` updates the value by replacing any previous director.

;; In our `ds` directory's `core.cljs` file we use negative numbers for
;; our IDs in order to reuse the same ID to refer to the same entity later
;; in the transaction. We use `d/tempid` in others to idiomatically, "Just
;; generate a new unique ID, I won't be referring to it in this transaction."

;; We could also use `tempid` in the following way:

(let [arnold (d/tempid)]
  @(d/transact conn [{:db/id arnold
                      :person/name "Arnold Schwarzenegger"}]))


;; ----
;; ----
;; Pull
;; ----
;; ----


;; So far, we've used tuples because they're fundamental to how Datomic and
;; Datascript represent things. However, we aren't really dealing with
;; tuples conceptually, we're thinking about `person` or `movie`, etc.
;; Both Datomic and Datascript utilize a feature called `pull`, which
;; retrieves some or all of an entity. It's a little like a `SELECT`
;; statement in SQL statements. But pull can also be used to load
;; data from related refs:

(let [clint
      (d/q '[:find ?e
             :in $ ?name
             :where [?e :person/name ?name]]
           @conn "Clint Eastwood")]
  (d/pull @conn '[*] clint))
; => {:db/id 7, :person/birth-year 1930, :person/name Clint Eastwood}

;; If we have an `e-id`, we can pull the current value of one or more
;; attributes. Here, the `[*]` is our pull-expression.

;; Let's look at another example:

(println "all movies:")
(d/q '[:find (pull ?movie [:movie/title])
       :in $
       :where [?movie :movie/title]] @conn)

;; Without the pull expression, writing `:find ?movie` would return
;; entity IDs. We can wrap `?movie` with `(pull ?movie [:movie/title])`,
;; and instead return attributes from the matching entity.

;; Inside a query, we don't need to quote `*` because the vector
;; passed to `d/q` is already quoted.

;; Let's imagine another scenario, this time getting a list of actors
;; in the movie 'Top Gun.' We'd normally use `d/q`, but we could use
;; `pull`:

(let [top-gun (d/q '[:find ?movie .
                     :in $ ?title
                     :where [?movie :movie/title ?title]]
                   @conn "Top Gun")]
  (d/pull @conn '[* {:movie/actors [:person/name]}] top-gun))

;; This example tells Datascript to pull all attributes on the entity,
;; then also look up the `:movie/actors` refs, and pull their names.

;; We can also look things up in reverse:

(let [clint
      (d/q '[:find ?e .
             :in $ ?name
             :where [?e :person/name ?name]]
           @conn "Clint Eastwood")]
  (d/pull @conn '[* {:movie/_actors [:movie/title]}] clint))

;; NOTE: Here, the leading `_` character tells pull to look up the
;;       values in reverse, that is, every place clint appears
;;       in the value of `:movie/actors`. This says, "Pull all
;;       attributes of Clint Eastwood and then also pull the
;;       title of every movie he acted in."


;; ---------------
;; ---------------
;; Finding results
;; ---------------
;; ---------------


;; Currently, all queries return a set of vectors, which can be awkward when
;; we know the query should evaluate and return a single matching result.
;; As we've alluded to, but haven't explicitly stated, a query returns one
;; or more variables:

(d/q '[:find ?person ?movie :in ...])

;; After `:find`, one or more variables (they start with `?`) are listed. The
;; query returns a set of vectors, where each vector's length matches the
;; number of variables to return.

;; We can customize our results in a number of ways:

(d/q '[:find ?p . :in $ ?name :where [?e :person/name ?name]]
     @conn "Arnold Schwarzenegger")

;; Here, the `.` after `?p` specifies that we want only the first result:

(d/q [:find [?p ...]
      :in $ ?title
      :where [?mov :movie/actors ? p] [?mov :movie/name ?title]]
     @conn "Top Gun")

;; We wrap `?p` in a vector ending with `...` to specify that we want the
;; collection of values `?p` can take on. This is nicer to write compared to:
(map first results)

;; We can also compose a pull by returning a collection:

(d/q '[:find [(pull ?p [:person/name])...]
       :in $ ?name
       :where [?mov :movie/actors ?p] [?mov :movie/title "Top Gun"]]
     @conn)


;; -----------
;; -----------
;; Unification
;; -----------
;; -----------


;; The `:where` clause of a query takes one or more EAVT, and the query returns
;; all values in the DB that satisfy the constraints.

(d/q '[:find (pull ?movie [:movie/title])
       :in $
       :where [?movie :movie/title]]
     @conn)

;; Note that our `:where` clause tuple is short, including only two values
;; corresponding to E and A (of EAVT). This means that the value is
;; unconstrained and can take any value, therefore it returns all
;; `:movie/title` values in the DB.
;; A full `:where` tuple can optionally include all three items (or fewer)
;; in EAV.
;; If we want to see all attributes that take on a specific value:

(d/q '[:find ?a
       :in $ ?tx
       :where [_ ?a "Top Gun"]]
     @conn)

;; The above query finds all attributes that take the value "Top Gun", and
;; it (expectedly) returns `:movie/title`. Note that we use a variable to refer
;; to an attribute.

;; To find all actors in the database:

(d/q '[:find ?act
       :in $
       :where [_ :movie/actors ?act]]
     @conn)

;; The `_` is used idiomatically to mean that the value can be anything, and
;; we don't care about the result. The `?act` param binds to all tuples where
[eid :movie/actor ?act]
;; then our `:find` returns `?act`, so it returns the list of all actors.

;; Here, another query, this time to find all movies where the director acts
;; in the movie:

(d/q '[:find (pull ?p [:person/name])
       :in $
       :where [?mov :movie/actors ?p] [?mov :movie/director ?p]]
     @conn)

;; In the above example, we have employed unification - meaning, a variable
;; must take the same value in all clauses at the same time.

;; Finally, if we wanted to find all people who have ever acted and directed:

(d/q '[:find ?p
       :in $
       :where [?mov1 :movie/actors ?p] [?mov2 :movie/director ?p]]
     @conn)


;; ---------------------
;; ---------------------
;; Predicate expressions
;; ---------------------
;; ---------------------


;; In the last query, our variables `?mov1` and `?mov2` are able to take
;; on separate values. It's still possible for them to return the same value
;; because there's not yet a contraint that the variables must be equal.
;; If we want to add this predicate, we could simply write:

(d/q '[:find
       (pull ?p [:person/name])
       (pull ?mov1 [:movie/title])
       (pull ?mov2 [:movie/title])
       :in $
       :where
       [?mov1 :movie/actors ?p]
       [?mov2 :movie/director ?p]
       [(!= ?mov1 ?mov2)]]
     @conn)


;; -------
;; -------
;; Indexes
;; -------
;; -------


;; To make queries fast, Datascript maintains several indicies, which the query
;; planner uses to optimize queries. These are named `eavt`, `aevt`, and `avet`.
;; Each index is sorted by the order declared.

;; For example, when looking up an actor by name, you'd use a `:where` clause
;; that looks like:
[?p :person/name ?name]
;; We know the attribute and the value (their name), but we need to know the
;; entity ID. Therefore, we traverse the `avet` index, jumping to
;; `:person/name` and then to the value of their name.

;; Most of the time, the query planner does a good job - sometimes we want to
;; get raw access to the datoms, for speed/performance.

(d/datoms @conn :eavt)

;; We can also provide a starting point to filter the results:

(->> (d/datoms @conn :avet :movie/actors)
     (map identity))

;; This will return a sorted set of datoms that use `:movie/actors`.
;; The `d/datoms` param returns a custom iterator, so we use (map identity)
;; to convert the values back to a sequence.


;; ------------------------------------------
;; ------------------------------------------
;; Differences between Datomic and Datascript
;; ------------------------------------------
;; ------------------------------------------


;; While they are both very similar, they aren't 100% compatible, as Datomic
;; has full time travelling. It stores every datom ever seen, so you can query
;; for things like "Show me all values this attribute has ever taken on,
;; and return the transaction IDs when changed."
;; Conversely, Datascript is designed to be in-memory, and as it targets
;; browsers, constant memory use is a must - therefore, there are no historical
;; datoms preserved.


;; ---------------
;; ---------------
;; Why Datascript?
;; ---------------
;; ---------------


;; Our biggest use case for Datascript's power is in managing a large amount of
;; state within a SPA. Not every SPA needs a DB, but when it's necessary in
;; ClojureScript, Datascript is the go-to solution.


;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------


;; --------------------
;; --------------------
;; Improving load times
;; --------------------
;; --------------------


;; Our SPAs can quickly balloon into large apps, containing upwards of 1MB of
;; JavaScript, which brings with it a host of concerns about download speed,
;; parsing, and page speed. ClojureScript provides us with a few tricks to help
;; improve our load time and reduce the download size of our SPA.


;; ---------------------
;; ---------------------
;; ClojureScript modules
;; ---------------------
;; ---------------------


;; ClojureScript modules are a Google Closure Compiler option for breaking
;; apart a ClojureScript application into multiple `.js` files. This helps us
;; avoid loading unnecessary files by ensuring that only what needs to be
;; downloaded to the client for any given page render is served.


;; ---------------------
;; ---------------------
;; Preparing for modules
;; ---------------------
;; ---------------------


;; Typically the biggest gains come from splitting third-party libraries into
;; modules because they usually contain more code than our own Om components.

;; ClojureScript modules split at the namespace level, meaning that the
;; contents of a single namespace are all in one module.

;; To experiment with ClojureScript modules, we'll split an app into "inner"
;; and "outer" modules.


;; ---------------
;; ---------------
;; Getting started
;; ---------------
;; ---------------
