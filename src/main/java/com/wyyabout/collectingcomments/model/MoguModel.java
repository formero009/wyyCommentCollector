package com.wyyabout.collectingcomments.model;

import java.util.List;

public class MoguModel {
    private String code;
    private List<Proxy> msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Proxy> getMsg() {
        return msg;
    }

    public void setMsg(List<Proxy> msg) {
        this.msg = msg;
    }
}
