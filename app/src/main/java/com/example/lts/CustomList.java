package com.example.lts;

import java.io.Serializable;

public class CustomList implements Serializable {
    String id;
    String username;// username
    String fromDate;
    String toDate;
    double daysCount;
    String entryDate;
    String status;
    String leaveType;
    String description;
    int image; // drawable reference id

    public CustomList(String id,String username, String fromDate, String toDate, double daysCount, String entryDate,String status ,String leaveType,
            String description,int image)
    {
        this.id=id;
        this.username = username;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.daysCount = daysCount;
        this.entryDate = entryDate;
        this.status = status;
        this.leaveType = leaveType;
        this.description = description;
        this.image = image;
    }
}
