(ns jme-clj-experiments.modelscaler
  (:require
   [jme-clj.core :as jme]))

(def scales-to-try (atom [0.0001 0.001 0.01 0.1 1.0 10.0 100.0 1000.0]))

(def current-scale-index (atom 0))
(def time-accumulator (atom 0.0))
(def character-ref (atom nil))

(defn update-all [tpf]
  (println "Update called, tpf:" tpf)
  (swap! time-accumulator + tpf)

  (when (>= @time-accumulator 2.0)
    (reset! time-accumulator 0.0)

    (when-let [current-scale (nth @scales-to-try @current-scale-index nil)]
      (println "Trying scale:" current-scale)
      (when-let [char @character-ref]
        (jme/set* char :local-scale (float current-scale)))

      (swap! current-scale-index inc)

      (when (>= @current-scale-index (count @scales-to-try))
        (reset! current-scale-index 0)
        (println "=== Scale cycle complete ===")))))

(defn init []
  (-> (jme/light :directional)
      (jme/set* :direction (jme/vec3 0 -1 0))
      (jme/set* :color (jme/color-rgba 1 1 1 1))
      (jme/add-light-to-root))
  (-> (jme/light :ambient)
      (jme/set* :color (jme/color-rgba 1 1 1 1))
      (jme/add-light-to-root))

  (let [test-box (jme/geo "test-cube" (jme/box 1 1 1))
        test-mat (jme/material "Common/MatDefs/Misc/Unshaded.j3md")]
    (-> test-mat
        (jme/set* :color "Color" com.jme3.math.ColorRGBA/Blue))
    (-> test-box
        (jme/set* :material test-mat)
        (jme/set* :local-translation (jme/vec3 -5 0 0))
        (jme/add-to-root)))

  (println "=== LOADING GLTF ===")
  (let [character (jme/load-model "Models/Terrain1.gltf")]
    (println "GLTF loaded")
    (reset! character-ref character)
    (let [bright-mat (jme/material "Common/MatDefs/Misc/Unshaded.j3md")]
      (-> bright-mat
          (jme/set* :color "Color" com.jme3.math.ColorRGBA/Blue))
      (letfn [(apply-material [node]
                (if (instance? com.jme3.scene.Geometry node)
                  (do
                    (jme/set* node :material bright-mat)
                    (println "Applied material to geometry:" (jme/get* node :name)))
                  (do
                    (println "Node:" (jme/get* node :name) "children:" (count (jme/get* node :children)))
                    (doseq [child (jme/get* node :children)]
                      (apply-material child)))))]
        (apply-material character)))

    (-> character
        (jme/set* :local-translation (jme/vec3 0 0 0))
        (jme/set* :local-scale (float 0.0001))
        (jme/add-to-root))

    (println "Added character at scale 0.0001")
    (println "Will cycle through scales every 2 seconds...")
    (println "Watch the console for scale changes!")))

(jme/unbind-app #'app)
(jme/run app
      (jme/re-init init))

(jme/defsimpleapp app :init init :update update-all)

(jme/start app)