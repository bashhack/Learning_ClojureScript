;; Building Single Page Applications
;; =================================


;; Here, we'll get into the nitty gritty of building
;; a Single Page Application (SPA) using Om and React.js
;; In total, we'll cover:
;; - Om and React.js
;; - Routing using bidi
;; - HTML5 history and pushState
;; - REST APIs using AJAX


;; --------------------------------------
;; --------------------------------------
;; Understanding Single Page Applications
;; --------------------------------------
;; --------------------------------------


;; Advantages:
;; - They provide a richer UI
;; - Easier to deal with client-side state and data
;; - Easier to deal with AJAX
;; - Faster client interactions once the page is loaded

;; Disadvantages:
;; - More development work
;; - Initial page load is usually slower
;; - Legacy browser support is harder

;; For this deep dive, our project will live in the `om-tut` directory

;; -------------------------------------
;; Items in the Om constructor signature
;; -------------------------------------

;; Let's take a moment to get more familiar with some of the idea, concepts,
;; and terms we're using in our project so far:

;; Cursors
;; =======
;; Cursors are a custom Om datatype, used to pass a piece of `app-state` data
;; around the app, while staying in sync with `app-state` and keeping track
;; of where the data originated.

;; Without cursors, we would just write:
(get-in @app-state [:todos 1])

;; Cursors, though, have an advantage: they keep track fo the path into
;; app-state where the data came from.

;; Since cursors track the original data, a new cursor may be generated when
;; the data is updated, and the reference will stay in sync.

;; The cursor is really a two-part data structure, made of the original atom
;; and a path. The path is the vector we would use to access nested data with
;; `get-in` from the root of app-state. In our example, this would be:
[:todos 1]
;; because:
(get-in @app-state [:todos 1])

