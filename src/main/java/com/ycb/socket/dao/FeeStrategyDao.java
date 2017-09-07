package com.ycb.socket.dao;

import com.ycb.socket.model.FeeStrategy;

/**
 * Created by zhuhui on 17-8-28.
 */
public interface FeeStrategyDao {
    FeeStrategy getFeeStrategy(Long feeSettings);
}
