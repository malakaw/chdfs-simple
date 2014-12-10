(ns chdfs-simple.brandCache
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

      )
  (:require
            [ring.middleware.json :as middleware]
            )
  (:gen-class))

(require '[clojure.string :as str])
(import com.vip.mlk.hdfs.HdfsReader)

(require 'clojure.edn)

(use 'overtone.at-at)

(def schedule-pool2 (mk-pool))




(def configmy (clojure.edn/read-string (slurp "conf/path.edn")))

(defn stop_AllSchedule2 []
  (stop-and-reset-pool! schedule-pool2)
  )

(defn show-Allschedule2 []
  (show-schedule schedule-pool2)
  )

;;brand的映射map
(def map_brandinfo (atom {}))


(defn add2bmap [key brand_id]
  (swap! map_brandinfo  assoc key brand_id))

(defn getbrand [key]
  (let [resu
        (if   (contains? @map_brandinfo key)
          (@map_brandinfo key)
          ""
          )
        ]
    (info resu)
    resu)
  )



(defn getbrands [keys]
  (let [bids
        (remove nil? (for [i keys]
                       (if (contains? @map_brandinfo  i)
                         (@map_brandinfo i))
                       )
                     )
        ]
    (clojure.string/join "," (vec  bids))
    )
  )






(defn callUpdateBrandMap []
  (every (* 1000 (:readBrandMap_every_minute configmy))
         #(do


            (let [ [info_  set_indexid]
                   [
                    (try
                         (into []  (com.vip.mlk.hdfs.HdfsReader/read (:hdfs_brandmap_2 configmy)))
                       (catch Exception e
                         (error "Exception message: " (.getMessage e)))
                       (finally
                         (info "Done.")))
                    (keys @map_brandinfo)
                    ]
                  ]
              (info "do delete.... ")
              (try
                (doseq [iid set_indexid]
                 (swap! map_brandinfo dissoc iid)
                 )
                (catch Exception e
                      (error "Exception message: " (.getMessage e)))
               )
               (info "do insert.... ")
               (doseq [x info_]
                 (add2bmap  (nth (str/split x #"	") 0) (nth (str/split x #"	") 2)))
               )
               (info "do insert over.... ")
             (let [info_
                    (try
                       (into []  (com.vip.mlk.hdfs.HdfsReader/read (:hdfs_brandmap_1 configmy)))
                    (catch Exception e
                      (error "Exception message: " (.getMessage e)))
                    (finally
                       (info "Done.")))
                  ]
               (doseq [x info_] (add2bmap  (nth (str/split x #"	") 0) (nth (str/split x #"	") 1)))
               )
            (info (format "add2map size:%s" (count @map_brandinfo)) )
           ;; (info "update  brand map")
           ;; (println "tom-------------")
         )
       schedule-pool2 :fixed-delay true)
  )



(defn -main [&args]
    (callUpdateBrandMap (:hdfs_brandmap configmy))
  )
