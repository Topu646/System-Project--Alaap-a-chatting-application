package com.example.alaap;

public class PostItem {
    String userpost;
    String username;

    public PostItem(String username,String userpost) {
        this.username = username;
        this.userpost = userpost;
    }

    public PostItem()
    {

    }
    public String getUserpost() {
        return userpost;
    }

    public String getUsername() {
        return username;
    }

    public void setUserpost(String userpost) {
        this.userpost = userpost;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
