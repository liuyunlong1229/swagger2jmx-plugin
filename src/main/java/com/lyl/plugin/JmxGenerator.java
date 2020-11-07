package com.lyl.plugin;


import com.lyl.plugin.generate.MyDefaultGenerator;
import org.apache.commons.lang3.StringUtils;

/**
 * 生成器，程序主入口
 * @author yunlong.liu
 * @date 2020-11-03 19:42:43
 */

public class JmxGenerator {

    public static void main(String[] args) {

        // generate -i swagger.json -g jmeter

        EnvArgs envArgs= parseArgs(args);

        MyDefaultGenerator myDefaultGenerator=new MyDefaultGenerator();
        try {
            myDefaultGenerator.generate(envArgs.getSwaggerAddr(),envArgs.getFileOutput());
            System.out.println("++++++++生成好了 ,文件地址:"+envArgs.getFileOutput()+"/auto_test.jmx");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


     static EnvArgs parseArgs(String [] args){

//       if(args==null ||  args.length <2){
//           System.err.println("请通过参数swagger地址和脚本输出目录");
//           System.exit(1);
//       }
        // "--i=http://localhost:18083/v2/api-docs";
        // "--o=D:/";
          EnvArgs envArgs=new  EnvArgs();
        for(int i=0;i<args.length;i++){
            if(args[i].startsWith("--i=")){
                String [] source= StringUtils.split(args[i],"=");
                if(source.length==2) {
                    envArgs.setSwaggerAddr(source[1].trim());
                }
            }

            if(args[i].startsWith("--o=")){
                String [] source= StringUtils.split(args[i],"=");
                if(source.length==2) {
                    envArgs.setFileOutput(source[1].trim());
                }
            }

            if(StringUtils.isNotBlank(envArgs.getSwaggerAddr()) && StringUtils.isNotBlank(envArgs.getFileOutput()) ){
                break;
            }
        }

        if(StringUtils.isBlank(envArgs.getSwaggerAddr())){
            System.err.println("请通过[--i=]方式声明swagger地址");
            System.exit(1);
        }

        if(StringUtils.isBlank(envArgs.getFileOutput())){
              System.err.println("请通过[--o=]方式声明生成jmeter脚本目录");
              System.exit(1);
        }

          return envArgs;
    }
}
