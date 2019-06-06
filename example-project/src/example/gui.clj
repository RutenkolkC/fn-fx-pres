(ns example.gui
  (:require [clojure.pprint]
            [example.css :as css]
            [fn-fx.fx-tree-search :as tree]
            [fn-fx.fx-dom :as dom]
            [fn-fx.diff :refer [component defui render should-update?]]
            [fn-fx.controls :as ui]
            [example.demo1 :as demo1]
            [example.demo2 :as demo2]
            [example.demo3 :as demo3]
            ))

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
        :anchor-pane/top-anchor    0.0
        :anchor-pane/bottom-anchor 0.0
        :anchor-pane/left-anchor   0.0
        :anchor-pane/right-anchor  0.0
        :children)
     [[node]]
     prop-vals)))

(def main-font (ui/font :family "Helvetica" :size 16))
;i have no idea currently how fn-fx does the autogeneration of the diff-component stuff.
;and there is no doc. So I have not the slightest idea how to get the javafx-font obj from (ui/font)
(def main-fx-font (javafx.scene.text.Font. "Helvetica" 16))

;switching to java-11 from java-8 was a surprise as my beloved FontMetrics are no longer available :(
(defn string-dims
  ([font string]
   (let [text-node (javafx.scene.text.Text.)
         _ (.setFont text-node font)
         _ (.setText text-node string)]
     {:width (-> text-node .getLayoutBounds .getWidth)
      :height (-> text-node .getLayoutBounds .getHeight)}))
  ([font string [x-padding y-padding]]
   (let [text-node (javafx.scene.text.Text.)
         _ (.setFont text-node font)
         _ (.setText text-node string)]
     {:width (+ x-padding x-padding (-> text-node .getLayoutBounds .getWidth))
      :height (+ y-padding y-padding (-> text-node .getLayoutBounds .getHeight))})))

(defn load-images-from-pdf! [path]
  (let [document (org.apache.pdfbox.pdmodel.PDDocument/load (clojure.java.io/file path))
        pdfRenderer (org.apache.pdfbox.rendering.PDFRenderer. document)]
    (doall
      (pmap
        (fn [page]
          (javafx.embed.swing.SwingFXUtils/toFXImage
            (.renderImageWithDPI pdfRenderer page 300
                                 org.apache.pdfbox.rendering.ImageType/RGB)
            nil))
        (range (.getNumberOfPages document))))))

(defn view-by-key [index state]
  (cond
    (= index 0) 
      (if (:images state) 
                      [(ui/anchor-pane 
                        :v-box/vgrow javafx.scene.layout.Priority/ALWAYS
                        :listen/height {:event :image-anchor-height-change}
                        :listen/width {:event :image-anchor-width-change}
                        :children [(ui/image-view 
                          :anchor-pane/top-anchor    0.0
                          :anchor-pane/bottom-anchor 0.0
                          :anchor-pane/left-anchor   0.0
                          :anchor-pane/right-anchor  0.0
                          :smooth true
                          :fit-width (:image-anchor-width state)
                          :fit-height (:image-anchor-height state)
                          :image (nth (:images state) (:image-index state)))])] 
                      [(ui/anchor-pane
                        :v-box/vgrow javafx.scene.layout.Priority/ALWAYS
                        :children
                        [(fit-to-parent
                            (ui/label :text "no-image-loaded yet"))])])
    (= index 1) [(demo1/v-box-demo state)]
    (= index 2) [(demo1/v-box-demo-styled state)]
    (= index 3) [(demo2/anim-button state)]
    (= index 4) [(demo3/code-slide ['(jojojo
                                      (+ 1 2
                                         {:wat (foobar :this "may" break)
                                          :i :want
                                          "dem" "pairs"}
                                         [:myvec "oh yes"]))
                                    (:font state)])]
    (= index 5) [(demo3/code-slide [demo1/v-box-demo-styled-src
                                    (:font state)])]
    (= index 6) [(demo3/code-slide [demo2/anim-button-src
                                    (:font state)])]
    (= index 7) [(demo3/code-slide ['(let [data-state (atom {:foo :bar})
                                           handler-fn (fn [event]
                                                        (try
                                                          (swap! data-state handle-event event)
                                                          (catch Throwable ex
                                                            (println ex))))])
                                    (:font state)])]
    (= index 8) [(demo3/code-slide ['(let [ui-state (agent (dom/app (stage @data-state) handler-fn))
                                           _ (add-watch
                                              data-state
                                              :ui
                                              (fn [_ _ _ _]
                                                (send ui-state
                                                      (fn [old-ui]
                                                        (try
                                                          (dom/update-app old-ui
                                                                          (stage @data-state))
                                                          (catch Throwable ex (println ex)))))))])
                                    (:font state)])]

    :default
    [(fit-to-parent-anchor
      :v-box/vgrow javafx.scene.layout.Priority/ALWAYS
      (ui/label :text (str "there is no view with key " index)))]))