;; Cursors are useful for reusable components. We can have multiple components
;; with data all coming from disparate locations in `app-state` (ex., each of
;; our todo items is rendered the same way, but they have different paths
;; because:
(not= [:todos 0] [:todos 1])

;; Owner
;; =====
;; `owner`, passed into an Om constructor function is the DOM element where
;; the component will be rendered.

;; Opts
;; ====
;; `Opts` is just extra data you may pass to a constructor function.
;; Opts is the only place to pass extra arguments because the function
;; signature is fixed:
[cursor owner]
[cursor owner opts]
;; Opts are passed into a component constructor via the `:opts` option in
;; `om/build` and `om/build-all`

;; Ex.
(om/build foo cursor {:opts {:bar 42}})

;; Input
;; =====
;; We're going to modify our app again, this time adding user input via
;; checkboxes so we can mark items in our todo list as completed.

;; See `om-tut` directory for code....

;; Rendering
;; =========
;; The most complex part of the equation is likely rendering, let's cover
;; that here by exploring how React works.

;; ---------------------------
;; The React diffing algorithm
;; ---------------------------

;; React lets us pretend to do what would be impossibly slow:
;; to re-render the whole of our app on each change.
;; To do this, React diffs the changes and only then inserts/modifies DOM nodes
;; only as necesary.

;; The diffing of two arbitrary trees is O(n^3) in the best case, but React
;; gets this down to O(n) via several heuristics.

;; When `om/root` is called, React creates a virtual DOM tree, and with no diff
;; renders the whole tree under `#app`, creating a real DOM element for each
;; element in our virtual tree. React then saves a copy of the virtual tree
;; for diffing later.

;; On changes to our app, React calls render on the root (the one passed to
;; `om/root`) and builds up a new virtual DOM tree.

;; The algorithm recursively walks down both the current virtual DOM tree and
;; the new tree (with the change). If two items are DOM nodes of different
;; classes, React will destroy the old and insert the new.

;; If two nodes are both DOM nodes (not components), React will diff their
;; attributes and insert and update attributes as necessary.

;; If two nodes are both components of the same React/Om class, React will call
;; `componentWillReceiveProps` and `componentDidReceiveProps` as necessary.
;; These functions are React-specific - and they are not exposed to Om's user.
;; This process mutates the existing real DOM node until it looks identical to
;; the virtual DOM node.

;; --------------------------------
;; Differences between om and React
;; --------------------------------

;; Components
;; ==========
;; Om components are not strictly React components. Om creates a hidden object
;; that implements React's interface, and that interface delegates to the
;; reified object returned from an Om component constructor.

;; State models
;; ============
;; Om is strongly opinionated about state and where it's stored.
;; Every component on the page reads and writes to the single source of state:
;; the application state atom.

;; Cursors
;; =======
;; Cursors are a new feature unique to Om and have no React counterpoint. A
;; cursor is a piece of the application state that knows how to update itself.

;; -----------------------------------
;; Determining the size of a component
;; -----------------------------------

;; The best advice is to split components at logical divisions in the
;; app state. Components should only receive the part of the state they need,
;; and doing so keeps things reusable and improves clarity about the structure
;; of the app.

;; ------------
;; Constructing
;; ------------

;; The `om/build` and `om/build-all` function have the same signature:
[constructor cursor options]

;; There are several options that can be passed in:
;; - `-:key` - The key passed in will be used to look up a unique ID in the
;;             cursor's  data for the component.

(om/build todo-item t {:key :id})

;; Here, Om will then go grab:
(get cursor :id)

;; In our app, the item maps contain:
{:id 1}
{:id 2}
;; ...so the components would have IDs of 1 and 2, respectively.

;; We could also pass the following options:
;; - `react-key` - Same as `:key` but directly passes a value rather than
;;    looking up in `:key` - that is, `{:react-key 1}` would use 1 as an
;;    ID rather than the result of `(get cursor :id)
;; - `:fn` - This applies a function f to the cursor's value before invoking
;;    the constructor, here's an example:
(om/build todo
          (first (:todos cursor)) {:fn (fn [todo] (assoc-in todo :done true))})
;;    This is essentially the same as:
(om/build todo (assoc (first (:todoes cursor)) :done true))
;; - `:init-state` - This is the extra initial state for the component.
;; - `:state` - This is the same as `:init-state`, but will also be used to
;;    modify a component (whereas the `:init-state` will only be used when a
;;    component is created).
;; - `:opts` - This is the extra data to be passed to the constructor fn. This
;;    should be a map, and if this option is supplied, the constructor fn
;;    will be called with `[data owner options]` rather than `[data owner]`.

;; ---------------
;; The local state
;; ---------------

;; While Om is opinionated that app state be consistent and stored in a single
;; place, components have access to component local state. There is often
;; confusion as to what the component local state should be used for, but
;; David Nolan has clarified this for us:
;; "With the exception of transient values (editing flags, dragging flags) or
;; resources (channels, web sockets, and so on), you should put everything into
;; the application state."
;; For example, if our component creates a go channel to process user input
;; events, the local state is a good place that channel.

;; There are several API functions that touch the local state:
(defn get-state
  ([owner] ...)
  ([owner korks] ...))

;; NOTE: `korks` is a common idiom in Om, which stands for "key or keys."

;; If `korks` is singular, it behaves like `get`:
(om/get-state :foo)
;; If `korks` is a collection, it behaves like `get-in`:
(om/get-state owner [:foo :bar])

;; For functions that set state, `korks` works the same way but uses
;; `assoc/assoc-in`:

(defn set-state!
  ([owner] ...)
  ([owner korks v] ...))

;; Setting the entire component local state:
(om/set-state! owner {:foo 42})

;; Setting a part of component local state:
(om/set-state! owner [:foo] 42)

(defn update-state!
  ([owner f] ...)
  ([owner korks f] ...))

;; NOTE: `set-state!` and `update-state!` will cause re-rendering

;; --------------------
;; Life cycle protocols
;; --------------------

;; In addition to the required `om/IRender` protocol, there are several
;; protocols that notify a component of various lifecycle and state changes:

(defprotocol IInitState
  (init-state [this]))

;; If a component uses the local state, `init-state` can be used to supply an
;; initial value. To use this, our component should implement `om/InitState`.
;; The `init-state` will be called on our component when created, and should
;; return a map. `om/build` can pass `:init-state` in as an option, in which
;; case, the data from `:init-state` will be merged with the value returned by
;; `init-state`.

(defprotocol IWillMount
  (will-mount [this]))

;; The `will-mount` function is called when a component is going to be
;; inserted into the DOM. It is a good place to set up and create non-DOM
;; resources like `core.async` channels because it is the earliest point
;; in the lifecycle where we know the component will be mounted.

(defprotocol IDidMount
  (did-mount [this]))

;; If our component implements the protocol, `did-mount` will be called after
;; the component is inserted into the DOM. At this point, the DOM node exists,
;; and this is a good place to set up code that interacts with the DOM.

(defprotocol IWillUnmount
  (will-unmount [this]))

;; The `will-unmount` function will be called when a component is removed
;; from the DOM, for example, after the diffing algorithm removes a component.
;; This should be used to clean up any resources created during `will-mount`
;; or `did-mount`.

(defprotocol IWillReceiveProps
  (will-receive-props [this next-props]))

;; The `will-receive-props` function is not called on the first render, but
;; it is called just before all subsequent renders. In React, props and state
;; are different concepts. Props are for immutable data that will never change
;; during the lfietime of the component, and state if for data that can change.
;; In Om, there are no props, just the app-state and component local state, so
;; in this context, `will-receive-props` is somewhat poorly named.

;; Use `will-receive-props` to react to state changes with additional state
;; changes. For example, if we have two pieces of data that should stay in sync
;; and a user action has changed one of them.

;; NOTE: Use `(om/get-props owner)` to get the current state.

(defprotocol IWillUpdate
  (will-update [this next-props next-state]))

;; The `will-update` function is not called on the first render, but it is
;; called just before all subsequent renders.
;; The `will-update` function is called after `will-receive-props`.
;; We cannot set the local state during `will-update`!

(defprotocol IDidUpdate
  (did-update [this prev-props prev-state]))

;; This is called after rendering the component. The DOM element is fully
;; rendered when this is called, so it's useful for third-party interop if
;; the JS lib needs to mutate in response to your React component re-rendering:

(defprotocol IShouldUpdate
  (should-update [this next-props next-state]))

;; The `should-update` function should ONLY be used if you really know
;; what you're doing - and, even then, you shouldn't.

;; IMPORTANT: The entire render timeline looks like this:
(WillReceiveProps -> ShouldUpdate -> WillUpdate -> Render -> DidUpdate)

;; --------------------
;; Using third-party JS
;; --------------------

;; We cannot simply mix React and standard JS libraries and expect the DOM
;; to stay consistent. In order to pull this off, we'll take advantage of
;; `IDidMount`:

;; Ex. Incorporating Leaflet.js (http://leafletjs.com/) - an open source JS
;;     library for displaying `OpenStreetMap` data
(defn leaflet-map
  [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:the-map nil})
    om/IDidMount
    (did-mount [_]
      (let [the-map (js/L.map "map")]
        (om/set-state! owner :the-map the-map)))
    om/IRender
    (render [this]
      (dom/div {:id "map"} nil))))

;; Here, in render, we create an empty div with the ID `map`. During
;; `IDidMount`, after the div is rendered into the page, we call Leaflet,
;; instructing it to render into `#map`. We then save the resulting map
;; into local state, in ase we want to manipulate it later.

;; jQuery listeners
;; ================
;; Don't mix jQuery and React!!!!

;; AJAX
;; ====
;; Let's expand our Todo app - we want to persist our todo items on
;; the server. To get this done only takes a simple Clojure REST API.
;; We'll be using a few new dependencies to accomplish this task:
;; - `bidi`
;; - `cljs-ajax`
;; - `clj-http`
;; - `com.cognitect/transit-clj`
;; - `ring`
;; - `ring/ring-jetty-adapter`

;; REMINDER: All new udates are in the `om-tut` directory

;; ------------
;; ------------
;; Dependencies
;; ------------
;; ------------

;; -------
;; Transit
;; -------

;; Transit is a standardized serialization format developed by the Cognitect
;; team (i.e, David Nolan, Rich Hickey, etc.). Transit is a high-performance,
;; compact, and extensible format for transferring data between apps. If your
;; business reqs don't require another encoding such as JSON, and one or both
;; ends of the communication are Clojure/ClojureScript, then Transcipt is the
;; way to go!

;; ----
;; Ring
;; ----

;; Ring presents a standard interface between web applications and HTTP
;; servers. Ring defines a data format for an HTTP request and HTTP response.
;; A Ring request is a Clojure map with a set of required and optional keys,
;; such as `:uri`, `:remote-addr`, and `:query-string`. A Ring response
;; is another map with another set of required and optional keys, such as
;; `:status`, `:body`, and `:headers`

;; Ring makes web application development easier because our Clojure app only
;; has to write functions that receive a Ring request map and return a Ring
;; response map. Ring isolates us from messy Java code and makes our app
;; align with functional programming. Ring is only used for handling HTTP
;; requests, it doesn't concern itself with DB access or generating HTML.

;; The spec can be viewed here:
;; `https://github.com/ring-clojure/ring/blob/master/SPEC`

;; ----
;; CORS
;; ----

;; Cross Origin Resource Sharing (CORS) is an HTTP standard for declaring
;; who is allowed access to an HTTP resource. Browsers won't allow AJAX calls
;; to hosts other than the one that served the current page unless the
;; responses contain specific headers.

;; Because we're using Ring, our server's response is just a Clojure map.
;; We will update the `:headers` map in the response to include more
;; HTTP headers.

;; To ensure that a valid Ring response is marked as Transit data,
;; some modifications need to take place (see: `om-tut` `server.clj` at
;; `transit-response` function). In the aforementioned function, we'll
;; set the `Content-Type` header to Transit if it isn't already present. We
;; will then set teh HTTP status to 200, serialize the body to Transit
;; format, and add CORS headers.

;; ----
;; Data
;; ----

;; While our Todo app would likely use a database for a production application,
;; for now we'll use an in-memory atom - a map of IDs (integers) to todo maps.
;; We'll create `ids`, another atom that increments an integer to guarantee
;; that todos get unique IDs.

;; For now, we'll continue with our changes in `om-tut` in `server.clj`

;; -------------------------
;; -------------------------
;; Routing and HTML5 history
;; -------------------------
;; -------------------------

;; Our app has some basic functionality, but we can improve it by extending
;; its features to include multiple pages.

;; `bidi` will assist in the client-side routing, as will `venantius/accountant`

;; These latest changes expand upon the existing app in the `om-tut` directory.

;; ---------
;; pushState
;; ---------

;; ----------
;; Navigation
;; ----------
