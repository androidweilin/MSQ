package com.wkbp.msq.result.bean;

import java.io.Serializable;

public class GsonConfirmOrderBack implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -6017040387255953805L;
    private String result;
    private String resultNote;
    private String orderID;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultNote() {
        return resultNote;
    }

    public void setResultNote(String resultNote) {
        this.resultNote = resultNote;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    @Override
    public String toString() {
        return "GsonConfirmOrderBack [result=" + result + ", resultNote="
                + resultNote + ", orderID=" + orderID + "]";
    }

}
