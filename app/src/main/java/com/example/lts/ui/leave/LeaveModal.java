package com.example.lts.ui.leave;

import com.example.lts.R;

import java.io.Serializable;

public class LeaveModal implements Serializable {

    String userId ;
    String fromDate;
    String toDate;
    Integer daysCount;
    String enntryDate;
    String status;
    String leaveType;
    String description;

    public LeaveModal() {

    }

    public LeaveModal(String userId, String fromDate, String toDate, Integer daysCount ,String entryDate,String status,String leaveType,String description)
    {
       this.userId = userId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.daysCount = daysCount;
        this.status = status;
        this.leaveType=leaveType;
        this.description=description;
        this.enntryDate=entryDate;
    }

}
