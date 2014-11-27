(ns chdfs-simple.index
  (:use
      [clojure.tools.logging :only (info error)]
      [org.httpkit.server]
      [compojure.core]
      )
  (:require
            [ring.middleware.json :as middleware]
            [compojure.handler :as handler]
            [compojure.route :as route])
  (:gen-class))

(def configmy (clojure.edn/read-string (slurp "conf/path.edn")))

(import 'com.vip.mlk.hdfs.MapFileLookup)

(defn getInfo ^{:tag clojure.lang.PersistentArrayMap} [req]
 {:status 200
                   :headers {"Content-Type" "application/json; charset=utf-8"}
                   :body [1,2]}

 )






(defroutes all-routes
  (GET "/req" []  getInfo)
  (GET "/p" []  (fn [req] (pr-str (:params req))))
  (GET "/b" []   { :status 200
                   :headers {"Content-Type" "application/json; charset=utf-8"}
                   :body "callback=jerry({})" })
  )


(def app
  (->
   (handler/api all-routes)
   (middleware/wrap-json-body)
   (middleware/wrap-json-params)
   (middleware/wrap-json-response)
  )
)




(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main [&args]
    (info  (format "start server .... on port :8811  " )  )
  (reset! server (run-server #'app {:port 8811}))
  )
