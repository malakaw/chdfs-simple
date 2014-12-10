(ns chdfs-simple.core
  (:use
      [clojure.tools.logging :only (info error)]
      [ring.middleware.jsonp :only (wrap-json-with-padding)]
      [org.httpkit.server]
      [compojure.core]
      [compojure.route]
      [compojure.handler :only [site]] ; form, query params decode; cookie; session, etc
      [compojure.core :only [defroutes GET POST DELETE ANY context]]
      [ring.util.response :only (response content-type)]
      [clojure.string     :only (lower-case)]
      [ring.middleware.params         :only [wrap-params]]
      [chdfs-simple.brandCache :only [callUpdateBrandMap getbrand getbrands]]
      )
  (:require
            [ring.middleware.json :as middleware]
            )
  (:gen-class))


(import com.vip.mlk.hdfs.MapFileLookup)

(require 'clojure.edn)
(require '[clojure.string :as str])
(use 'overtone.at-at)

(def schedule-pool (mk-pool))




(def configmy (clojure.edn/read-string (slurp "conf/path.edn")))

(defn getInfo [req]
 {:status 200
                   :headers {"Content-Type" "application/json; charset=utf-8"}
                   :body  req}

 )

(defn stop_AllSchedule []
  (stop-and-reset-pool! schedule-pool)
  )

(defn show-Allschedule []
  (show-schedule schedule-pool)
  )



(defn callUpdateHDFS []
  (every (* 1000 60 (:update_every_minute configmy))
         #(do
           (info "update hdfs file")
           (-> (com.vip.mlk.hdfs.MapFileLookup/getInstance  (:hdfs_path configmy))
               (.update (:hdfs_path configmy)
                       ))
         )
       schedule-pool :fixed-delay true :initial-delay (* 1000 60 (:update_delay_minute configmy)))
  )






(defn  getJsonpValueFromMapFile ^{:tag clojure.lang.PersistentArrayMap}  [req]
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




(defn  getValueFromMapFile ^{:tag clojure.lang.PersistentArrayMap}  [req]
  {:status 200
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body
   (let [getval
         (-> (com.vip.mlk.hdfs.MapFileLookup/getInstance  (:hdfs_path configmy))
             (.getValueByteV (get (-> req :params) "key"))
         )
         ]
;;     (remove nil? (for [i (str/split tom #",")] (if (contains? jerry i) (jerry i))))

     (format "{\"data\" : \"%s\"}" (clojure.string/join "," getval) ))
   }
  )


(defn  getBrandbyindexID   [req]
  {:status 200
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body
   (let [getval
         (getbrand (get (-> req :params) "key"))
         ]
     (format "{\"data\" : \"%s\"}"  getval))
   }
  )


(defn  getBrandbyindexIDS   [req]
   {:status 200
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body
   (let [getval
         (-> (com.vip.mlk.hdfs.MapFileLookup/getInstance  (:hdfs_path configmy))
             (.getValueByteV (get (-> req :params) "key"))
         )
         ]
;;     (remove nil? (for [i (str/split tom #",")] (if (contains? jerry i) (jerry i))))

     (format "{\"data\" : [%s] }"      (getbrands getval) )


     )
   }
  )


(defroutes all-routes

  (GET "/req" []  getInfo)
  (GET "/jsonp_getvalue" []  getJsonpValueFromMapFile)
  (GET "/getvalue" [] getValueFromMapFile)
  (GET "/b" [] getBrandbyindexID)
  (GET "/bids" [] getBrandbyindexIDS)
  )







(def app
  (->
   (wrap-params all-routes)
   (wrap-json-with-padding)
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
  (do
   (start-server)
   (callUpdateBrandMap)
  )
  )
