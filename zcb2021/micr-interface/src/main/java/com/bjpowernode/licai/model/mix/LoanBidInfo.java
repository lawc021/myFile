package com.bjpowernode.licai.model.mix;

import com.bjpowernode.licai.model.BidInfo;
import java.io.Serializable;

//产品信息和投资信息的组合
public class LoanBidInfo extends BidInfo implements Serializable {
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
