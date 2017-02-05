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
