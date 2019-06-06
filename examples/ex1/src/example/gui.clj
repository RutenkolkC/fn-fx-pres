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


(comment
  ------------------------------------------------------------------------------
  Please use the custom component definition below for easy-going.
  ------------------------------------------------------------------------------)
(defui YourCustomComponentHere
  (render [this state]
    (ui/v-box
      :children [(ui/label :text "hello :)")])))
(comment
  ------------------------------------------------------------------------------
  Please use the custom component definition above for easy-going.
  ------------------------------------------------------------------------------)



(defui MainWindow
  (render [this state]
    (ui/h-box
      :children
        [(ui/v-box
          :h-box/hgrow javafx.scene.layout.Priority/ALWAYS
          :children [(your-custom-component-here state)])])))

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
  (println "No handler for event " (:type event) event)
  state)

(defn -main []
  (let [_ (css/generate)
        data-state (atom {:fullscreen? false})
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
