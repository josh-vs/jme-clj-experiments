(ns jme-clj-expirements.modelmovement
  (:require
   [jme-clj.core :as jme]
  [jme-clj-expirements.models :as models]
  [jme-clj-expirements.movement :as movement])
  (:import
   [com.jme3.math ColorRGBA]
   (com.jme3.bullet BulletAppState)
   (com.jme3.bullet.collision.shapes BoxCollisionShape)  
   (com.jme3.bullet.control RigidBodyControl)
   (com.jme3.math Vector3f)
   ))

(defn init []
  (let [bullet-app-state (BulletAppState.)]
    (.attach (jme/state-manager) bullet-app-state)
    (let  [
        {:keys [player-model]} (models/player-model-data)
        {:keys [ground]} (models/ground-box-data)
        {:keys [ambient-light main-light sun-light plsr]} (models/light-data)
        anim-composer (movement/find-anim-composer player-model)
        player-model-shape (BoxCollisionShape. (jme/vec3 0.2 0.0 0.2))
        player-model-control (RigidBodyControl. player-model-shape 1)
        ground-shape   (BoxCollisionShape. (jme/vec3 20 0.2 20))
        ground-control (RigidBodyControl. ground-shape 0)
      ]
      (jme/set* (jme/view-port) :background-color ColorRGBA/DarkGray)
      (jme/set* (jme/fly-cam) :move-speed 15)
      (movement/init-keys)
      (jme/add-light-to-root main-light)(jme/add-light-to-root sun-light)(jme/add-light-to-root ambient-light)
      (.setLight plsr main-light)(.addProcessor (jme/view-port) plsr)(.setShadowIntensity plsr 0.3)
      (.add (.getPhysicsSpace bullet-app-state) ground-control)
      (jme/add-control ground ground-control)
      (jme/add-to-root player-model)
      (jme/add-control player-model player-model-control)
      (.add (.getPhysicsSpace bullet-app-state) player-model-control)
      (.lookAt (jme/cam) (.getPhysicsLocation player-model-control) (jme/vec3 0 1 0))
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
          phys-pos (.getPhysicsLocation player-model-control)
          current-cam-pos (.getLocation (jme/cam))
          target-cam-pos (.add phys-pos (jme/vec3 0 10 -20))
          new-cam-pos (jme/vec3 0 0 0)
          ]
        (jme/set* player-model :local-translation phys-pos)
        (let [lerp-factor (* tpf 10.0)]
        (.interpolateLocal new-cam-pos current-cam-pos target-cam-pos 
                        (min lerp-factor 1.0)))
        ;; Set camera position and look-at
        (.setLocation (jme/cam) new-cam-pos)
        (.lookAt (jme/cam) phys-pos (jme/vec3 0 1 0))
        (println "x:" (.-x phys-pos) "y:" (.-y phys-pos) "z:" (.-z phys-pos))
))))

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
;;         anim-name "squat_idle"
;;         anim-list (jme/get* anim-composer :anim-clips-names)
;;         ]
;;         (when (some #{anim-name} anim-list)
;;           (jme/set* anim-composer :current-action anim-name)
;;           (println "Changed animation to:" anim-name))
;;   ))

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

;; (jme/run app
;;   (let  [
;;         anim-composer (:anim-composer (jme/get-state))
;;         anim-name "walk_loop"
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