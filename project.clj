(defproject org.clojars.vladimirmarkovic86/audit-lib "0.1.35"
  :description "Audit library"
  :url "http://github.com/VladimirMarkovic86/audit-lib"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojars.vladimirmarkovic86/mongo-lib "0.2.11"]
                 [org.clojars.vladimirmarkovic86/utils-lib "0.4.12"]
                 [org.clojars.vladimirmarkovic86/session-lib "0.2.27"]
                 ]

  :min-lein-version "2.0.0"
  
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  
  :jar-exclusions [#"README.md$"
                   #"LICENSE$"])

