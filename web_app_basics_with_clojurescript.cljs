;; Web Application Basics with ClojureScript
;; =========================================


;; ClojureScript has various approaches to developing on the browser,
;; but being a hosted language with powerful interop, ClojureScript
;; empowers its devs to mirror JavaScript DOM manipulation and event
;; handling habits.

;; However, we can go further - we can take advantage of ClojureScript's
;; more browser-agnostic DOM manipulation and event handling routines.

;; In this section, we'll cover:
;; (1) - Raw DOM manipulation and events handling
;; (2) - Interacting with the browser via Google Closure
;; (3) - Dommy - an idiomatic ClojureScript library for the DOM
;; (4) - Client-side templating in ClojureScript
;; (5) - CSS preprocessors in ClojureScript


;; ----------------------------------------
;; ----------------------------------------
;; Raw DOM manipulation and events handling
;; ----------------------------------------
;; ----------------------------------------


;; Because of its JavaScript interop syntax, much can be done to work with
;; the DOM almost word or word in ClojureScript as we would in JavaScript.

;; For example, to access a DOM's element property, we write:

(-.property a-js-object)

;; To mutate these properties, we use the `set!` function:

(set! (-.property a-js-object))

;; To explore basic, direct DOM manipulation in ClojureScript,
;; I have created a directory titled `raw-dom`

;; To explore including jQuery in an idiomatic ClojureScript way,
;; I have created a directory titled `jq-project`

;; NOTE: I ran into difficulty while creating my `jq-project`, detailed here:
;;       1) To get CIDER, Figwheel, and Emacs working properly, I had to
;;          update my `init.el` file with the following changes:
(require 'cider)
(setq cider-cljs-lein-repl
      "(do (require 'figwheel-sidecar.repl-api)
           (figwheel-sidecar.repl-api/start-figwheel!)
           (figwheel-sidecar.repl-api/cljs-repl))")
;;       2) This change, in conjunction with a correctly configured
;;          `project.clj` file, then allowed me to hop into my `.cljs`
;;          file and run `C-c-M-J` to run `cider-jack-in-clojurescript`

;; NOTE: In order to interact with my code, I was able to type the following
;;       within the Figwheel REPL:
;;       a) cljs.user> (require '[jq-project.core :as jq-project])
;;          ==> nil
;;       b) cljs.user> (dir jq-project.core)
;;          ==> $main-div
;;          ==> app-state
;;          ==> change-the-div!
;;          ==> on-js-reload
;;       c) cljs.user> (jq-project/change-the-div!)
;;          ==> #object[r [object Object]]
;;          (IMPORTANT: At this point, my function ran and the browser updated)

;; -------------------------------------------------------------
;; -------------------------------------------------------------
;; Interacting with the browser using the Google Closure Library
;; -------------------------------------------------------------
;; -------------------------------------------------------------

;; To work with the Advanced Compilation mode in ClojureScript, and to use
;; the Google Closure Libraries (`goog.dom`, `goog.events`, and `goog.style`),
;; I have created a new project in the directory `raw-goog`

;; Working with the Google Closure Library in the way we did in `raw-goog`
;; is fine, but there's a more idiomatic way available via a library
;; called `Domina`, `https://github.com/levand/domina`, which abstracts
;; the Google Closure Library in a Clojure-esque way.

;; For more, I created a `domina-project` directory

;; ------------------------------------------------------
;; ------------------------------------------------------
;; Dommy - An idiomatic ClojureScript library for the DOM
;; ------------------------------------------------------
;; ------------------------------------------------------

;; Dommy follows a different approach than the methods we've seen so far:
;; 1) raw, native DOM manipulation
;; 2) use of the `jayq` library
;; 3) use of the Google Closure Library
;; and 4) using an idiomatic wrapper around GCL, `domina`

;; Dommy's selection facilities model jQuery's in that we can:
;; (a) select single elements, or (b) multiple DOM nodes

;; Dommy's DOM manipulation routines are inspired by jQuery, but embrace
;; ClojureScript's functional programming style.

;; A simple Dommy project has been created in the directory `dommy-project`

;; ---------------------------------------
;; ---------------------------------------
;; Client-side templating in ClojureScript
;; ---------------------------------------
;; ---------------------------------------

;; There are two main approaches to HTML templating in Clojure:
;; 1) Hiccup (`https://github.com/weavejester/hiccup`)
;; 2) Enlive (`https://github.com/cgrand/enlive`)

;; The client-side implementation of Hiccup is Hipo, source available
;; here, at: `https://github.com/jeluard/hipo/`

;; Before we get into it in detail, let's review basic Hiccup syntax:

;; Ex. <div class="a-class">some-text</div>
[:div {:class "a-class"} "some-text"]

;; We can also chain CSS identifiers and class in Hiccup to have a more
;; concise syntax presentation:

;; Ex. <div id="a-div" class="class1 class2">some-text</div>
[:div#a-div.class1.class2 "some-text"]

;; It is possible to create user interfaces using ClojureScript sequences:
[:ul
 (for [x ["item1" "item2" "item3" "item4"]]
   [:li x])]

;; To explore this further, I've created a directory titled `hipo-project`

;; Whereas Hiccup is the de facto templating language for ClojureScript,
;; Enlive includes a set of DOM selection facilities, coupled with
;; transformations that can be applied to any DOM element (once selected).

;; To dive into Enlive, we'll look at its client-side counterpart, Enfocus,
;; in the directory titled `enfocus-project`

;; ----------------------------------
;; ----------------------------------
;; CSS preprocessors in ClojureScript
;; ----------------------------------
;; ----------------------------------

;; CSS preprocessors (like Stylus, SASS, Less, etc) are common place in
;; front-end development, essential to a scalable, cross-browser
;; CSS codebase. Generally speaking, these tools are server-side, but with
;; Node.js rising to such widespread use, client-side CSS preprocessors
;; can be an option to explore.

;; One such option for ClojureScript development is Garden:
;; `https://github.com/noprompt/garden`

;; The syntax is very Hiccup-like, as the example below illustrates:
[:div :a {:border-style "solid"}]
; => div, a {
; =>   border-style: solid;
; => }

[:mydiv [:p {:background-color "yellow"}]]
; => mydiv {
; =>   p {
; =>     background-color: yellow;
; =>   }
; => }

[:mydiv {:border-style "solid"}
 [:&:hover
  {:border-style "dashed"}]]
; => mydiv {
; =>   border-style: solid;
; =>
; =>   &:hover {
; =>     border-style: dashed;
; =>   }
; =>
; => }

[:.box {:-moz {:border-radius "3px"
               :box-sizing "border-box"}}]
; => .box {
; =>   -moz-border-radius: 3px;
; =>   -moz-box-sizing: border-box;
; => }

;; Garden allows media-queries using `at-media`:
(require '[garden.stylesheet :refer [at-media]])
(css (at-media {:screen true} [:h1 {:font-weight "bold"}]))
; => "@media screen{h1{font-weight:bold}}"

;; For more, I have explored a bit with Garden in the directory titled
;; `garden-project`
