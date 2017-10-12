package com.ycb.socket.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by zhuhui on 17-8-21.
 */
public class Order implements Serializable {

    private static final long serialVersionUID = 3966752733263935774L;

    //@MetaData("订单id")
    private String orderid;

    //@MetaData("用户")
    private Long customerid;

    //@MetaData("平台")
    private Integer platform = 0;

    //@MetaData("状态")
    private Integer status = 0;

    //@MetaData("借出时间")
    private Date borrowTime;

    //@MetaData("归还时间")
    private Date returnTime;

    //@MetaData("借出商铺id")
    private Long borrowShop;

    //@MetaData("归还商铺id")
    private Long returnShop;

    //@MetaData("借出设备id")
    private Long borrowStation;

    //@MetaData("归还设备id")
    private Long returnStation;

    //@MetaData("借出商铺站点id")
    private Long borrowShopStation;

    //@MetaData("归还商铺站点id")
    private Long returnShopStation;

    //@MetaData("借出电池id")
    private String borrowBattery;

    //@MetaData("借出电池线类型")
    private String cable;

    //@MetaData("订单价格")
    private BigDecimal price = BigDecimal.ZERO;

    //@MetaData("已退款金额")
    private BigDecimal refunded;

    //@MetaData("租金")
    private BigDecimal usefee;

    //@MetaData("已付款")
    private BigDecimal paid;

    private Long feeSettings;

    private String openid;

    private String address;

    //@MetaData("信用借还的订单号")
    //@Column(name = "order_no")
    private String orderNo;

    //@MetaData("支付宝的资金流水号")
    //@Column(name = "alipay_fund_order_no")
    private String alipayFundOrderNo;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public Long getCustomerid() {
        return customerid;
    }

    public void setCustomerid(Long customerid) {
        this.customerid = customerid;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(Date borrowTime) {
        this.borrowTime = borrowTime;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    public Long getBorrowShop() {
        return borrowShop;
    }

    public void setBorrowShop(Long borrowShop) {
        this.borrowShop = borrowShop;
    }

    public Long getReturnShop() {
        return returnShop;
    }

    public void setReturnShop(Long returnShop) {
        this.returnShop = returnShop;
    }

    public Long getBorrowStation() {
        return borrowStation;
    }

    public void setBorrowStation(Long borrowStation) {
        this.borrowStation = borrowStation;
    }

    public Long getReturnStation() {
        return returnStation;
    }

    public void setReturnStation(Long returnStation) {
        this.returnStation = returnStation;
    }

    public Long getBorrowShopStation() {
        return borrowShopStation;
    }

    public void setBorrowShopStation(Long borrowShopStation) {
        this.borrowShopStation = borrowShopStation;
    }

    public Long getReturnShopStation() {
        return returnShopStation;
    }

    public void setReturnShopStation(Long returnShopStation) {
        this.returnShopStation = returnShopStation;
    }

    public String getBorrowBattery() {
        return borrowBattery;
    }

    public void setBorrowBattery(String borrowBattery) {
        this.borrowBattery = borrowBattery;
    }

    public String getCable() {
        return cable;
    }

    public void setCable(String cable) {
        this.cable = cable;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getRefunded() {
        return refunded;
    }

    public void setRefunded(BigDecimal refunded) {
        this.refunded = refunded;
    }

    public BigDecimal getUsefee() {
        return usefee;
    }

    public void setUsefee(BigDecimal usefee) {
        this.usefee = usefee;
    }

    public BigDecimal getPaid() {
        return paid;
    }

    public void setPaid(BigDecimal paid) {
        this.paid = paid;
    }

    public Long getFeeSettings() {
        return feeSettings;
    }

    public void setFeeSettings(Long feeSettings) {
        this.feeSettings = feeSettings;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getAlipayFundOrderNo() {
        return alipayFundOrderNo;
    }

    public void setAlipayFundOrderNo(String alipayFundOrderNo) {
        this.alipayFundOrderNo = alipayFundOrderNo;
    }
}
