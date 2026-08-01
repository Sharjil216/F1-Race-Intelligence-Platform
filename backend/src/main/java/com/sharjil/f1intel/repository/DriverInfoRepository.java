package com.sharjil.f1intel.repository;

import com.sharjil.f1intel.domain.model.DriverInfo;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DriverInfoRepository {

    private final JdbcClient jdbcClient;

    private static final String SQL = "SELECT driver_number, full_name, team_name, team_colour, name_acronym FROM driver WHERE session_key = :sessionKey ORDER BY driver_number";

    public DriverInfoRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<DriverInfo> getDriverInfo(int sessionKey) {
        return jdbcClient
                .sql(SQL)
                .param("sessionKey", Integer.valueOf(sessionKey))
                .query(DriverInfo.class)
                .list();
    }
}
