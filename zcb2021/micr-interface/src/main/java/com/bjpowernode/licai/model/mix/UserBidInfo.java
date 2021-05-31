package com.bjpowernode.licai.model.mix;

import com.bjpowernode.licai.model.BidInfo;

import java.io.Serializable;

public class UserBidInfo extends BidInfo implements Serializable {

    private static final long serialVersionUID = -1787196573180960953L;

    private String prodcutName;

    public String getProdcutName() {
        return prodcutName;
    }

    public void setProdcutName(String prodcutName) {
        this.prodcutName = prodcutName;
    }
}
