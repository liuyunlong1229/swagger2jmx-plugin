一、获取工具包

1、直接到 https://github.com/liuyunlong1229/swagger2jmx-plugin/archive/v0.0.2.zip 下载

2、有maven环境的话，直接在本地执行mvn package生成

二、解压包后，运行bin目录下的启动startup.cmd文件，运行前先设置2个参数值

【SWAGGER_LOCATION】参数：指定swagger的源，可以是本地文件，或者线上的swagger地址。

方式一：线上swagger：
SWAGGER_LOCATION=http://localhost:18083/v2/api-docs

方式二：本地swagger的文件，也就是上面的线上显示的整个大的json内容保存到本地一个文件中。
SWAGGER_LOCATION=D:/swagger.json


【JMX_FILE_DIR】参数：指定生成jmeter脚本auto_test.jmx生成的位置
例如保存到D:/jmeter目录下，可以这么设置

JMX_FILE_DIR=D:/jmeter/
