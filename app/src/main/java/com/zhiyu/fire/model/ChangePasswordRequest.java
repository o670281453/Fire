package com.zhiyu.fire.model;

/**
 * Created by Administrator on 2017/6/20 0020.
 * 修改密码对应的数据返回类
 */

public class ChangePasswordRequest {

    private int status;

    private Data data;

    public int getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public static class Data {

        private String token;

        public String getToken() {
            return token;
        }

    }

}