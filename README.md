# chdfs-simple
这是一个简单的web api(clojure/http-kit)方式访问hdfs／mapfile文件的入口，配置简单，使用方便；但是现在的版本只支持hadoop-client "2.2.0"，当然后期还会支持sequenceFile的key/value查询。

# 配置
文件conf/path.edn
<pre><code>
{
 :hdfs_path      "/spark/out2"
 :server_port    8988
}
</code></pre>
:hdfs_path 是hdfs文件上的mapfile路径
:server_port 是启动web的端口号
