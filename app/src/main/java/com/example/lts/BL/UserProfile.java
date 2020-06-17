package com.example.lts.BL;

import java.io.Serializable;

public class UserProfile  implements Serializable {
    String userId ;
    String name;
    String mobile;
    String address;


    public UserProfile() {

    }

    public UserProfile(String userId, String name,String mobile, String address)
    {
        this.userId = userId;
        this.name = name;
        this.mobile = mobile;
        this.address = address;

    }

}
