(defproject chdfs-simple "0.0.1"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [com.vip.nproject/java_malaka_hdfsUtil  "0.0.1"]
                 [org.clojure/clojure "1.6.0"]
                 [http-kit "2.1.16"]
                 [javax.servlet/servlet-api "2.5"]
                 [compojure "1.1.8"]
                 [ring/ring-json "0.2.0"]
                 [ring.middleware.jsonp "0.1.6"]
                 [org.apache.hadoop/hadoop-client "2.2.0"]
                 [com.taoensso/carmine "2.7.0"]
                 [org.clojure/tools.logging "0.2.4"]
                 [org.slf4j/slf4j-log4j12 "1.7.1"]
                 [log4j/log4j "1.2.17" :exclusions [javax.mail/mail
               	                                           javax.jms/jms
                                                            com.sun.jmdk/jmxtools
                                                            com.sun.jmx/jmxri]]
                 [commons-net/commons-net "2.2"]
                 [commons-logging "1.1.1"]
                 ]
  :main ^:skip-aot chdfs-simple.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
