(ns example.gui
  (:require [clojure.pprint]
            [example.css :as css]
            [fn-fx.fx-tree-search :as tree]
            [fn-fx.fx-dom :as dom]
            [fn-fx.diff :refer [component defui render should-update?]]
            [fn-fx.controls :as ui]))

(comment
  This is an excercise project that you can use to play around.
  you might find useful stuff in here that might help you.

  Have a look at the macros below for example.
  If you are familiar with the JavaFX Scenebuilder, you might know the
  "fit-to-parent" functionality of anchor-panes.
  It allows you to stretch the child node to the edges of the parent node.
  This however is somewhat cumbersome to type out in pure Java Code, or
  fn-fx code for this matter. But as clojure-programmers we are lucky.
  We can simply use macros to eliminate these inconveniences.)

(defmacro fit-to-parent [node]
  (concat
    node
    [:anchor-pane/top-anchor    0.0
     :anchor-pane/bottom-anchor 0.0
     :anchor-pane/left-anchor   0.0
     :anchor-pane/right-anchor  0.0]))
(defmacro fit-to-parent-anchor [& args]
  (let [prop-vals (drop-last args)
        node (last args)]
   (concat
     `(ui/anchor-pane
        :children)
     [[(concat node [:anchor-pane/top-anchor    0.0
                     :anchor-pane/bottom-anchor 0.0
                     :anchor-pane/left-anchor   0.0
                     :anchor-pane/right-anchor  0.0])]]
     prop-vals)))
(macroexpand-1
 '(fit-to-parent-anchor
   :anchor-prop-val :some-value
   :anchor-prop-val :some-value
  (ui/blubb :stuff :val)))
(defui MainWindow
  (render [this state]
          (ui/grid-pane
           :alignment :center
           :children [(ui/label
                       :text "Street:"
                       :grid-pane/column-index 0
                       :grid-pane/row-index 0)

                      (ui/text-field
                       :id :street-field
                       :grid-pane/column-index 1
                       :grid-pane/row-index 0)

                      (ui/label
                       :text "House:"
                       :grid-pane/column-index 0
                       :grid-pane/row-index 1)

                      (ui/text-field
                       :id :house-field
                       :grid-pane/column-index 1
                       :grid-pane/row-index 1)

                      (ui/h-box
                       :spacing 10
                       :alignment :bottom-right
                       :children [(ui/button :text "search for address"
                                             :on-action {:event :adress-search
                                                         :fn-fx/include {:street-field #{:text}
                                                                         :house-field #{:text}}})]
                       :grid-pane/column-index 1
                       :grid-pane/row-index 2)

                      #_(--------------------------------------------------------------------------------
                         Try to conditionally create components based on the state.
                         To modify the state you should implement a handle-event method
                         for the event :adress-search
                         --------------------------------------------------------------------------------)

                      ])))

(defui Stage
  (render [this args]
    (ui/stage
      :title "Example"
      :min-height 800
      :min-width  600
      :shown true
      :on-shown {:event :app-start
                 :fn-fx/include {:fn-fx/event #{:target}}}
      :always-on-top true
      :full-screen (:fullscreen? args)
      :scene (ui/scene
               :stylesheets ["main.css"]
               :on-key-pressed {:event :key-was-pressed-on-scene
                                :fn-fx/include
                                  {:fn-fx/event
                                    #{:target :text :code
                                      :control-down? :shift-down?
                                      :meta-down? :alt-down?}}}
               :root (main-window args)))))

(defmulti handle-event (fn [state event]
                          (:event event))) ;dispatch simply on the :event for now

(comment
  Note that this project has some events flying around, that are not handled yet.
  You might want to implement your own handle-event method.
  Just keep a look at the repl for unhandled events.)

(defmethod handle-event :you-event-type-here
  [state event]
  (comment
    here you can safely react to an event and update the state)
  state)

(defmethod handle-event :default
  [state event]
  (println "no handler for event " (:type event))
  (clojure.pprint/pprint  event)
  state)

(defn -main []
  (let [_ (css/generate)
        data-state (atom {:fullscreen? false
                          :authed? false})
        _ (def peekabo data-state)

        ;; handler-fn handles events from the ui and updates the data state
        handler-fn (fn [event]
                     (try
                       (swap! data-state handle-event event)
                       (catch Throwable ex
                         (println ex))))

        ;; ui-state holds the most recent state of the ui
        ui-state   (agent (dom/app (stage @data-state) handler-fn))]

    ;; Every time the data-state changes, queue up an update of the UI
    (add-watch data-state :ui (fn [_ _ _ _]
                                (send ui-state
                                      (fn [old-ui]
                                        (try
                                          (dom/update-app old-ui (stage @data-state))
                                          (catch Throwable ex
                                            (println ex)))))))))
(comment
  @peekabo
  (clojure.pprint/pprint @peekabo)
  (-main))
(-main)
