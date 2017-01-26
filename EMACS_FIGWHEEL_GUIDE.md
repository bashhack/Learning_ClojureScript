# How-To Guide - Emacs Cider Figwheel Configuration and Setup
- - -


1. In the root of the CLJS project, `C-c M-j` opens my
   'cider-jack-in-clojurescript' command

2. Once the nREPL is loaded, at `user>` prompt, type: `(use 'figwheel-sidecar.repl-api)`

3. Return value from step 2 should be `nil`, at which point, type: `(start-figwheel!)`
   to initialize figwheel on default port (i.e., '3449'), running the project's 'core.cljs'
   file at 'src/project_name/core.cljs'
