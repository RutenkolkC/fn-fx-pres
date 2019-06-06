(ns example.demo1
  (:require [clojure.pprint]
            [example.css :as css]
            [fn-fx.fx-tree-search :as tree]
            [fn-fx.fx-dom :as dom]
            [fn-fx.diff :refer [component defui render should-update?]]
            [fn-fx.controls :as ui]))
(defmacro do-with-src-def [name form]
  `(do ~form
       (def ~name '~form)))

(do-with-src-def v-box-demo-src
 (defn v-box-demo [{:keys [font]}]
   (ui/v-box
    :children [(ui/label :font font :text "hallo")
               (ui/label :font font :text "welt")
               (ui/button :style-class []
                          :font font
                          :text "ich bin ein knopf")])))

(do-with-src-def v-box-demo-styled-src
 (defn v-box-demo-styled [{:keys [font]}]
   (ui/v-box
    :children [(ui/label :font font :text "hallo")
               (ui/label :font font :text "welt")
               (ui/button :font font
                          :style-class ["rect-button"]
                          :text "ich bin ein fancy knopf")])))
