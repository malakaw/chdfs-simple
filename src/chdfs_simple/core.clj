(ns chdfs-simple.core
  (:use
      [clojure.tools.logging :only (info error)]
      [org.httpkit.server]
      [compojure.core]
      [compojure.route]
      [compojure.handler :only [site]] ; form, query params decode; cookie; session, etc
      [compojure.core :only [defroutes GET POST DELETE ANY context]]
      [ring.util.response :only (response content-type)]
      [clojure.string     :only (lower-case)]
      [ring.middleware.params         :only [wrap-params]]

      )
  (:require
            [ring.middleware.json :as middleware]
            [compojure.handler :as handler]
            [compojure.route :as route])
  (:gen-class))


(import com.vip.mlk.hdfs.MapFileLookup)

(require 'clojure.edn)
(def configmy (clojure.edn/read-string (slurp "conf/path.edn")))

(defn getInfo [req]
 {:status 200
                   :headers {"Content-Type" "application/json; charset=utf-8"}
                   :body  req}

 )







(defn getValueFromMapFile ^{:tag clojure.lang.PersistentArrayMap}  [req]
  {:status 200
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body
   (let [getval
         (-> (com.vip.mlk.hdfs.MapFileLookup/getInstance  (:hdfs_path configmy))
             (.getValue (get (-> req :params) "key"))
            )
         ]
     (format "callback=f({\"data\" : \"%s\"})" getval))
   }
  )




(defn getJsonpValueFromMapFile ^{:tag clojure.lang.PersistentArrayMap}  [req]
  {:status 200
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body
   (let [getval
         (-> (com.vip.mlk.hdfs.MapFileLookup/getInstance  (:hdfs_path configmy))
             (.getValue (get (-> req :params) "key"))
            )
         ]
     (format "{\"data\" : \"%s\"}" getval))
   }
  )


(defroutes all-routes
  (GET "/req" []  getInfo)
  (GET "/jsonp_getvalue" []  (fn [req] (pr-str (:params req))))
  (GET "/getvalue" [] getValueFromMapFiletest)
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

(defn start-server []
    (info  (format "start server .... on port :%s " (:server_port configmy))  )
    (reset! server (run-server #'app {:port (:server_port configmy)}))
  )




(defn -main [&args]
  (start-server)
  )
