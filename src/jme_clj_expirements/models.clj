(ns jme-clj-expirements.models
    (:require 
    [jme-clj.core :as jme]
    )
    (:import
    [com.jme3.math ColorRGBA]
    )
)

(defn terrain-data []
    (let [model (jme/load-model "Models/Terrain1.gltf")
          mat (jme/material "Common/MatDefs/Light/Lighting.j3md")
         ]
        (-> mat
            (jme/set* :boolean "UseMaterialColors" true)
            (jme/set* :color "Ambient" (ColorRGBA. 0.3 0.3 0.4 1.0))
            (jme/set* :color "Diffuse" (ColorRGBA. 0.5 0.5 0.6 1.0)) ; Darker ground
        )
        (-> model
            (jme/set* :local-translation (jme/vec3 0 -15 0))
            (jme/set* :local-scale (float 1.0))
            (jme/set* :material mat)
            (jme/set* :shadow-mode com.jme3.renderer.queue.RenderQueue$ShadowMode/Receive)
        )
        {:terrain-model model
        :terrain-mat mat
        }
))

(defn ground-box-data []
	(let [ground (jme/geo "Ground" (jme/box 20 0.2 20))
        ground-mat     (jme/material "Common/MatDefs/Light/Lighting.j3md")]
		(-> ground-mat
			(jme/set* :boolean "UseMaterialColors" true)
			(jme/set* :color "Ambient" (ColorRGBA. 0.3 0.3 0.4 1.0))
			(jme/set* :color "Diffuse" (ColorRGBA. 0.5 0.5 0.6 1.0)) ; Darker ground
			;; (.setParam "Shininess" VarType/Float (Float. 8.0))
			)
		(-> ground
			(jme/set* :local-translation (jme/vec3 0 -6 0))
			(jme/set* :material ground-mat)
			(jme/set* :shadow-mode com.jme3.renderer.queue.RenderQueue$ShadowMode/Receive)
			(jme/add-to-root)
			)
		{:ground ground :ground-mat ground-mat}
))

(defn player-model-data []
    (let [model         (jme/load-model "Models/CharModel4.gltf")
          mat     (jme/material "Common/MatDefs/Light/Lighting.j3md")
         ]
        (-> mat
            (jme/set* :boolean "UseMaterialColors" true)
            (jme/set* :color "Ambient" (ColorRGBA. 0.4 0.4 0.5 1.0))   ; Bright ambient
            (jme/set* :color "Diffuse" (ColorRGBA. 0.9 0.9 1.0 1.0))   ; Bright diffuse
            ;; (jme/set* :color "Specular" (ColorRGBA. 1.0 1.0 1.0 1.0))  ; Strong specular
        )
        (-> model
            (jme/set* :material mat)
            (jme/set* :local-scale (float 1.0))
            (jme/set* :local-translation (jme/vec3 0 -5 -5))
            (jme/set* :shadow-mode com.jme3.renderer.queue.RenderQueue$ShadowMode/CastAndReceive)
            ;; (jme/set* :local-rotation (com.jme3.math.Quaternion. (float 0) (float 0) (float 0) (float 0)))
            (jme/add-to-root)
        )
        {:player-model model
        :player-mat mat
        }
))

(defn light-data []
    (let [plsr          (com.jme3.shadow.PointLightShadowRenderer. (jme/asset-manager) 1024)
          main-light    (jme/light :point)
          sun-light     (jme/light :directional)
          ambient-light (jme/light :ambient)
        ;;   fpp           (com.jme3.post.FilterPostProcessor. (jme/asset-manager))
        ;;   ssao-filter   (com.jme3.post.ssao.SSAOFilter. 12.94 43.92 0.33 0.61)
        ;;   ssao-filter   (com.jme3.post.ssao.SSAOFilter. 8.0 30.0 0.3 0.5)
        ]
        (-> main-light
            (jme/set* :position (jme/vec3 5 8 5))  
            (jme/set* :radius 40.0)  
            (jme/set* :color (ColorRGBA. 1.0 0.95 0.9 1.0)) ; Warm white
        )
        (-> sun-light
            (jme/set* :direction (jme/vec3 -0.5 -0.5 1.0 :normalize))
            (jme/set* :color (ColorRGBA. 1.0 0.95 0.9 1.0)) ; Warm sunlight
        )
        (-> ambient-light
            (jme/set* :color (ColorRGBA. 0.2 0.2 0.25 1.0)) ; Soft blue-gray ambient
        )
        ;; (.addFilter fpp ssao-filter)
        ;; (.addProcessor (jme/view-port) fpp)
        {:plsr plsr
         :main-light main-light
         :sun-light sun-light
         :ambient-light ambient-light}
))

        ;;  fpp           (com.jme3.post.FilterPostProcessor. (jme/asset-manager))
        ;;  ssao-filter   (com.jme3.post.ssao.SSAOFilter. 12.94 43.92 0.33 0.61)
        ;;  ssao-filter   (com.jme3.post.ssao.SSAOFilter. 8.0 30.0 0.3 0.5)
      ;; (.addFilter fpp ssao-filter)
      ;; (.addProcessor (jme/view-port) fpp)
