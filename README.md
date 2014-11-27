# chdfs-simple
这是一个简单的web api(clojure/http-kit)方式访问hdfs／mapfile文件的入口，配置简单，使用方便,支持jsonp方式访问；但是现在的版本只支持hadoop-client "2.2.0"。

目前的key/value只支持 string/string.

# 配置
文件conf/path.edn
<pre><code>
{
 :hdfs_path      "/spark/out2"
 :server_port    8988
}
</code></pre>
:hdfs_path 是hdfs文件上的mapfile路径
<br/>
:server_port 是启动web的端口号


# 运行
clone 整个项目，然后放的hadoop集群上的某台机器上，只要本地目录就可以；修改配置，
<pre><code>
hadoop jar chdfs-simple-0.0.1-standalone.jar chdfs-simple.core
</code></pre>



## 打包
当然你也可以修改代码 ，就用lein uberjar 来打包。
<br/> 这里你要使用到我的另外一个repository   [https://github.com/malakaw/java_malaka_hdfsUtil](https://github.com/malakaw/java_malaka_hdfsUtil)


## 访问
<pre><code>
http://xxxx/getvalue?key=...
jsonp方式，  http://xxxx/jsonp_getvalue?key=...
</code></pre>
