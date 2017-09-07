package com.ycb.socket.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by duxinyuan on 17-6-27.
 */
public class FeeStrategy implements Serializable {

    private static final long serialVersionUID = -2393239113465655945L;

    //@MetaData("策略名称")
    private String name;

    //@MetaData("意外借出免费时长")
    private Long freeTime;

    //@MetaData("意外借出免费时长单位")
    private Long freeUnit;

    //@MetaData("固定收费时长")
    private Long fixedTime;

    //@MetaData("固定收费时长单位")
    private Long fixedUnit;

    //@MetaData("固定费用")
    private BigDecimal fixed;

    //@MetaData("超出计费")
    private BigDecimal fee;

    //@MetaData("超出计费时长单位")
    private Long feeUnit;

    //@MetaData("最高收费时长")
    private Long maxFeeTime;

    //@MetaData("最高收费时长单位")
    private Long maxFeeUnit;

    //@MetaData("最高收费金额")
    private BigDecimal maxFee;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFreeTime() {
        return freeTime;
    }

    public void setFreeTime(Long freeTime) {
        this.freeTime = freeTime;
    }

    public Long getFreeUnit() {
        return freeUnit;
    }

    public void setFreeUnit(Long freeUnit) {
        this.freeUnit = freeUnit;
    }

    public Long getFixedTime() {
        return fixedTime;
    }

    public void setFixedTime(Long fixedTime) {
        this.fixedTime = fixedTime;
    }

    public Long getFixedUnit() {
        return fixedUnit;
    }

    public void setFixedUnit(Long fixedUnit) {
        this.fixedUnit = fixedUnit;
    }

    public BigDecimal getFixed() {
        return fixed;
    }

    public void setFixed(BigDecimal fixed) {
        this.fixed = fixed;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Long getFeeUnit() {
        return feeUnit;
    }

    public void setFeeUnit(Long feeUnit) {
        this.feeUnit = feeUnit;
    }

    public Long getMaxFeeTime() {
        return maxFeeTime;
    }

    public void setMaxFeeTime(Long maxFeeTime) {
        this.maxFeeTime = maxFeeTime;
    }

    public Long getMaxFeeUnit() {
        return maxFeeUnit;
    }

    public void setMaxFeeUnit(Long maxFeeUnit) {
        this.maxFeeUnit = maxFeeUnit;
    }

    public BigDecimal getMaxFee() {
        return maxFee;
    }

    public void setMaxFee(BigDecimal maxFee) {
        this.maxFee = maxFee;
    }
}
