(ns pomodoro.core
    (:require [reagent.core :as reagent :refer [atom]]))

;; what is happening here?

;; i am building a pomodoro app for my personal use, and as a learning exercise
;; for some sort of front-end skills. i've chosen reagent and clojurescript,
;; and will be storing state first in atoms, and eventually in browser based
;; local storage, which affords me a few meg in which i can store straight edn
;; data. i should ideally be taking extensive notes on challenges i encounter

;; challenges already encountered but not really noted
;; * reagent components only render inside a hiccup expression, despite being
;; defined as functions. the fuction returns a data sturcture that hiccup must
;; hand off to react in some way i don't fully understand. look at the hiccup
;; source and understand that parsing well enough to explain it in a tutorial.

;;----------------------------------------------------------------------------;;

;; pretty sure that without this println would go to the repl/stdout of the
;; process only, whereas it is convenient to have it be in the browser dev
;; console.
(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
;; defonce is evaluated at parse time.
(defonce timer-active (atom false))
(defonce timer-percentage (atom nil))

(defn wait-component []
  [:input {:type :button :value "Start!"
           :on-click #(reset! timer-active true)}])

(defn countdown-component []
  (let [time-now #(js/Date.)
        start-time (time-now)
        current-time (atom start-time)
        update-time #(reset! current-time (time-now))
        max-time 1500
        timer-id (atom nil)]
    (reagent/create-class
     {:component-will-mount #(reset! timer-id (js/setInterval update-time 1000))
      :component-will-unmount #(js/clearInterval @timer-id)
      :display-name "countdount-component"
      :reagent-render
      (fn []
        (let [time @current-time
              ticks (int (/ (- time start-time) 1000))
              remaining (- max-time ticks)
              percentage (reset! timer-percentage
                                 (str (int (* 100 (/ remaining max-time)))))
              done? (> 0 remaining)]
        [:div
          [:h1 "You can do it!"]
         (if done?
           [:h2 "YATTA"]
           [:h1 (str (.substr (.toISOString (doto (js/Date. nil)
                                                  (.setSeconds remaining)))
                              14 5))])
          [:input {:type :button :value "Cancel!"
                   :on-click #(reset! timer-active false)}]]))})))

(defn heart [active timer-percentage]
  [:div {:class "heartdiv"}
   [:svg {:class "heart" :viewBox "0 0 100 100"
          :style (if active { :animation "pulse 1s ease infinite" } nil)
          }
      [:defs
        [:radialGradient {:id "grad1" :cx "50%" :cy "50%" :r "50%" :fx "50%" :fy "50%"}
        [:stop {:class "stop1" :offset "10%" :stop-color "#F0F"}]
         [:stop {:class "stop2" :offset "100%" :stop-color "#000" :stop-opacity "0"}]
         ]
       [:linearGradient {:id "grad2" :x1 "0%" :y1 "0%" :x2 "0%" :y2 "100%"}
        [:stop {:offset (str (- timer-percentage 50) "%") :stop-color "#0F0"}]
        [:stop {:offset (str timer-percentage "%") :stop-color "#F0F"}]]
      ]
      (println timer-percentage)
      [:path {:id "heart"
              ;; :d "M 200 350 c 0 -2 -30 0 -100 -120 c -20 -30 40 -160 100 -40 c 40 -100 120 -30 100 40 c 0 0 0 30 -100 120"
              :d "M 50 88 l -25 -30 a 14 14 0 1 1 25 -10 a 14 14 0 1 1 25 10 l -25 30"
              :stroke "black"
              :stroke-width "5"
              :stroke-linecap "round"
              :fill "url(#grad2)"}]]])

(defn timer-dispatch [state]
  (if @timer-active
    [countdown-component]
    (wait-component)))

(defn basic-timer []
    [:div
      (heart @timer-active @timer-percentage)
      (timer-dispatch @timer-active)])

(reagent/render-component [basic-timer]
                          (. js/document (getElementById "app")))
