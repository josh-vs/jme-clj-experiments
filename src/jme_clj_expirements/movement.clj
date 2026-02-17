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
    (let [{:keys [anim-composer player-model-control]} (jme/get-state)]
      (case name
        (::forward ::backward ::left ::right)
        (if pressed?
          (jme/set* anim-composer :current-action "walk_loop")
          (do
            (jme/set* anim-composer :current-action "idle_loop")
            (.setWalkDirection player-model-control (jme/vec3 0 0 0))))
        ::squat
        (if pressed?
          (jme/set* anim-composer :current-action "squat_idle")
          (jme/set* anim-composer :current-action "idle_loop"))
        nil)
))))

(defn on-analog-listener [] (jme/analog-listener
  (fn [name analog-value tpf]
    (let [{:keys [player-model-control]} (jme/get-state)]
      (when player-model-control
        (case name
          ::forward
          (let [forward (jme/vec3 0 0 -1)
                cam-rot (.getRotation (jme/cam))]
            (.mult cam-rot forward forward)
            (.setY forward 0)
            (when (> (.length forward) 0.001)
              (.normalizeLocal forward)
              (.setViewDirection player-model-control forward)
              (.multLocal forward (float -5.0))
              (.setWalkDirection player-model-control forward)))
          ::backward
          (let [backward (jme/vec3 0 0 1)
                cam-rot (.getRotation (jme/cam))]
            (.mult cam-rot backward backward)
            (.setY backward 0)
            (when (> (.length backward) 0.001)
              (.normalizeLocal backward)
              (.setViewDirection player-model-control backward)
              (.multLocal backward (float -5.0))
              (.setWalkDirection player-model-control backward)))
          ::right
          (let [left (jme/vec3 1 0 0)
                cam-rot (.getRotation (jme/cam))]
            (.mult cam-rot left left)
            (.setY left 0)
            (when (> (.length left) 0.001)
              (.normalizeLocal left)
              (.setViewDirection player-model-control left)
              (.multLocal left (float -5.0))
              (.setWalkDirection player-model-control left)))
          ::left
          (let [left (jme/vec3 -1 0 0)
                cam-rot (.getRotation (jme/cam))]
            (.mult cam-rot left left)
            (.setY left 0)
            (when (> (.length left) 0.001)
              (.normalizeLocal left)
              (.setViewDirection player-model-control left)
              (.multLocal left (float -5.0))
              (.setWalkDirection player-model-control left)))
          nil)
)))))


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
                
    :listeners {(on-analog-listener) [::cam-down ::cam-left ::cam-right ::cam-up ::forward ::backward ::left ::right]
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
