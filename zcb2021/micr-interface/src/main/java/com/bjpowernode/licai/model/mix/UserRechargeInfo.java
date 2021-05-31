package com.bjpowernode.licai.model.mix;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class UserRechargeInfo implements Serializable {

    private static final long serialVersionUID = 1490657334360454361L;

    private BigDecimal cmoney;
    private String cresult;
    private Date ctime;

    public BigDecimal getCmoney() {
        return cmoney;
    }

    public void setCmoney(BigDecimal cmoney) {
        this.cmoney = cmoney;
    }

    public String getCresult() {
        return cresult;
    }

    public void setCresult(String cresult) {
        this.cresult = cresult;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }
}
