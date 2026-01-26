(ns jme-clj-expirements.terrainimport
    (:require 
    [jme-clj.core :as jme]
    [jme-clj-expirements.models :as models]
    [jme-clj-expirements.movement :as movement]
    :reload
    )
    (:import
    [com.jme3.math ColorRGBA]
    (com.jme3.bullet.util CollisionShapeFactory)
    (com.jme3.bullet.collision.shapes BoxCollisionShape)
    (com.jme3.bullet.control RigidBodyControl)
    (com.jme3.bullet BulletAppState)
    )
)

(defn init []
  (let [bullet-app-state (BulletAppState.)]
    (.attach (jme/state-manager) bullet-app-state)
    (let    [
            {:keys [terrain-model]} (models/terrain-data)
            {:keys [player-model]} (models/player-model-data)
            {:keys [ambient-light main-light sun-light plsr]} (models/light-data)
            anim-composer (movement/find-anim-composer player-model)
            player-model-shape (BoxCollisionShape. (jme/vec3 0.2 0.0 0.2))
            player-model-control (RigidBodyControl. player-model-shape 1)
            terrain-shape   (CollisionShapeFactory/createMeshShape terrain-model)
            terrain-control (RigidBodyControl. terrain-shape 0)
            ]
        (jme/set* (jme/view-port) :background-color ColorRGBA/DarkGray)
        (jme/set* (jme/fly-cam) :move-speed 15)
        (movement/init-keys)
        (jme/add-light-to-root main-light)
        (jme/add-light-to-root sun-light)
        (jme/add-light-to-root ambient-light)
        (.setLight plsr main-light)
        (.addProcessor (jme/view-port) plsr)
        (.setShadowIntensity plsr 0.3)
        (jme/add-to-root terrain-model)
        (jme/add-control terrain-model terrain-control)
        (.add (.getPhysicsSpace bullet-app-state) terrain-control)
        (jme/add-to-root player-model)
        (jme/add-control player-model player-model-control)
        (.add (.getPhysicsSpace bullet-app-state) player-model-control)
        (.lookAt (jme/cam) (.getPhysicsLocation player-model-control) (jme/vec3 0 1 0))
    {:player-model player-model
     :anim-composer anim-composer
     :player-model-control player-model-control
     :bullet-app-state bullet-app-state}
)))

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
)

(jme/start app)
