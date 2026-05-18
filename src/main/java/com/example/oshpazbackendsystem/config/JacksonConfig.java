package com.example.oshpazbackendsystem.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.DayOfWeek;

@Configuration
public class JacksonConfig {

    @Bean
    public SimpleModule dayOfWeekModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(DayOfWeek.class, new DayOfWeekDeserializer());
        return module;
    }

    /**
     * Accepts both ISO numeric values (1=MONDAY … 7=SUNDAY) and name strings ("MONDAY").
     * Fixes the +1 day shift caused by Jackson using 0-based ordinals for integer input.
     */
    static class DayOfWeekDeserializer extends StdDeserializer<DayOfWeek> {

        DayOfWeekDeserializer() {
            super(DayOfWeek.class);
        }

        @Override
        public DayOfWeek deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            String value = p.getText().trim();
            try {
                int isoDay = Integer.parseInt(value);
                return DayOfWeek.of(isoDay); // 1=MONDAY, 7=SUNDAY (ISO 8601)
            } catch (NumberFormatException e) {
                return DayOfWeek.valueOf(value.toUpperCase());
            }
        }
    }
}
