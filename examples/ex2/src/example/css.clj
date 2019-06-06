(ns example.css
  (:require [garden.core :as garden]))
(defn generate [] 
  #_(defining primary/secondary/tertiary color can be useful if you want to swap out your colorsstyle)
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
    [:.image-view {:-fx {:pref-width "10%"}}]))
