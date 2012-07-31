(ns soundcljs.core
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [ring.util.serve :as server]
            [cljs.closure :as closure]))

(defroutes app-routes
  (route/files "/") ;; defaults to /public
  (route/not-found "Not Found"))

(def app
  (handler/site #'app-routes))

(defn -main [& m]
  (server/serve app))

(defn cljs-compile []
  (closure/build "src-cljs" {:optimizations :simple
                             :pretty-print true
                             :output-dir "public/javascripts"
                             :output-to "public/javascripts/main.js"
                             :foreign-libs [{:file "http://connect.soundcloud.com/sdk.js"
                                             :provides ["soundcloud"]}]}))

(comment
  ;; compile cljs code
  (cljs-compile)
  ;; Start a cljs repl (other process)
  (do
    (require '[cljs.repl :as repl])
    (require '[cljs.repl.browser :as browser])
    (repl/repl (browser/repl-env)))
  ;; or in emacs-lisp
  (require 'clojurescript-mode)
  (defun cljs-repl ()
    (interactive)
    (let ((browser-repl (expand-file-name "script/browser-repl" clojurescript-home)))
      (inferior-lisp browser-repl)))
  ;; serve the app and connect into the repl backend
  (server/serve app)
  )
