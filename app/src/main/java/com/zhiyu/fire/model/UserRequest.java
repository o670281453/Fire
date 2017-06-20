package com.zhiyu.fire.model;

/**
 * Created by Administrator on 2017/6/8 0008.
 * 用户数据模型类
 */

public class UserRequest extends ViewModel {

    private static User sUser;

    private UserRequest() {}

    //请求状态码，1表示成功，其它都表示请求失败
    private int status;

    private User user;

    public static void setsUser(User sUser) {
        UserRequest.sUser = sUser;
    }

    public static User getsUser() {
        return sUser;
    }

    public User getUser() {
        return user;
    }

    public int getStatus() {
        return status;
    }

    public static class User {

        //消防员用户名（消防编号）
        private String username;

        //身份验证令牌
        private String token;

        //权限等级
        private int rank;

        //所属机构编号
        private int departmentID;

        //消防员姓名
        private String name;

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public int getRank() {
            return rank;
        }

        public int getDepartmentID() {
            return departmentID;
        }

        public String getName() {
            return name;
        }

    }

}