(ns jme-clj-experiments.planemesh
  (:require [jme-clj.core :as jme])
  (:import  
    [com.jme3.scene Mesh VertexBuffer$Type]
    [com.jme3.math ColorRGBA]
    [com.jme3.util BufferUtils]
    [com.jme3.shader VarType]
   )
)

(defn create-plane-mesh [width depth segments-x segments-z]
  (let [vertex-count-x (inc segments-x)
        vertex-count-z (inc segments-z)
        vertex-data (for [z (range vertex-count-z)
                         x (range vertex-count-x)]
                     (let [x-pos (- (* (/ x segments-x) width) (/ width 2))
                           z-pos (- (* (/ z segments-z) depth) (/ depth 2))]
                       {:position [x-pos 0.0 z-pos]
                        :normal   [0.0 1.0 0.0]
                        :tex-coord [(/ x segments-x) (/ z segments-z)]}))
        indices (vec (for [z (range segments-z)
                          x (range segments-x)
                          triangle-corner [0 1 2 2 1 3] ; Two triangles: 0,1,2 and 2,1,3
                          :let [top-left (+ x (* z vertex-count-x))
                                top-right (inc top-left)
                                bottom-left (+ x (* (inc z) vertex-count-x))
                                bottom-right (inc bottom-left)
                                corners [top-left bottom-left top-right bottom-right]]]
                      (get corners triangle-corner)))
        mesh (Mesh.)]
    ;; Convert to Java arrays and set mesh buffers
    (.setBuffer mesh VertexBuffer$Type/Position 
                3 (->> vertex-data
                       (map :position)
                       (mapcat identity)
                       float-array
                       BufferUtils/createFloatBuffer))
    (.setBuffer mesh VertexBuffer$Type/Normal
                3 (->> vertex-data
                       (map :normal)
                       (mapcat identity) 
                       float-array
                       BufferUtils/createFloatBuffer))
    (.setBuffer mesh VertexBuffer$Type/TexCoord
                2 (->> vertex-data
                       (map :tex-coord)
                       (mapcat identity)
                       float-array
                       BufferUtils/createFloatBuffer))
    (.setBuffer mesh VertexBuffer$Type/Index
                3 (->> indices
                       int-array
                       BufferUtils/createIntBuffer))
    (.updateBound mesh)
    (.updateCounts mesh)
    {:mesh mesh
     :vertices (->> vertex-data (map :position) (map vec) vec)
     :indices indices}
))

(defn init []
  (let [plane-data (create-plane-mesh 10 10 50 50)
        plane-geo (jme/geo "Distortion Plane" (:mesh plane-data))
        ;; plane-geo-unshaded (jme/geo "Distortion Plane" (:mesh plane-data))
        wave-shader  (jme/material "Shaders/Wave.j3md")
        ;; shader  (jme/material "Common/MatDefs/Light/PBRLighting.j3md")
        ]

    ;; (jme/set* wave-shader :color "Color" ColorRGBA/Blue)
    ;; (.setVector4 wave-shader "Color" (Vector4f. 0.4 0.6 1.0 1.0))
    (.setParam wave-shader "SinFreq" VarType/Float (float 1.7))
    (.setParam wave-shader "WaveSpeed" VarType/Float (float 2.0))
    (.setParam wave-shader "WaveQuant" VarType/Float (float 1.2))
    
    (jme/set* plane-geo :material wave-shader)
    (jme/set* plane-geo :local-translation (jme/vec3 0 -7 -7))
    (jme/add-to-root plane-geo)
    
    ;; (.setVector4 shader "BaseColor" (Vector4f. 0.4 0.6 1.0 1.0))
    ;; (.setVector4 shader "Emissive" (Vector4f. 1.0 0.5 0.5 1.0))
    ;; (.setParam shader "Metallic" VarType/Float (float 1.0))
    ;; (.setParam shader "Glossiness" VarType/Float (float 1.0))

    ;; (jme/set* plane-geo-unshaded :material shader)
    ;; (jme/set* plane-geo-unshaded :local-translation (jme/vec3 -1 -2 2))
    ;; (jme/add-to-root plane-geo-unshaded)
    (let [ambient-light (jme/light :ambient)
          directional-light (jme/light :directional)]
      (jme/set* ambient-light :color (ColorRGBA. 0.3 0.3 0.3 1.0))
      (jme/set* directional-light :direction (jme/vec3 -1 -1 -1 :normalize))
      (jme/add-light-to-root ambient-light)
      (jme/add-light-to-root directional-light))
    {
     :start-time (System/currentTimeMillis)
     }
    ))

(jme/unbind-app #'app)

(jme/defsimpleapp app
  :opts {:show-settings?       false
         :pause-on-lost-focus? false
         :settings             {:title          "My JME Game"
                                :load-defaults? true
                                :frame-rate     60
                                :width          720
                                :height         480
                                }}
  :init init
)

(jme/start app)
