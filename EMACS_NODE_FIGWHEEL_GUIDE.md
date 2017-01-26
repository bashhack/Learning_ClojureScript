# How-To Guide - Emacs Cider Node Figwheel Configuration and Setup
- - -


1. From within the project directory, open a terminal and run `lein figwheel`

2. Open a second terminal instance, running Node: `node target/server_dev/{proj_name}.js`

3. Going back to the first terminal instance, we should now see that our Clojurescript
   REPL prompt appears: `cljs.user=>`

4. In the first terminal instance, we can also establish an HTTP server by writing:

cljs.user=> (def http (js/require "http"))
cljs.user=> (.listen (.createServer http
    #_=>                            (fn [req res]
    #_=>                            (do
    #_=>                            (.writeHead res
    #_=>                            200
    #_=>                            (js-obj
    #_=>                            "Content-Type"
    #_=>                              "text/plain"))
    #_=>                            (.end res
    #_=>                            "Hello World from
    #_=>                              Node.js http server!"))))
    #_=>                            1337
    #_=>                            "127.0.0.1")
