package com.ycb.socket.dao.impl;

import com.ycb.socket.dao.UserDao;
import com.ycb.socket.model.Message;
import com.zipeiyi.xpower.dao.DaoFactory;
import com.zipeiyi.xpower.dao.IDao;
import com.zipeiyi.xpower.dao.OpResult;
import com.zipeiyi.xpower.dao.OpUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * Created by zhuhui on 17-8-21.
 */
@Repository
public class UserDaoImpl implements UserDao {

    final IDao dao = DaoFactory.getIDao();
    final String bizName = "ycb";
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void updateUserFee(Long userid, BigDecimal deposit, BigDecimal usablemoney) {
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ycb_mcs_user ")
                .append("SET optlock = optlock + 1, ")
                .append("lastModifiedBy = 'SYS:return_back', ")
                .append("lastModifiedDate = NOW(), ")
                .append("deposit = deposit - ?, ")
                .append("usablemoney = usablemoney + ? ")
                .append("WHERE id = ? ");
        dao.update(new OpUpdate(sql, bizName).addParams(
                deposit,
                usablemoney,
                userid
        ));
    }

    @Override
    public Message getUserLastMessage(String openid) {
        StringBuffer selectSql = new StringBuffer();
        selectSql.append("SELECT * ")
                .append("FROM ycb_mcs_message m ")
                .append("WHERE ")
                .append("m.openid = ? ")
                .append("AND m.form_prepay_id <> 'the formId is a mock one' ")
                .append("ORDER BY m.createdDate ")
                .append("LIMIT 1 ");
        return dao.queryResult(OpResult.create(selectSql, bizName, rs -> {
            Message message = null;
            while (rs.next()) {
                message = new Message();
                message.setId(rs.getLong("id"));
                message.setOpenid(rs.getString("openid"));
                message.setOrderid(rs.getString("orderid"));
                message.setFormId(rs.getString("form_prepay_id"));
                message.setNumber(rs.getInt("number"));
                message.setType(rs.getInt("type"));
            }
            return message;
        }).addParams(openid));
    }

    @Override
    public String getOpenidById(Long customerid) {
        StringBuffer selectSql = new StringBuffer();
        selectSql.append("SELECT openid ")
                .append("FROM ycb_mcs_user ")
                .append("WHERE ")
                .append("id = ? ");
        return dao.queryResult(OpResult.create(selectSql, bizName, rs -> {
            String openid = null;
            while (rs.next()) {
                openid = rs.getString("openid");
            }
            return openid;
        }).addParams(customerid));
    }

    @Override
    public void deleteMessageById(Long messageid) {
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM ycb_mcs_message ")
                .append("WHERE ")
                .append("(number <= 1 AND id = ?) ")
                .append("OR date_add(createdDate, interval 7 day) < NOW() ")
                .append("OR form_prepay_id = 'the formId is a mock one' ");
        dao.update(new OpUpdate(sql, bizName).addParams(messageid));
    }

    @Override
    public void updateMessageNumberById(Long messageid) {
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE ycb_mcs_message ")
                .append("SET optlock = optlock + 1, ")
                .append("lastModifiedBy = 'SYS:socket', ")
                .append("lastModifiedDate = NOW(), ")
                .append("number = number - 1 ")
                .append("WHERE id = ? ");
        dao.update(new OpUpdate(sql, bizName).addParams(messageid));
    }
}
