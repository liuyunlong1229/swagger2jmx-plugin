
#### V1.1 新增功能介绍

* 支持通过初始参数值默认生成 <br>
生成jmeter脚本会根据参数的默认值（即取@ApiImplicitParam的defaultValue和@ApiModelProperty的example的值）生成。

* 支持测试用例生成的顺序 <br>
@ApiOperation维护扩展属性{@Extension(name="ext",properties = @ExtensionProperty(name = "sortNo", value = "1"))})，指定sortNo的值，值越小，生成测试用例越靠前。

***


#### Step 1: 获取工具包

1、直接到 [latest stable release](https://github.com/liuyunlong1229/swagger2jmx-plugin/releases).下载

2、有maven环境的话，直接在本地执行mvn package生成

#### Step 2: 解压包后，运行bin目录下的startup.cmd文件，运行前先设置2个参数值

【SWAGGER_LOCATION】参数：指定swagger的源，可以是本地文件，或者线上的swagger地址。

* **方式一：线上swagger** 

```sh
SWAGGER_LOCATION=http://localhost:18083/v2/api-docs

``` 

* **方式二：本地swagger的文件，也就是上面的线上显示的整个大的json内容保存到本地一个文件中**。

```sh
SWAGGER_LOCATION=D:/swagger.json

``` 

【JMX_FILE_DIR】参数：指定生成jmeter脚本`auto_test.jmx`生成的位置
例如保存到D:/jmeter目录下，可以这么设置

```sh
JMX_FILE_DIR=D:/jmeter/

``` 
#### 效果
![Image text](https://images.gitee.com/uploads/images/2020/1107/211059_003c5955_1615225.png)
