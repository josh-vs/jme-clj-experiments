(ns jme-clj-expirements.movement
  (:require
   [jme-clj.core :as jme]
  ;;  [jme-clj-expirements.models :as models]
  )
  (:import
   [com.jme3.input KeyInput MouseInput]
   [com.jme3.anim AnimComposer]
   ))

(defn find-anim-composer [spatial]
  (try
    (let [anim-composer (jme/get* spatial :control AnimComposer)]
      (if anim-composer
        anim-composer
        (when (instance? com.jme3.scene.Node spatial)
          (some find-anim-composer (jme/get* spatial :children)))))
    (catch Exception e
      nil)))

(defn on-action-listener [] (jme/action-listener
  (fn [name pressed? tpf]
    (when pressed?
      (let [{:keys [anim-composer]} (jme/get-state)]
        (case name
          ::forward (jme/set* anim-composer :current-action "walk_loop")
          ::squat (jme/set* anim-composer :current-action "squat_idle")
          nil)))
    (when (not pressed?)
      (let [{:keys [anim-composer]} (jme/get-state)]
        (case name
          ::forward (jme/set* anim-composer :current-action "idle_loop")
          ::squat (jme/set* anim-composer :current-action "idle_loop")
          nil)))
)))

(def angle (atom 0.0))
(def angle-vertical (atom 0.5))

(defn update-camera-position [player-pos angle-h angle-v distance]
  (let [offset-x (* distance (Math/cos angle-h) (Math/cos angle-v))
        offset-y (* distance (Math/sin angle-v))
        offset-z (* distance (Math/sin angle-h) (Math/cos angle-v))]
    (jme/vec3 (+ (.-x player-pos) offset-x)
              (+ (.-y player-pos) offset-y)
              (+ (.-z player-pos) offset-z))))

(defn on-analog-listner [] (jme/analog-listener
  (fn [name pressed? tpf]
    (let [{:keys [player-model-control]} (jme/get-state)]
    (when player-model-control
      (case name 
        ::forward 
          (let [phys-pos (.getPhysicsLocation player-model-control)
                ;; forward-vec (.getPhysicsRotation player-model-control)
                ;; _ (.multLocal forward-vec (jme/vec3 0 0 (* move-speed value tpf)))]
                ]
            (.set phys-pos (float (.-x phys-pos)) 
                          (float (.-y phys-pos)) 
                          (float (+ -0.1 (.-z phys-pos))))
            (.setPhysicsLocation player-model-control phys-pos))
        ;; ::cam-left
        ;;   (let [
        ;;         phys-pos (.getPhysicsLocation player-model-control)
        ;;         _ (swap! angle + 0.1)
        ;;         offset-x (* 15.0 (Math/cos @angle))
        ;;         offset-z (* 15.0 (Math/sin @angle))
        ;;         target-cam-pos (jme/vec3 (+ (.-x phys-pos) offset-x) 
        ;;                           (+ (.-y phys-pos) 20.0) 
        ;;                           (+ (.-z phys-pos) offset-z))
        ;;         ]
        ;;     (.setLocation (jme/cam) target-cam-pos)
        ;;     (.lookAt (jme/cam) phys-pos (jme/vec3 0 0 0))
        ;;   )
        nil
))))))

(defn init-keys []
  (jme/apply-input-mapping
   {:triggers  {
                ::forward (jme/key-trigger KeyInput/KEY_W)
                ::backward (jme/key-trigger KeyInput/KEY_S)
                ::left (jme/key-trigger KeyInput/KEY_A)
                ::right (jme/key-trigger KeyInput/KEY_D)
                ::squat (jme/key-trigger KeyInput/KEY_LSHIFT)
                ::cam-up (jme/mouse-ax-trigger MouseInput/AXIS_Y true)
                ::cam-down (jme/mouse-ax-trigger MouseInput/AXIS_Y false)
                ::cam-right (jme/mouse-ax-trigger MouseInput/AXIS_X false)
                ::cam-left (jme/mouse-ax-trigger MouseInput/AXIS_X true)}
                
    :listeners {(on-analog-listner) [::cam-down ::cam-left ::cam-right ::cam-up ::forward ::backward ::left ::right]
                (on-action-listener) [::forward ::backward ::left ::right ::squat]
                }
}))

        ;; (when anim-composer
        ;;   (println "Found AnimComposer.")
        ;;   (let [anim-list (jme/get* anim-composer :anim-clips-names)]
        ;;     (println "Available animations:" anim-list)
        ;;     (let [anim-name "default"]
        ;;       (if (some #{anim-name} anim-list)
        ;;         (do
        ;;           (println "Setting animation to loop:" anim-name)
        ;;           (jme/set* anim-composer :current-action anim-name)
        ;;           (println "Animation" anim-name "should be looping."))
        ;;         (println "Animation" anim-name "not found.")))))
