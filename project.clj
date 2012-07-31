(defproject soundcljs "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.1.1"]
                 [ring-serve "0.1.2"] ;; Should be a dev dep, but swank fails.
                 ]
  :plugins [[lein-git-deps "0.0.1-SNAPSHOT"]
            [lein-cljsbuild "0.2.4"]]
  :git-dependencies [["https://github.com/clojure/clojurescript.git"]
                     ["https://github.com/julienfantin/clojure-playground.git"]]
  :source-paths ["src/"
                 "lib/"
                 "lib/dev/"
                 ;; remember to ./script/bootstrap after clojurescript checkout
                 ".lein-git-deps/clojurescript/lib/goog.jar"
                 ".lein-git-deps/clojurescript/lib/compiler.jar"
                 ".lein-git-deps/clojurescript/lib/js.jar"
                 ".lein-git-deps/clojurescript/src/clj/"
                 ".lein-git-deps/clojurescript/src/cljs/"
                 ".lein-git-deps/clojure-playground/src/"]
  :ring {:handler soundcljs.core/app}
  :cljsbuild
  {:builds [{:source-path "src-cljs"
             :compiler {:output-dir "public/javascripts"
                        :output-to "public/javascripts/main.js"
                        :foreign-libs [{:file "http://connect.soundcloud.com/sdk.js"
                                        :provides ["soundcloud"]}]
                        :optimizations :simple
                        :pretty-print true}}]}
  :main soundcljs.core)
