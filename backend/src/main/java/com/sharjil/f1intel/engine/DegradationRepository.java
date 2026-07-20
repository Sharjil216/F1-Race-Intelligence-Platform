package com.sharjil.f1intel.engine;

import com.sharjil.f1intel.engine.model.DegradationResult;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DegradationRepository {

    private final JdbcClient jdbcClient;

    private static final String SQL = """
            WITH stint_laps AS (
                SELECT s.driver_number,
                       s.stint_number,
                       s.compound,
                       l.lap_duration,
                       s.tyre_age_at_start + (l.lap_number - s.lap_start) AS tyre_age,
                       l.lap_duration + 0.035 * l.lap_number              AS fuel_corrected
                FROM stint s
                JOIN lap l
                  ON  l.session_key   = s.session_key
                  AND l.driver_number = s.driver_number
                  AND l.lap_number > s.lap_start
                  AND l.lap_number < s.lap_end
                WHERE s.session_key = :sessionKey
                  AND l.lap_duration IS NOT NULL
            ),
            stint_stats AS (
                SELECT driver_number,
                       stint_number,
                       AVG(fuel_corrected)                                       AS stint_avg,
                       PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY lap_duration) AS stint_median
                FROM stint_laps
                GROUP BY driver_number, stint_number
            )
            SELECT sl.compound,
                   COUNT(*)                                                                     AS laps_counted,
                   ROUND(regr_slope(sl.fuel_corrected - ss.stint_avg, sl.tyre_age)::numeric, 4) AS slope,
                   ROUND(regr_r2(sl.fuel_corrected - ss.stint_avg, sl.tyre_age)::numeric, 4)    AS r2
            FROM stint_laps sl
            JOIN stint_stats ss
              ON  ss.driver_number = sl.driver_number
              AND ss.stint_number  = sl.stint_number
            WHERE sl.tyre_age BETWEEN 3 AND 15
              AND sl.lap_duration <= 1.07 * ss.stint_median
            GROUP BY sl.compound
            ORDER BY sl.compound;
            """;

    public DegradationRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<DegradationResult> degradationByCompound(int sessionKey) {
        return jdbcClient.sql(SQL)
                .param("sessionKey", sessionKey)
                .query(DegradationResult.class)
                .list();
    }
}
