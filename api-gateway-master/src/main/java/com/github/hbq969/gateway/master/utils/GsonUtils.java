package com.github.hbq969.gateway.master.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : hbq969@gmail.com
 * @description : gson工具类
 * @createTime : 2024/9/25 15:35
 */
public class GsonUtils {
    public final static Gson gson = new Gson();

    public static final String toJSONString(Object object) {
        return gson.toJson(object);
    }

    public static <T> T parse(String str, Class<T> t) {
        return gson.fromJson(str, t);
    }

    public static <T> List<T> parseArray(String str, TypeToken<List<T>> token) {
        return gson.fromJson(str, token.getType());
    }

    public static void main(String[] args) {
        String str = "[{\"age\":10,\"name\":\"zhangsan\"}]";
        List<MyFoo> list = parseArray(str, new TypeToken<List<MyFoo>>() {
        });
        for (MyFoo myFoo : list) {
            System.out.println(myFoo);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class MyFoo {
        private String name;
        private int age;
    }
}
