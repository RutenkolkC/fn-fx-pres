(ns example.demo3
  (:require [clojure.pprint]
            [example.css :as css]
            [fn-fx.fx-tree-search :as tree]
            [fn-fx.fx-dom :as dom]
            [fn-fx.diff :refer [component defui render should-update?]]
            [fn-fx.controls :as ui]))
(def ws (repeat " "))
(defn lp-i [n] (apply str (take n ws)))
(def lp (memoize lp-i))
(defn nl [font] (ui/text :text "\n" :font font))
;hacky pretty printer :)
(defn clj-tok [form indent only-this font]
  (cond
    (= form nil) (ui/text)
    (= form :__NEWLINE__) (nl font)

    (list? form)
    [(ui/text :font font :text (str (lp only-this) "(") :style-class ["open-bracket"])
     (ui/text :font font :text (str (first form) " ") :style-class ["function"])
     (map #(clj-tok % (+ indent 3) (+ indent 3) font) (interleave
                                                          (repeat :__NEWLINE__) (rest form)))
     (ui/text :font font :text (str ")") :style-class ["close-bracket"])]

    (vector? form)
    (if (empty? form)
      [(ui/text :font font :text (str (lp only-this) "[") :style-class ["open-square-bracket"])
       (ui/text :font font :text "]" :style-class ["close-square-bracket"])]

      [(ui/text :font font :text (str (lp only-this) "[") :style-class ["open-square-bracket"])
       (clj-tok (first form) (inc indent) 0 font)
       (map #(clj-tok % (inc indent) (inc indent) font) (interleave (repeat :__NEWLINE__) (rest form)))
       (ui/text :font font :text "]" :style-class ["close-square-bracket"])])

    (map? form)
    [(ui/text :font font :text (str (lp only-this) "{") :style-class ["open-curly-bracket"])
     (if (first form) (clj-tok (first (first form)) indent 0 font) (ui/text))
     (if (first form) (clj-tok (second (first form)) indent 0 font) (ui/text))
     (flatten
      (interleave
       (map (fn [_] (nl font)) (rest form))
       (map (fn [[k v]] [(clj-tok k (inc indent) (inc indent) font) (clj-tok v (inc indent) 0 font)]) (rest form))))

     (ui/text :font font :text "}" :style-class ["close-curly-bracket"])]
    (keyword? form)
      (ui/text :font font :text (str (lp only-this) form " ") :style-class ["keyword"])

    (string? form)
      (ui/text :font font :text (str (lp only-this) "\"" form "\" ") :style-class ["str"]) 

    (number? form)
      (ui/text :font font :text (str (lp only-this) form) :style-class ["nbr"]) 

    :default
      (ui/text :font font :text (str (lp only-this) form " ") :style-class ["clojure-default"]) 
    ))
(defui CodeSlide
  (render [this [txt font]]
          (ui/text-flow :children (flatten (clj-tok txt 0 0 font)))))

(comment
  (dom/app
    (ui/stage
    :title "AAA"
    :min-height 800
    :min-width  600
    :shown true
    :scene (ui/scene
            :root (code-slide
                   ['(jojojo
                      (+ 1 2
                         {:wat :map
                          :i :want
                          "dem" "pairs"
                          "wrong" (+ indent :for ["this" :probably])} 
                         [:myvec "oh yes"
                          (+ here also wrong (indent? or not?))])
                      "but wat about"
                      {:maps :with
                       (call here) (and here?)}
                      {:or {:some {:nested :maps?
                                   :thats :kinda
                                   {:weird {:isnt {:it :?}}} :no?}}})
                    (ui/font :size 32)])))))
