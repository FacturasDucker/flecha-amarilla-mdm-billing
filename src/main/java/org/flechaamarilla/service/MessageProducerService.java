package org.flechaamarilla.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.flechaamarilla.dto.RawDataDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service responsible for producing messages to the raw-data topic.
 * This service is used by the API to send data to the MDM processing pipeline.
 */
@ApplicationScoped
@Slf4j
public class MessageProducerService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Inject
    @Channel("raw-data-api")
    Emitter<String> rawDataEmitter;

    /**
     * Sends issuer data to the raw-data topic for processing
     *
     * @param tenantId The tenant ID
     * @param issuerData The issuer data to process
     * @return The batch ID assigned to this message
     */
    public String sendIssuerData(String tenantId, Map<String, String> issuerData) {
        String batchId = UUID.randomUUID().toString();
        log.info("Sending issuer data to raw-data topic. Tenant: {}, Batch ID: {}", tenantId, batchId);

        RawDataDTO rawData = RawDataDTO.builder()
                .entityType("ISSUER")
                .tenantId(tenantId)
                .source("api-call")
                .data(new HashMap<>(issuerData))
                .batchId(batchId)
                .build();

        try {
            String rawDataJson = OBJECT_MAPPER.writeValueAsString(rawData);
            rawDataEmitter.send(rawDataJson);
            return batchId;
        } catch (Exception e) {
            log.error("Error sending issuer data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send issuer data to processing queue", e);
        }
    }

    /**
     * Sends receiver data to the raw-data topic for processing
     *
     * @param tenantId The tenant ID
     * @param receiverData The receiver data to process
     * @return The batch ID assigned to this message
     */
    public String sendReceiverData(String tenantId, Map<String, String> receiverData) {
        String batchId = UUID.randomUUID().toString();
        log.info("Sending receiver data to raw-data topic. Tenant: {}, Batch ID: {}", tenantId, batchId);

        RawDataDTO rawData = RawDataDTO.builder()
                .entityType("RECEIVER")
                .tenantId(tenantId)
                .source("api-call")
                .data(new HashMap<>(receiverData))
                .batchId(batchId)
                .build();

        try {
            String rawDataJson = OBJECT_MAPPER.writeValueAsString(rawData);
            rawDataEmitter.send(rawDataJson);
            return batchId;
        } catch (Exception e) {
            log.error("Error sending receiver data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send receiver data to processing queue", e);
        }
    }

    /**
     * Sends product data to the raw-data topic for processing
     *
     * @param tenantId The tenant ID
     * @param productData The product data to process
     * @return The batch ID assigned to this message
     */
    public String sendProductData(String tenantId, Map<String, String> productData) {
        String batchId = UUID.randomUUID().toString();
        log.info("Sending product data to raw-data topic. Tenant: {}, Batch ID: {}", tenantId, batchId);

        RawDataDTO rawData = RawDataDTO.builder()
                .entityType("PRODUCT")
                .tenantId(tenantId)
                .source("api-call")
                .data(new HashMap<>(productData))
                .batchId(batchId)
                .build();

        try {
            String rawDataJson = OBJECT_MAPPER.writeValueAsString(rawData);
            rawDataEmitter.send(rawDataJson);
            return batchId;
        } catch (Exception e) {
            log.error("Error sending product data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send product data to processing queue", e);
        }
    }

    /**
     * Sends a batch of data to the raw-data topic for processing
     *
     * @param tenantId The tenant ID
     * @param entityType The type of entity (ISSUER, RECEIVER, PRODUCT)
     * @param dataList List of data maps to process
     * @return The batch ID assigned to these messages
     */
    public String sendDataBatch(String tenantId, String entityType,
                                List<Map<String, String>> dataList) {
        String batchId = UUID.randomUUID().toString();
        log.info("Sending batch data to raw-data topic. Tenant: {}, Entity Type: {}, Batch ID: {}, Count: {}",
                tenantId, entityType, batchId, dataList.size());

        try {
            for (Map<String, String> data : dataList) {
                RawDataDTO rawData = RawDataDTO.builder()
                        .entityType(entityType.toUpperCase())
                        .tenantId(tenantId)
                        .source("csv-import")
                        .data(new HashMap<>(data))
                        .batchId(batchId)
                        .build();

                String rawDataJson = OBJECT_MAPPER.writeValueAsString(rawData);
                rawDataEmitter.send(rawDataJson);
            }
            return batchId;
        } catch (Exception e) {
            log.error("Error sending batch data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send batch data to processing queue", e);
        }
    }
}