package com.ycb.socket.dao.impl;

import com.ycb.socket.dao.StationDao;
import com.zipeiyi.xpower.dao.DaoFactory;
import com.zipeiyi.xpower.dao.DefaultOpUniq;
import com.zipeiyi.xpower.dao.IDao;
import com.zipeiyi.xpower.dao.OpInsert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Created by zhuhui on 17-8-3.
 */
@Repository
public class StationDaoImpl implements StationDao {
    final IDao dao = DaoFactory.getIDao();
    final String bizName = "ycb";
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void createStationByMac(String mac) {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ycb_mcs_station(mac)")
                .append("VALUES(?)");
        dao.insert(new OpInsert<Integer>(sql, bizName, Integer.class).addParams(mac));
    }

    @Override
    public Long getStationIdByMac(String mac) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT id FROM ycb_mcs_station ")
                .append("WHERE ")
                .append("mac = ? ");
        return dao.queryUniq(new DefaultOpUniq<Long>(sql, bizName).setMapper((resultSet, i) -> resultSet.getLong("id")).addParams(mac));
    }
}
