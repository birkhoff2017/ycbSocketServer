package com.ycb.socket.service;

import com.ycb.socket.dao.FeeStrategyDao;
import com.ycb.socket.model.FeeStrategy;
import com.ycb.socket.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * Created by zhuhui on 17-8-28.
 */
@Service
public class FeeStrategyService {

    public static final Logger logger = LoggerFactory.getLogger(FeeStrategyService.class);

    @Autowired
    private FeeStrategyDao feeStrategyDao;

    /**
     * 收费标准
     *
     * @param feeStrategy
     * @return
     */
    public String transFeeStrategy(FeeStrategy feeStrategy) {
        //固定收费
        Long fixedTime = feeStrategy.getFixedTime();
        Long fixedUnit = feeStrategy.getFixedUnit();
        BigDecimal fixed = feeStrategy.getFixed();
        //String fixedStr = StringUtils.isEmpty(fixed) ? "" : "每" + fixedTime + TimeUtil.getUnitString(fixedUnit) + "收费" + fixed + "元。";
        String fixedStr = StringUtils.isEmpty(fixed) ? "" : fixedTime + TimeUtil.getUnitString(fixedUnit) + "免费时长，";

        //超出后每小时收费1元。
        BigDecimal fee = feeStrategy.getFee();
        Long feeUnit = feeStrategy.getFeeUnit();
        String feeStr = StringUtils.isEmpty(fee) ? "" : "超出后每" + TimeUtil.getUnitString(feeUnit) + "收费" + fee + "元。";

        // 每天最高收费
        Long maxFeeUnit = feeStrategy.getMaxFeeUnit();
        Long maxFeeTime = feeStrategy.getMaxFeeTime();
        BigDecimal maxFee = feeStrategy.getMaxFee();
        String maxFeeUnitStr = TimeUtil.getUnitString(maxFeeUnit);
        String maxFeeStr = StringUtils.isEmpty(maxFee) ? "" : "每" + maxFeeTime + maxFeeUnitStr + "最高收费" + maxFee + "元。";
        return fixedStr + feeStr + maxFeeStr;
    }

    /**
     * 根据收费策略计算已花费租金
     *
     * @param feeStrategy
     * @param duration
     * @return
     */
    public BigDecimal calUseFee(FeeStrategy feeStrategy, Long duration) {
        //将计算出来的费用初始化为0
        BigDecimal useFee = BigDecimal.ZERO;
        //计算免费时长
        Long fixedTime = feeStrategy.getFixedTime() * feeStrategy.getFixedUnit();
        //计算意外借出时长
        Long freeTime = feeStrategy.getFreeTime() * feeStrategy.getFreeUnit();
        //计算最长收费时间
        Long maxFeeTime = feeStrategy.getMaxFeeTime() * feeStrategy.getMaxFeeUnit();
        if (duration < freeTime) {
            // 如果租借时长小于意外借出时间，则不计费用
            return useFee;
        } else if (duration > maxFeeTime) {
            // 租借时长大于最高收费时长，按最高收费
            //一天的秒数
            Long daySeconds = Long.valueOf(24 * 60 * 60);
            // 计算借出总天数
            Integer days = Math.toIntExact(duration / daySeconds);
            //如果借用时长没有超过一天，按最高费用返回
            if (days.equals(0)) {
                useFee = feeStrategy.getMaxFee();
            } else {
                //计算最后不足一天的费用，一次递归调用本函数，因此传递回去的应该是最后不足一天的时间
                Long todaySeconds = duration - daySeconds * days;
                BigDecimal todayUsedFee = this.calUseFee(feeStrategy, todaySeconds);
                //最终费用是使用天数*最高费用+最后不足一天的使用费用
                useFee = feeStrategy.getMaxFee().multiply(BigDecimal.valueOf(days)).add(todayUsedFee);
            }
        } else {
            // 计算收费时长
            Long expirTime = duration - fixedTime;
            //如果收费时长小于0，那么费用为固定收费，0元
            if (expirTime < 0) {
                useFee = feeStrategy.getFixed();
            } else {
                useFee = feeStrategy.getFixed().
                        add(feeStrategy.getFee().multiply(BigDecimal.valueOf(1L + (expirTime / feeStrategy.getFeeUnit()))));
                BigDecimal maxFee = feeStrategy.getMaxFee();
                if (useFee.compareTo(maxFee) > 0) {
                    return maxFee;
                }
            }
        }
        return useFee;
    }

    /**
     * 获取免费时长
     */
    public String getFreeTime(FeeStrategy feeStrategy) {
        //固定收费
        Long fixedTime = feeStrategy.getFixedTime();
        Long fixedUnit = feeStrategy.getFixedUnit();
        BigDecimal fixed = feeStrategy.getFixed();
        if (fixed.compareTo(BigDecimal.ZERO) == 0) {
            return fixedTime + TimeUtil.getUnitString(fixedUnit);
        } else {
            return "没有免费时长";
        }
    }

    /**
     * @param feeSettings
     * @return
     */
    public FeeStrategy getFeeStrategy(Long feeSettings) {
        return feeStrategyDao.getFeeStrategy(feeSettings);
    }
}
