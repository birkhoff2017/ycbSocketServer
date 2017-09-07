package com.ycb.socket.dao;

import com.ycb.socket.model.Message;

import java.math.BigDecimal;

/**
 * Created by zhuhui on 17-8-21.
 */
public interface UserDao {
    void updateUserFee(Long userid, BigDecimal deposit, BigDecimal usablemoney);

    Message getUserLastMessage(String openid);

    void deleteMessageById(Long messageid);

    void updateMessageNumberById(Long messageid);

    String getOpenidById(Long customerid);
}
