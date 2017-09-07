package com.ycb.socket.dao.impl;

import com.ycb.socket.dao.FeeStrategyDao;
import com.ycb.socket.model.FeeStrategy;
import com.zipeiyi.xpower.dao.DaoFactory;
import com.zipeiyi.xpower.dao.IDao;
import com.zipeiyi.xpower.dao.OpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Created by zhuhui on 17-8-28.
 */
@Repository
public class FeeStrategyDaoImpl implements FeeStrategyDao {
    final IDao dao = DaoFactory.getIDao();
    final String bizName = "ycb";
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public FeeStrategy getFeeStrategy(Long feeSettings) {
        StringBuffer selectSql = new StringBuffer();
        selectSql.append("SELECT ")
                .append("* ")
                .append("FROM ycb_mcs_fee_strategy ")
                .append("WHERE id = ? ");
        return dao.queryResult(OpResult.create(selectSql, bizName, rs -> {
            FeeStrategy feeStrategy = null;
            while (rs.next()) {
                feeStrategy = new FeeStrategy();
                feeStrategy.setFee(rs.getBigDecimal("fee"));
                feeStrategy.setFeeUnit(rs.getLong("fee_unit"));
                feeStrategy.setFixed(rs.getBigDecimal("fixed"));
                feeStrategy.setFixedTime(rs.getLong("fixed_time"));
                feeStrategy.setFixedUnit(rs.getLong("fixed_unit"));
                feeStrategy.setFreeUnit(rs.getLong("free_unit"));
                feeStrategy.setFreeTime(rs.getLong("free_time"));
                feeStrategy.setMaxFee(rs.getBigDecimal("max_fee"));
                feeStrategy.setMaxFeeTime(rs.getLong("max_fee_time"));
                feeStrategy.setMaxFeeUnit(rs.getLong("max_fee_unit"));
                feeStrategy.setName(rs.getString("name"));
            }
            return feeStrategy;
        }).addParams(feeSettings));
    }
}
