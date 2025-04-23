package org.flechaamarilla.config;

import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

/**
 * Configuration for Jackson JSON processing
 */
@ApplicationScoped
public class JacksonConfig {

    /**
     * Configure ObjectMapper with custom settings
     *
     * @return Configured ObjectMapper
     */
    @Produces
    @Singleton
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Prevent default datetime serialization as timestamps
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Set up indentation for pretty printing
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Configure higher nesting level limit (if needed)
        // Default is 1000, we can increase it but it's better to fix the circular reference
        StreamWriteConstraints constraints = StreamWriteConstraints.builder()
                .maxNestingDepth(1500)  // Increased from default 1000
                .build();
        objectMapper.getFactory().setStreamWriteConstraints(constraints);

        return objectMapper;
    }
}