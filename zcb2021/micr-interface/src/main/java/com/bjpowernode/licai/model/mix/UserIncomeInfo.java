package com.bjpowernode.licai.model.mix;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UserIncomeInfo implements Serializable {

    private static final long serialVersionUID = -4025685889703397407L;

    private String productName;
    private BigDecimal imoney;
    private Date itime;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getImoney() {
        return imoney;
    }

    public void setImoney(BigDecimal imoney) {
        this.imoney = imoney;
    }

    public Date getItime() {
        return itime;
    }

    public void setItime(Date itime) {
        this.itime = itime;
    }
}
