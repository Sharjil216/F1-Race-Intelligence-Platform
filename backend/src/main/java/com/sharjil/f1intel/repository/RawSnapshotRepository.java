package com.sharjil.f1intel.repository;

import com.sharjil.f1intel.domain.RawSnapshot;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface RawSnapshotRepository extends Repository<RawSnapshot, Long> {

    @Modifying
    @Query("""
        INSERT INTO raw_snapshot (endpoint, payload, query_params)
        VALUES (:endpoint, CAST(:payload AS jsonb), :queryParams)
        """)
    void insertSnapshot(@Param("endpoint") String endpoint,
                        @Param("payload") String payload,
                        @Param("queryParams") String queryParams);
}
