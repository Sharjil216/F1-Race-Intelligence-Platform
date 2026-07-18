package com.sharjil.f1intel.converter;


import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.mapping.JdbcValue;

import java.sql.JDBCType;
import java.time.Instant;
import java.time.ZoneOffset;

@WritingConverter
public class InstantToOffsetDateTimeConverter implements Converter<Instant, JdbcValue> {
    @Override
    public JdbcValue convert(Instant source) {
        return JdbcValue.of(
                source.atOffset(ZoneOffset.UTC),
                JDBCType.TIMESTAMP_WITH_TIMEZONE
        );
    }
}
