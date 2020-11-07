package com.lyl.plugin;

/**
 * 运行参数对象
 * @author yunlong.liu
 * @date 2020-11-06 11:35:10
 */

public class EnvArgs {

    /**
     * swagger地址，可以是网上的地址，也可以是本地文件
     */
    private String swaggerAddr;

    /**
     * jmeter的脚本生成位置
     */
    private String fileOutput;

    public String getSwaggerAddr() {
        return swaggerAddr;
    }

    public void setSwaggerAddr(String swaggerAddr) {
        this.swaggerAddr = swaggerAddr;
    }

    public String getFileOutput() {
        return fileOutput;
    }

    public void setFileOutput(String fileOutput) {
        this.fileOutput = fileOutput;
    }
}
