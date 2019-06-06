(defproject fn-fx-meetup-example "0.1"
  :description "fn-fx presentation done IN fn-fx"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [aysylu/loom "1.0.2"]
                 [garden "1.3.9"]
                 [fn-fx/fn-fx-openjfx11 "0.5.0-SNAPSHOT"]]

  :main ^:skip-aot example.gui
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
