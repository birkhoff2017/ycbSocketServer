package com.ycb.socket.service;

import com.ycb.socket.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created by zhuhui on 17-8-18.
 */
@Service
public class UserService {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserDao stationDao;

    public void updateUserFee(Long userid, BigDecimal deposit, BigDecimal usablemoney) {
        stationDao.updateUserFee(userid, deposit, usablemoney);
    }
}
