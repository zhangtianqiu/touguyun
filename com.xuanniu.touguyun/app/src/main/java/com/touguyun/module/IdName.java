package com.touguyun.module;
/**
 * Created by zhengyonghui on 15/9/1.
 */
public class IdName extends TouguJsonObject{

    public long id;
    public String name;
    /**
     * 适配接口
     */
    public String subject;
    public void setSubject(String subject) {
        this.subject = subject;
        this.name = subject;
    }
}
