package com.lyl.test;


import com.lyl.test.generate.MyDefaultGenerator;

/**
 * @author yunlong.liu
 * @date 2020-11-03 19:42:43
 */

public class Main {

    public static void main(String[] args) {

        // generate -i swagger.json -g jmeter

        MyDefaultGenerator myDefaultGenerator=new MyDefaultGenerator();
        try {
          //  myDefaultGenerator.generate("D:\\jmeter\\api-docs.json");
            myDefaultGenerator.generate("http://localhost:18084/v2/api-docs","D:/");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
