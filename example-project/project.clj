(defproject fn-fx-meetup-example "0.1"
  :description "fn-fx presentation done IN fn-fx"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [gorillalabs/neo4j-clj "1.0.0"]
                 [http-kit "2.3.0"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [aysylu/loom "1.0.2"]
                 [org.clojure/data.json "0.2.6"]
                 [garden "1.3.9"]
                 [org.apache.pdfbox/pdfbox "2.0.15"]
                 [fn-fx/fn-fx-openjfx11 "0.5.0-SNAPSHOT"]]

  :main ^:skip-aot example.gui
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
