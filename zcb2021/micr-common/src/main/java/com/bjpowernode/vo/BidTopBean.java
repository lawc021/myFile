package com.bjpowernode.vo;

import javafx.beans.property.ObjectProperty;

import java.io.Serializable;

public class BidTopBean implements Serializable {

    private static final long serialVersionUID = -8426328703933691465L;
    private String phone;
    private Double bidMoney;

    public BidTopBean() {
    }

    public BidTopBean(String phone, Double bidMoney) {
        this.phone = phone;
        this.bidMoney = bidMoney;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getBidMoney() {
        return bidMoney;
    }

    public void setBidMoney(Double bidMoney) {
        this.bidMoney = bidMoney;
    }
}
