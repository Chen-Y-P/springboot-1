package com.xwbing.demo;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2018/1/23 14:45
 * 作者: xiangwb
 * 说明: 测试用
 */
public class Ademo {
    public static void main(String[] args) {
        Map<String, String> aa = new HashMap<>();
        aa.put(null,"aa");
        String s = aa.get(null);
        System.out.println(s);

    }

}