package com.sharjil.f1intel.engine;

import com.sharjil.f1intel.engine.model.StintShape;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StrategyRepository {

    private final JdbcClient jdbcClient;

    private static final String STINT_QUERY = "SELECT stint_number, compound, lap_start, lap_end FROM stint WHERE session_key = :sessionKey AND driver_number = :driverNumber ORDER BY stint_number";

    private static final String TOTAL_LAPS_QUERY = "SELECT MAX(lap_number) FROM lap WHERE session_key = :sessionKey";

    public StrategyRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<StintShape> stintShapes(int sessionKey, int driverNumber) {
        return jdbcClient
                .sql(STINT_QUERY)
                .param("sessionKey", Integer.valueOf(sessionKey))
                .param("driverNumber", Integer.valueOf(driverNumber))
                .query(StintShape.class)
                .list();
    }

    public int totalLaps(int sessionKey) {
        return jdbcClient
                .sql(TOTAL_LAPS_QUERY)
                .param("sessionKey", Integer.valueOf(sessionKey))
                .query(Integer.class).single();
    }

}


