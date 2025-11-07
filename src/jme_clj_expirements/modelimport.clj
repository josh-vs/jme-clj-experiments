(ns jme-clj-expirments.modelimport
  (:require
   [jme-clj.core :as jme])
  (:import
   [com.jme3.input KeyInput]
   [com.jme3.anim AnimComposer]
   [com.jme3.shader VarType]
   [com.jme3.math ColorRGBA]
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

(defn on-action-listener []
  (jme/action-listener
    (fn [name pressed? tpf]
      (when (not pressed?)
        (let [{:keys [anim-composer]} (jme/get-state)]
          (case name 
            ::walk (jme/set* anim-composer :current-action "walk_loop")
            ::idle  (jme/set* anim-composer :current-action "idle_loop")
            ::squat (jme/set* anim-composer :current-action "squat_idle")
            nil)
        )))))

(defn- init-keys []
  (jme/apply-input-mapping
   {:triggers  {::walk (jme/key-trigger KeyInput/KEY_1) 
                ::idle (jme/key-trigger KeyInput/KEY_2) 
                ::squat (jme/key-trigger KeyInput/KEY_3)}
    :listeners {(on-action-listener) [::walk ::idle ::squat]}}
  ))

(defn init []
  (let  [
         model         (jme/load-model "Models/CharModel4.gltf")
         anim-composer      (find-anim-composer model)
         model-mat     (jme/material "Common/MatDefs/Light/Lighting.j3md")
         ground        (jme/geo "Ground" (jme/box 20 0.2 20))
         ground-mat    (jme/material "Common/MatDefs/Light/Lighting.j3md")
         plsr          (com.jme3.shadow.PointLightShadowRenderer. (jme/asset-manager) 1024)
         main-light    (jme/light :point)
         sun-light     (jme/light :directional)
         ambient-light (jme/light :ambient)
        ;;  fpp           (com.jme3.post.FilterPostProcessor. (jme/asset-manager))
        ;;  ssao-filter   (com.jme3.post.ssao.SSAOFilter. 12.94 43.92 0.33 0.61)
        ;;  ssao-filter   (com.jme3.post.ssao.SSAOFilter. 8.0 30.0 0.3 0.5)
         ]
    (jme/set* (jme/view-port) :background-color ColorRGBA/DarkGray)
    (jme/set* (jme/fly-cam) :move-speed 15)
    (init-keys)
    (-> main-light
        (jme/set* :position (jme/vec3 5 8 5))  
        (jme/set* :radius 40.0)  
        (jme/set* :color (ColorRGBA. 1.0 0.95 0.9 1.0)) ; Warm white
        (jme/add-light-to-root)
    )
    (-> sun-light
        (jme/set* :direction (jme/vec3 -0.5 -0.5 1.0 :normalize))
        (jme/set* :color (ColorRGBA. 1.0 0.95 0.9 1.0)) ; Warm sunlight
        (jme/add-light-to-root)
    )
    (-> ambient-light
        (jme/set* :color (ColorRGBA. 0.2 0.2 0.25 1.0)) ; Soft blue-gray ambient
        (jme/add-light-to-root)
    )
    (.setLight plsr main-light)
    (.addProcessor (jme/view-port) plsr)
    (.setShadowIntensity plsr 0.3)
    ;; (.addFilter fpp ssao-filter)
    ;; (.addProcessor (jme/view-port) fpp)
    (-> ground-mat
        (jme/set* :boolean "UseMaterialColors" true)
        (jme/set* :color "Ambient" (ColorRGBA. 0.3 0.3 0.4 1.0))
        (jme/set* :color "Diffuse" (ColorRGBA. 0.5 0.5 0.6 1.0)) ; Darker ground
        ;; (.setParam "Shininess" VarType/Float (Float. 8.0))
    )
    (-> ground
        (jme/set* :local-translation (jme/vec3 0 -5.2 0))
        (jme/set* :material ground-mat)
        (jme/set* :shadow-mode com.jme3.renderer.queue.RenderQueue$ShadowMode/Receive)
        (jme/add-to-root)
    )
    (-> model-mat
        (jme/set* :boolean "UseMaterialColors" true)
        (jme/set* :color "Ambient" (ColorRGBA. 0.4 0.4 0.5 1.0))   ; Bright ambient
        (jme/set* :color "Diffuse" (ColorRGBA. 0.9 0.9 1.0 1.0))   ; Bright diffuse
        ;; (jme/set* :color "Specular" (ColorRGBA. 1.0 1.0 1.0 1.0))  ; Strong specular
    )
    (-> model
        (jme/set* :material model-mat)
        (jme/set* :local-scale (float 1.0))
        (jme/set* :local-translation (jme/vec3 0 -5 -5))
        (jme/set* :shadow-mode com.jme3.renderer.queue.RenderQueue$ShadowMode/CastAndReceive)
        ;; (jme/set* :local-rotation (com.jme3.math.Quaternion. (float 0) (float 0) (float 0) (float 0)))
        (jme/add-to-root)
    )
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
    {
     :model model
     :anim-composer anim-composer
    }
  ))

(defn update-all [tpf]
  (let  [
         {:keys [model]} (jme/get-state)
         ]
    (jme/rotate model (* 0 tpf) (* 1 tpf) (* 0 tpf))
  ))

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

(jme/run app
  (let  [
        anim-composer (:anim-composer (jme/get-state))
        anim-name "squat_idle"
        anim-list (jme/get* anim-composer :anim-clips-names)
        ]
        (when (some #{anim-name} anim-list)
          (jme/set* anim-composer :current-action anim-name)
          (println "Changed animation to:" anim-name))
  ))

(jme/run app
  (let  [
        anim-composer (:anim-composer (jme/get-state))
        anim-name "idle_loop"
        anim-list (jme/get* anim-composer :anim-clips-names)
        ]
        (when (some #{anim-name} anim-list)
          (jme/set* anim-composer :current-action anim-name)
          (println "Changed animation to:" anim-name))
  ))

(jme/run app
  (let  [
        anim-composer (:anim-composer (jme/get-state))
        anim-name "squat_change"
        anim-list (jme/get* anim-composer :anim-clips-names)
        ]
        (when (some #{anim-name} anim-list)
          (jme/set* anim-composer :current-action anim-name)
          (println "Changed animation to:" anim-name))
  ))

(jme/run app
  (let  [
        anim-composer (:anim-composer (jme/get-state))
        anim-name "walk_loop"
        anim-list (jme/get* anim-composer :anim-clips-names)
        ]
        (when (some #{anim-name} anim-list)
          (jme/set* anim-composer :current-action anim-name)
          (println "Changed animation to:" anim-name))
  ))

(jme/run app
  (let [model (:model (jme/get-state))]
    (-> model
        ;; (jme/set* :local-rotation (com.jme3.math.Quaternion. (float 0) (float 0) (float 0) (float 0)))
        (jme/set* :local-translation (jme/vec3 0 -3 -5)))
  ))