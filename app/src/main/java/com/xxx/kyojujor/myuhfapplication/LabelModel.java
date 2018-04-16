package com.xxx.kyojujor.myuhfapplication;

public class LabelModel {
    private String EpcName;
    private String count;

    public LabelModel(String epcName,String count)
    {

        this.EpcName = epcName;
        this.count = count;
    }

    public String getEpcName() {
        return EpcName;
    }

    public void setEpcName(String epcName) {
        EpcName = epcName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
