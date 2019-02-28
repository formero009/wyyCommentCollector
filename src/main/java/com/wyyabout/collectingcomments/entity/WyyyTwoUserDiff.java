package com.wyyabout.collectingcomments.entity;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "wyyy_two_user_diff")
public class WyyyTwoUserDiff {

    @Id
    private int id;
    private String sname;
    private String sid;
    private String slistname;
    private String userid1;
    private String userid2;
    private String create_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSlistname() {
        return slistname;
    }

    public void setSlistname(String slistname) {
        this.slistname = slistname;
    }

    public String getUserid1() {
        return userid1;
    }

    public void setUserid1(String userid1) {
        this.userid1 = userid1;
    }

    public String getUserid2() {
        return userid2;
    }

    public void setUserid2(String userid2) {
        this.userid2 = userid2;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
