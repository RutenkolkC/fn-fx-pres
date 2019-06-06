(ns example.css
  (:require [garden.core :as garden]))
(def main-font-size 24)
(defn generate []
  (println
    (garden/css {:output-to "resources/main.css"}
      [:* {:primary     "#2A2E37"
           :secondary   "#FFFF8D"
           :primarytext "#B2B2B2"
           :blue        "#1976D2"
           :red         "#FF0000"
           :color-1     "#1976D2"}]
      [:.root * {:-fx {:background-color :primary}}]
      [:.label {:-fx {:background-color "derive(primary, 20%)"
                      :text-fill "derive(secondary, 20%)"}}
       [:&:hover   {:-fx {:text-fill :secondary}}]
       [:&:focused {:-fx {:text-fill :secondary}}] ]
      [:.text-field {:-fx {:background-color "derive(primary, 20%)"
                           :text-fill "derive(secondary, 20%)"}}
        [:&:hover   {:-fx {:text-fill :secondary}}]
        [:&:focused {:-fx {:text-fill :secondary}}] ]
      [:.image-view {:-fx {:pref-width "10%"}}]

      [:.open-bracket {:-fx {:fill :magenta}}
       [:&:hover {:-fx {:scale-x 1.2 :scale-y 1.2}}]]
      [:.close-bracket {:-fx {:fill :magenta}}
       [:&:hover {:-fx {:scale-x 1.2 :scale-y 1.2}}]]
      [:.open-square-bracket {:-fx {:fill :magenta}}
       [:&:hover {:-fx {:scale-x 1.2 :scale-y 1.2}}]]
      [:.close-square-bracket {:-fx {:fill :magenta}}
       [:&:hover {:-fx {:scale-x 1.2 :scale-y 1.2}}]]
      [:.open-curly-bracket {:-fx {:fill :magenta}}
       [:&:hover {:-fx {:scale-x 1.2 :scale-y 1.2}}]]
      [:.close-curly-bracket {:-fx {:fill :magenta}}
       [:&:hover {:-fx {:scale-x 1.2 :scale-y 1.2}}]]
      [:.function {:-fx {:fill :aqua}}
       [:&:hover {:-fx {:scale-x 1.2 :scale-y 1.2}}]]
      [:.keyword {:-fx {:fill :orange}}
       [:&:hover {:-fx {:scale-x 1.2 :scale-y 1.2}}]]
      [:.str {:-fx {:fill :green}}
       [:&:hover {:-fx {:scale-x 1.2 :scale-y 1.2}}]]
      [:.nbr {:-fx {:fill :lightgreen}}
       [:&:hover {:-fx {:scale-x 1.2 :scale-y 1.2}}]]
      [:.clojure-default {:-fx {:fill :white}}
       [:&:hover {:-fx {:scale-x 1.2 :scale-y 1.2}}]]

      [:.rect-button
       {:-fx {:text-fill :primarytext
              :background-color :primary
              :border-color :secondary
              :border-radius 0
              :background-radius 0}}
        [:&:hover {:-fx {:background-color :secondary
                         :text-fill :primary}}]])))
