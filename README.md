
2、安装包下载
  https://github.com/liuyunlong1229/swagger2jmx-plugin/releases/ 


3、解压后，运行bin目录下的启动startup.cmd文件，运行前先设置2个参数值

【SWAGGER_LOCATION】参数：指定swagger的源，可以是本地文件，或者线上的swagger地址。

方式一：线上swagger：
SWAGGER_LOCATION=http://localhost:18083/v2/api-docs

方式二：本地swagger的文件：
SWAGGER_LOCATION=D:/swagger.json


【JMX_FILE_DIR】参数：指定生成jmeter脚本auto_test.jmx生成的位置，一定要是存在的目录
JMX_FILE_DIR=D:/jmeter/
