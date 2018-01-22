(ns pomodoro.core
    (:require [reagent.core :as reagent :refer [atom]]))

(comment
  what is happening here?

  i am building a pomodoro app for my personal use, and as a learning exercise
  for some sort of front-end skills. i've chosen reagent and clojurescript,
  and will be storing state first in atoms, and eventually in browser based
  local storage, which affords me a few meg in which i can store straight edn
  data. i should ideally be taking extensive notes on challenges i encounter

  challenges already encountered but not really noted
  * reagent components only render inside a hiccup expression, despite being
  defined as functions. the fuction returns a data sturcture that hiccup must
  hand off to react in some way i don't fully understand. look at the hiccup
  source and understand that parsing well enough to explain it in a tutorial.



  )
;; pretty sure that without this println would go to the repl/stdout of the
;; process only, whereas it is convenient to have it be in the browser dev
;; console.
(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
;; defonce is evaluated at parse time.
(defonce app-state (atom {:text "Hello world!"
                          :clicks 0
                          :timer-state :waiting}))

;; simple components just return hiccup.
(defn clicker []
  [:div
   "The clicker " [:code "clicks"] " has been clicked: "
   (:clicks @app-state) " times."
   [:input {:type "button" :value "Click!"
            :on-click #(swap! app-state update-in [:clicks] inc)}]])

;; small chunks of unchanging hiccup can be stored in vars and used
;; accordingly. not sure if this will turn out to be particularly valuable
;; but it seems worth mentioning.
(def will-it-render
  [:div [:h3 "It blends!"]])

(defn wait-component []
  [:input {:type :button :value "Start!"
           :on-click #(swap! app-state assoc :timer-state :countdown)}])

(defn new-date [] (js/Date.))

;; I'm not really sure how to communicate the counterintuitive behaviour of
;; js/setInterval in a functional way until i get deeper into defining element
;; destructors. everything that calls this needs to retain it's return value
;; and then later call js/clearInterval with the returned ID or leak all sorts
;; of mess, especially if fn is not pure (and why would it be, nothing takes
;; it's return)
(defn every-second [fn] (js/setInterval fn 1000))

(defn countdown-component [a]
  (let [start-time (new-date)]
    (reagent/create-class
     {:component-will-mount #(println "component mounting")
      :component-did-mount #(println "component mounted")
      :component-will-unmount #(println "component unmounting")
      :display-name "countdount-component"
      :reagent-render
      (fn [a]
        (let [this :that]
          (fn []
            ;; this seems intuitive but :div is the html/hiccup equivalent of
            ;; clojure's 'do'. if a function is to return several ui elements,
            ;; a containing element will make things manageable. you can just
            ;; return a list of things, but the compiler gets upset about not
            ;; having ids for all the things.
            [:div
              [:h3 "the final countdown"]
              [:h3 (str start-time)]
              [:input {:type :button :value "Cancel!"
                      :on-click #(swap! app-state assoc :timer-state :waiting)}]])))})))


(defn wtf-component [] [:div [:h3 "WHAT THE FUCK"]])

(defn timer-dispatch [state]
  (case state
    :waiting (wait-component)
    :countdown [countdown-component 1]
    (wtf-component)))

(defn hello-world []
  (let [state @app-state]
    [:div
      [:h1 (:text state)]
      [:h3 "Edit this and watch it change!"]
     will-it-render
      (clicker)
      (timer-dispatch (:timer-state state))]))

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
