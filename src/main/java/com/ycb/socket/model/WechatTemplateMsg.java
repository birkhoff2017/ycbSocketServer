package com.ycb.socket.model;

import java.util.TreeMap;

/**
 * Created by yunchongba on 2017/8/29.
 */
public class WechatTemplateMsg {

    private String touser; //接收者openid

    private String template_id; //模板ID

    private String page; //模板跳转链接

    private String form_id;  //表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id

    private TreeMap<String, TreeMap<String, String>> data; //data数据

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public TreeMap<String, TreeMap<String, String>> getData() {
        return data;
    }

    public void setData(TreeMap<String, TreeMap<String, String>> data) {
        this.data = data;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    /**
     * 参数
     * @param value
     * @param color 可不填
     * @return
     */
    public static TreeMap<String, String> item(String value, String color) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("value", value);
        params.put("color", color);
        return params;
    }
}