(defui MainWindow
  (render [this state]
    (ui/h-box
      :children
        [(ui/v-box
          :h-box/hgrow javafx.scene.layout.Priority/ALWAYS
          :padding (ui/insets :top-right-bottom-left (:inset-size state))
          :children (view-by-key (:view-index state) state))])))

(defui Stage
  (render [this args]
    (ui/stage
      :title "java architecture tool"
      :min-height 800
      :min-width  600
      :listen/height {:event :height-change}
      :listen/width {:event :width-change}
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

(defmethod handle-event :width-change
  [state event]
  (assoc state :width (event :fn-fx.listen/new)
         :image-anchor-width (if (< (event :fn-fx.listen/new) 
                                    (event :fn-fx.listen/old))
                                    1.0
                                    (state :image-anchor-width))))

(defmethod handle-event :height-change
  [state event]
  (assoc state :height (event :fn-fx.listen/new)
         :image-anchor-height (if (< (event :fn-fx.listen/new) 
                                    (event :fn-fx.listen/old))
                                    1.0
                                    (state :image-anchor-height))))


(defmethod handle-event :image-anchor-width-change
  [state event]
  (assoc state :image-anchor-width (event :fn-fx.listen/new)))

(defmethod handle-event :image-anchor-height-change
  [state event]
  (assoc state :image-anchor-height (event :fn-fx.listen/new)))

(defn pulse-anim [node duration cycles]
  (let [anim (javafx.animation.FadeTransition.)
        _ (.setNode anim node)
        _ (.setDuration anim (javafx.util.Duration. duration))
        _ (.setFromValue anim 1.0)
        _ (.setToValue anim 0.0)
        _ (.setCycleCount anim cycles)
        _ (.setAutoReverse anim true)]
    anim))
(defn pulse-color-anim [shape duration]
  (let [anim (javafx.animation.FillTransition.)
        _ (.setShape anim shape)
        _ (.setDuration anim (javafx.util.Duration. duration))
        _ (.setToValue anim javafx.scene.paint.Color/GOLD)
        _ (.setCycleCount anim javafx.animation.Timeline/INDEFINITE)
        _ (.setAutoReverse anim true)]
    anim))
(defn rotate-anim [node duration cycles]
  (let [anim (javafx.animation.RotateTransition.)
        _ (.setNode anim node)
        _ (.setFromAngle anim 0)
        _ (.setToAngle anim 360)
        _ (.setDuration anim (javafx.util.Duration. duration))
        _ (.setCycleCount anim cycles)]
    anim))

(defmacro ->! [obj & test-and-forms]
  (let [tfs (map (fn [[test form]] `(if ~test ~(conj (rest form) obj (first form))))
                 (partition 2 test-and-forms))]
    `(do ~@tfs nil)))

(defn set-anim-props! [anim {:keys [node duration cycles from-value
                                   to-value from-angle to-angle]}]
  (->! anim
       node (.setNode node)
       duration (.setDuration (javafx.util.Duration. duration))
       cycles (.setCycleCount cycles)
       from-value (.setFromValue from-value)
       to-value (.setToValue to-value)
       from-angle (.setFromAngle from-angle)
       to-angle (.setToAngle to-angle)))

(defmethod handle-event :anim-button-press
  [state event]
  (let [button (-> event :fn-fx/includes :fn-fx/event :target)
        _ (.play (pulse-anim button 500 javafx.animation.Timeline/INDEFINITE))]
    nil
    state))
(defmethod handle-event :anim-circle-press
  [state event]
  (let [target (-> event :fn-fx/includes :fn-fx/event :target)
        _ (.play (pulse-color-anim target 400))]
    nil
    state))
(defmethod handle-event :text-mouse-hover
  [state event]
  (let [target (-> event :fn-fx/includes :fn-fx/event :target)
        _ (.play (rotate-anim target 500 1))]
    nil
    state))
(def pdf-location
  "../pres/pres/pres.pdf")

(defmethod handle-event :key-was-pressed-on-scene
  [state event]
  (let [character (-> event :fn-fx/includes :fn-fx/event :code)
        text (-> event :fn-fx/includes :fn-fx/event :text)
        control-down? (-> event :fn-fx/includes :fn-fx/event :control-down?) 
        alt-down? (-> event :fn-fx/includes :fn-fx/event :alt-down?) 
        toggle-fullscreen? (= javafx.scene.input.KeyCode/F character)
        load-images? (= javafx.scene.input.KeyCode/L character)
        left? (= javafx.scene.input.KeyCode/LEFT character)
        right? (= javafx.scene.input.KeyCode/RIGHT character)
        inset-grow? (and control-down? alt-down?
                         (= javafx.scene.input.KeyCode/UP character))
        inset-shrink? (and control-down? alt-down?
                           (= javafx.scene.input.KeyCode/DOWN character))
        font-grow? (and control-down? alt-down?
                      (= javafx.scene.input.KeyCode/PLUS character))
        font-shrink? (and control-down? alt-down?
                           (= javafx.scene.input.KeyCode/MINUS character))
        number (try (Integer/parseInt text) (catch Exception e nil))
        change-view? (and control-down? alt-down? number)]
    (println "keypress:")
    (println "number / changeview?:" number change-view?)
    (clojure.pprint/pprint event)
    (println (= javafx.scene.input.KeyCode/ESCAPE character))

    (if (= javafx.scene.input.KeyCode/ESCAPE character) 
      (javafx.application.Platform/runLater
        (fn [] (let [target (-> event :fn-fx/includes :fn-fx/event :target)]
                 (if (= javafx.scene.Scene (type target))
                   (-> target .getWindow .close)
                   (-> target .getScene .getWindow .close))))))

    (-> state 
        (assoc :shut-down? (= javafx.scene.input.KeyCode/ESCAPE character))
        (update :fullscreen? #(if toggle-fullscreen? (not %) %))
        (update :image-index #(if (and right? (< % (dec (count (:images state))))) 
                                (inc %) %))
        (update :image-index #(if (and left? (< 0 %)) 
                                (dec %) %))
        (update :images #(if (and load-images? (not %)) 
                           (load-images-from-pdf! pdf-location) %))
        (update :inset-size #(if inset-grow? (+ % 25) %))
        (update :inset-size #(if (and inset-shrink? (<= 25 %)) (- % 25) %))
        (assoc  :view-index (if change-view? number (:view-index state)))
        (update :font-size #(cond font-grow? (inc %)
                                  (and font-shrink? (<= 1 %)) (dec %)
                                  :default %))
        (#(assoc % :font (ui/font :size (:font-size %))))
        )))

(defmethod handle-event :default
  [state event]
  (println "No handler for event " (:type event) event)
  state)

(defn -main []
  (let [;; Data State holds the business logic of our app
        _ (css/generate)
        data-state (atom {:font-size 16
                          :font (ui/font :size 16)
                          :image-index 0
                          :view-index 0
                          :zoom-level 1
                          :fullscreen? false
                          :image-anchor-width 0.0
                          :image-anchor-height 0.0
                          :width 0.0
                          :height 0.0
                          :inset-size 25
                          :current-view :chord-diagram-view
                          :menu-options [:package-viewer
                                         :chord-diagram-view
                                         :something-else?]
                          :todos [{:done? false
                                   :text  "Take out trash"}]})
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
