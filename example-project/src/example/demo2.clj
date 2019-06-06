(ns example.demo2
  (:require [clojure.pprint]
            [example.css :as css]
            [fn-fx.fx-tree-search :as tree]
            [fn-fx.fx-dom :as dom]
            [fn-fx.diff :refer [component defui render should-update?]]
            [fn-fx.controls :as ui]))

(defmacro do-with-src-def [name form]
  `(do ~form
       (def ~name '~form)))

(do-with-src-def anim-button-src
 (defui AnimButton
   (render [this state]
           (ui/v-box
            :children [(ui/h-box
                        :children [(ui/circle :radius 100
                                              :style "-fx-fill: green;"
                                              :on-mouse-clicked {:event :anim-circle-press
                                                                 :fn-fx/include {:fn-fx/event #{:target}}})
                                   (ui/button :style-class ["rect-button"]
                                              :font (:font state)
                                              :text "Animated Styled Buttons?"
                                              :on-mouse-clicked {:event :anim-button-press
                                                                 :fn-fx/include {:fn-fx/event #{:target}}})
                                   (ui/circle :radius 100
                                              :style "-fx-fill: red;"
                                              :on-mouse-clicked {:event :anim-circle-press
                                                                 :fn-fx/include {:fn-fx/event #{:target}}})])
                       (ui/text :text "This is a Text-Node"
                                :font (:font state)
                                :style "-fx-fill: white;"
                                :on-mouse-entered {:event :text-mouse-hover
                                                   :fn-fx/include {:fn-fx/event #{:target}}})]))))
