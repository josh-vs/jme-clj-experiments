(defproject jme-clj-expirements "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 ;; jme-clj with critical exclusions
                 [jme-clj "0.1.13" 
                  :exclusions [com.github.stephengold/Heart
                               org.jmonkeyengine/lwjgl-platform
                               org.jmonkeyengine/jme3-core
                               org.jmonkeyengine/jme3-desktop
                               org.jmonkeyengine/jme3-lwjgl]
                  ]
                 [org.jmonkeyengine/jme3-core "3.8.0-stable"]
                 [org.jmonkeyengine/jme3-desktop "3.8.0-stable"]
                 [org.jmonkeyengine/jme3-lwjgl "3.8.0-stable"]
                 [org.jmonkeyengine/jme3-lwjgl3 "3.8.0-stable"]
                 [org.jmonkeyengine/lwjgl-platform "2.9.5" :extension "pom"]
                 [com.github.stephengold/Heart "9.2.0"]
                 [com.github.stephengold/MonkeyWrench "1.0.0"]
                 [com.github.stephengold/Wes "0.8.1"]
                 [com.twelvemonkeys.imageio/imageio-tga "3.12.0"]
                 [com.twelvemonkeys.imageio/imageio-webp "3.12.0"]
                 [org.lwjgl/lwjgl "3.3.6"]
                 [org.lwjgl/lwjgl "3.3.6" :classifier "natives-linux"]
                 [org.lwjgl/lwjgl "3.3.6" :classifier "natives-windows"]
                 [org.lwjgl/lwjgl "3.3.6" :classifier "natives-macos"]
                 [org.lwjgl/lwjgl-assimp "3.3.6"]
                 [org.lwjgl/lwjgl-assimp "3.3.6" :classifier "natives-linux"]
                 [org.lwjgl/lwjgl-assimp "3.3.6" :classifier "natives-windows"]
                 [org.lwjgl/lwjgl-assimp "3.3.6" :classifier "natives-macos"]
                 ]
  
  :main ^:skip-aot jme-clj-expirements.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})