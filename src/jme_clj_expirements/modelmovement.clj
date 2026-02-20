(ns jme-clj-expirements.modelmovement
  (:require
  [jme-clj.core :as jme]
  [jme-clj-expirements.models :as models]
  [jme-clj-expirements.movement :as movement]
)
  (:import
   [com.jme3.math ColorRGBA]
   [com.jme3.bullet BulletAppState]
   [com.jme3.bullet.collision.shapes BoxCollisionShape]  
   [com.jme3.bullet.control RigidBodyControl]
   [com.jme3.bullet.control BetterCharacterControl]
   [com.jme3.input ChaseCamera]
))

(defn init []
  (let [bullet-app-state (BulletAppState.)]
    (.attach (jme/state-manager) bullet-app-state)
    (let  [
        {:keys [player-model]} (models/player-model-data)
        {:keys [ground]} (models/ground-box-data)
        {:keys [ambient-light main-light sun-light plsr]} (models/light-data)
        anim-composer (movement/find-anim-composer player-model)
        ;; player-model-shape (BoxCollisionShape. (jme/vec3 0.2 0.0 0.2))
        player-model-control (BetterCharacterControl. (float 0.2) (float 0.5) (float 1.0))
        ground-shape   (BoxCollisionShape. (jme/vec3 20 0.2 20))
        ground-control (RigidBodyControl. ground-shape 0)
        chase-cam (ChaseCamera. (jme/cam) player-model (jme/input-manager))
      ]
      (jme/set* (jme/view-port) :background-color ColorRGBA/DarkGray)
      ;; (jme/set* (jme/fly-cam) :enabled false)
      (.setDragToRotate chase-cam false)
      (.setMaxDistance chase-cam 20)
      (.setMinDistance chase-cam 20)
      (.setDefaultDistance chase-cam 20)
      (.setRotationSensitivity chase-cam (float 100.0))
      (.setLookAtOffset chase-cam (jme/vec3 0 4 0))
      (jme/add-light-to-root main-light)(jme/add-light-to-root sun-light)(jme/add-light-to-root ambient-light)
      (.setLight plsr main-light)(.addProcessor (jme/view-port) plsr)(.setShadowIntensity plsr 0.3)
      (.add (.getPhysicsSpace bullet-app-state) ground-control)
      (jme/add-control ground ground-control)
      (jme/add-to-root player-model)
      (jme/add-control player-model player-model-control)
      (.add (.getPhysicsSpace bullet-app-state) player-model-control)
        {
        :player-model player-model
        :anim-composer anim-composer
        :player-model-control player-model-control
        :bullet-app-state bullet-app-state
        }
)))

(defn update-all [tpf]
  (let  [{:keys [player-model player-model-control]} (jme/get-state)]
    (when (and player-model player-model-control)
      (let [
          player-pos (jme/get* player-model :local-translation)
          ]
        (movement/init-keys)
        (.lookAt (jme/cam) player-pos (jme/vec3 0 0 0))
        ;; (let [lerp-factor (* tpf 10.0)]
        ;; (.interpolateLocal new-cam-pos current-cam-pos target-cam-pos 
        ;;                 (min lerp-factor 1.0)))
        ;; (.setLocation (jme/cam) target-cam-pos)
        ;; (println "Player X:" (.getX phys-rot) "Player Y:" (.getY phys-rot) "Player Z:" (.getZ phys-rot) "Player W:" (.getW phys-rot))
        ;; (println "Cam X:" (.getX cam-rot) "Cam Y:" (.getY cam-rot) "Cam Z:" (.getZ cam-rot) "Cam W:" (.getW cam-rot))
        ;; (println "x:" (.-x phys-pos) "y:" (.-y phys-pos) "z:" (.-z phys-pos))
))))

;; (let [{:keys [player-model-control]} (jme/get-state)] (clojure.reflect/reflect player-model-control))

(jme/unbind-app #'app)
(jme/defsimpleapp app
  :opts {:show-settings?       false
         :pause-on-lost-focus? false
         :display-fps?         true
        :settings             {:title          "My JME Game"
                                :load-defaults? true
                                :frame-rate     120
                                :width          1080
                                :height         720
                                }}
  :init init
  :update update-all)

(jme/start app)
(jme/run app (jme/re-init init))


;; (jme/run app
;;   (let  [
;;         anim-composer (:anim-composer (jme/get-state))
;;         anim-name "idle_loop"
;;         anim-list (jme/get* anim-composer :anim-clips-names)
;;         ]
;;         (when (some #{anim-name} anim-list)
;;           (jme/set* anim-composer :current-action anim-name)
;;           (println "Changed animation to:" anim-name))
;;   ))

;; (jme/run app
;;   (let  [
;;         anim-composer (:anim-composer (jme/get-state))
;;         anim-name "squat_change"
;;         anim-list (jme/get* anim-composer :anim-clips-names)
;;         ]
;;         (when (some #{anim-name} anim-list)
;;           (jme/set* anim-composer :current-action anim-name)
;;           (println "Changed animation to:" anim-name))
;;   ))


(jme/run app
  (let [model (:model (jme/get-state))]
    (-> model
        ;; (jme/set* :local-rotation (com.jme3.math.Quaternion. (float 0) (float 0) (float 0) (float 0)))
        (jme/set* :local-translation (jme/vec3 0 -3 -5)))
  ))