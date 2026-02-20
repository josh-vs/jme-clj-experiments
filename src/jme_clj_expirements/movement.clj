(ns jme-clj-expirements.movement
  (:require
   [jme-clj.core :as jme]
  )
  (:import
   [com.jme3.input KeyInput MouseInput]
   [com.jme3.anim AnimComposer]
   [com.jme3.renderer Camera]
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

(def key-state (atom {::forward false ::backward false ::left false ::right false}))

(defn on-action-listener [] (jme/action-listener
  (fn [name pressed? tpf]
    (let [{:keys [anim-composer player-model-control]} (jme/get-state)
          cam-dir (.normalizeLocal (.setY (.getDirection (jme/cam)) 0))
          cam-left (.normalizeLocal (.setY (.getLeft (jme/cam)) 0))
          walk-dir (.set (com.jme3.math.Vector3f.) (jme/vec3 0 0 0))]
      (swap! key-state assoc name pressed?)
      (when player-model-control
        (when (::forward @key-state)
          (.addLocal walk-dir (.negate cam-dir)))
        (when (::backward @key-state)
          (.addLocal walk-dir cam-dir))
        (when (::right @key-state)
          (.addLocal walk-dir cam-left))
        (when (::left @key-state)
          (.addLocal walk-dir (.negate cam-left)))
        (when (and (not (zero? (.lengthSquared walk-dir)))
                   (> (.lengthSquared walk-dir) 1))
          (.normalizeLocal walk-dir))
        (if (zero? (.lengthSquared walk-dir))
          (jme/set* anim-composer :current-action "idle_loop")
          (do (.setViewDirection player-model-control walk-dir)
              (jme/set* anim-composer :current-action "walk_loop")))
        (jme/mult-loc walk-dir (float -5))
        (.setWalkDirection player-model-control walk-dir))
))))

(defn on-analog-listener [] (jme/analog-listener
  (fn [name analog-value tpf] (let [] ))))

(defn init-keys []
  (jme/apply-input-mapping
   {:triggers  {::forward (jme/key-trigger KeyInput/KEY_W)
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
