package org.flechaamarilla.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.messaging.Message;
import io.smallrye.reactive.messaging.annotations.Blocking;
import org.flechaamarilla.dto.CfdiDTO;
import org.flechaamarilla.dto.ConceptDTO;
import org.flechaamarilla.dto.InvoiceRequestDTO;
import org.flechaamarilla.dto.ProcessedDataDTO;
import org.flechaamarilla.dto.RawDataDTO;
import org.flechaamarilla.model.Issuer;
import org.flechaamarilla.model.Receiver;
import org.flechaamarilla.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.Instant;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Master Data Management service responsible for processing data from various sources,
 * standardizing it, and maintaining the golden record.
 */
@ApplicationScoped
@Slf4j
public class MdmService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Inject
    DataCleanService dataCleanService;

    /**
     * Processes raw data messages from the raw-data topic.
     * This method handles the incoming message, processes it, and produces a processed data message.
     *
     * @param rawDataJson The incoming raw data message payload
     * @return Processed data as JSON string
     */
    @Incoming("raw-data")
    @Outgoing("processed-data")
    @Blocking
    public String processRawData(String rawDataJson) {
        try {
            // Parse the raw data
            RawDataDTO rawData = OBJECT_MAPPER.readValue(rawDataJson, RawDataDTO.class);
            log.info("Processing raw data: {}, tenant: {}, type: {}",
                    rawData.getSource(), rawData.getTenantId(), rawData.getEntityType());

            // Process the data based on entity type
            ProcessedDataDTO processedData = processEntityData(rawData);

            // Convert processed data to JSON and return
            return OBJECT_MAPPER.writeValueAsString(processedData);
        } catch (Exception e) {
            log.error("Error processing raw data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process raw data", e);
        }
    }

    /**
     * Process entity data based on its type (Issuer, Receiver, Product)
     *
     * @param rawData The raw data to process
     * @return The processed data
     */
    @Transactional
    public ProcessedDataDTO processEntityData(RawDataDTO rawData) {
        String entityType = rawData.getEntityType().toUpperCase();
        String tenantId = rawData.getTenantId();
        Map<String, String> cleanedData;
        Long entityId;
        Double qualityScore = 0.75; // Default score

        switch (entityType) {
            case "ISSUER":
                cleanedData = dataCleanService.cleanIssuerData(rawData.getData());
                entityId = importIssuer(tenantId, cleanedData);
                break;

            case "RECEIVER":
                cleanedData = dataCleanService.cleanReceiverData(rawData.getData());
                entityId = importReceiver(tenantId, cleanedData);
                break;

            case "PRODUCT":
                cleanedData = dataCleanService.cleanProductData(rawData.getData());
                entityId = importProduct(tenantId, cleanedData);
                break;

            default:
                throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }

        // Calculate quality score based on data completeness
        qualityScore = calculateQualityScore(cleanedData, entityType);

        // Build the processed data response
        return ProcessedDataDTO.builder()
                .entityType(entityType)
                .tenantId(tenantId)
                .entityId(entityId)
                .data(cleanedData)
                .qualityScore(qualityScore)
                .timestamp(Instant.now().toEpochMilli())
                .batchId(rawData.getBatchId())
                .build();
    }

    /**
     * Import an issuer entity (create or update)
     *
     * @param tenantId The tenant ID
     * @param data Cleaned data for the issuer
     * @return The entity ID
     */
    @Transactional
    public Long importIssuer(String tenantId, Map<String, String> data) {
        log.info("Importing issuer for tenant: {}", tenantId);

        // Check if issuer already exists
        Issuer existingIssuer = Issuer.find("tenantId = ?1 and rfc = ?2",
                tenantId, data.get("rfc")).firstResult();

        if (existingIssuer != null) {
            // Update existing issuer
            existingIssuer.setBusinessName(data.get("businessName"));
            existingIssuer.setTaxRegime(data.get("taxRegime"));
            existingIssuer.setPostalCode(data.get("postalCode"));
            existingIssuer.persist();
            return existingIssuer.id;
        } else {
            // Create new issuer
            Issuer newIssuer = new Issuer();
            newIssuer.setTenantId(tenantId);
            newIssuer.setRfc(data.get("rfc"));
            newIssuer.setBusinessName(data.get("businessName"));
            newIssuer.setTaxRegime(data.get("taxRegime"));
            newIssuer.setPostalCode(data.get("postalCode"));
            newIssuer.persist();
            return newIssuer.id;
        }
    }

    /**
     * Import a receiver entity (create or update)
     *
     * @param tenantId The tenant ID
     * @param data Cleaned data for the receiver
     * @return The entity ID
     */
    @Transactional
    public Long importReceiver(String tenantId, Map<String, String> data) {
        log.info("Importing receiver for tenant: {}", tenantId);

        // Check if receiver already exists
        Receiver existingReceiver = Receiver.find("tenantId = ?1 and rfc = ?2",
                tenantId, data.get("rfc")).firstResult();

        if (existingReceiver != null) {
            // Update existing receiver
            existingReceiver.setBusinessName(data.get("businessName"));
            existingReceiver.setCfdiUsage(data.get("cfdiUsage"));
            existingReceiver.setPostalCode(data.get("postalCode"));
            existingReceiver.setEmail(data.get("email"));
            existingReceiver.persist();
            return existingReceiver.id;
        } else {
            // Create new receiver
            Receiver newReceiver = new Receiver();
            newReceiver.setTenantId(tenantId);
            newReceiver.setRfc(data.get("rfc"));
            newReceiver.setBusinessName(data.get("businessName"));
            newReceiver.setCfdiUsage(data.get("cfdiUsage"));
            newReceiver.setPostalCode(data.get("postalCode"));
            newReceiver.setEmail(data.get("email"));
            newReceiver.persist();
            return newReceiver.id;
        }
    }

    /**
     * Import a product entity (create or update)
     *
     * @param tenantId The tenant ID
     * @param data Cleaned data for the product
     * @return The entity ID
     */
    @Transactional
    public Long importProduct(String tenantId, Map<String, String> data) {
        log.info("Importing product for tenant: {}", tenantId);

        // Check if product already exists by internal code
        String internalCode = data.get("internalCode");
        Product existingProduct = null;

        if (internalCode != null && !internalCode.isEmpty()) {
            existingProduct = Product.find("tenantId = ?1 and internalCode = ?2",
                    tenantId, internalCode).firstResult();
        }

        if (existingProduct != null) {
            // Update existing product
            existingProduct.setDescription(data.get("description"));
            existingProduct.setProdServCode(data.get("prodServCode"));
            existingProduct.setUnit(data.get("unit"));

            if (data.get("unitPrice") != null) {
                try {
                    existingProduct.setUnitPrice(Double.parseDouble(data.get("unitPrice")));
                } catch (NumberFormatException e) {
                    log.warn("Invalid unit price: {}", data.get("unitPrice"));
                }
            }

            existingProduct.persist();
            return existingProduct.id;
        } else {
            // Create new product
            Product newProduct = new Product();
            newProduct.setTenantId(tenantId);
            newProduct.setInternalCode(internalCode);
            newProduct.setProdServCode(data.get("prodServCode"));
            newProduct.setDescription(data.get("description"));
            newProduct.setUnit(data.get("unit"));

            if (data.get("unitPrice") != null) {
                try {
                    newProduct.setUnitPrice(Double.parseDouble(data.get("unitPrice")));
                } catch (NumberFormatException e) {
                    log.warn("Invalid unit price: {}", data.get("unitPrice"));
                }
            }

            newProduct.persist();
            return newProduct.id;
        }
    }

    /**
     * Generate CFDI data from an invoice request
     *
     * @param tenantId The tenant ID
     * @param request The invoice request
     * @return CFDI data
     */
    public CfdiDTO generateCfdi(String tenantId, InvoiceRequestDTO request) {
        log.info("Generating CFDI for customer: {}", request.getCustomerRfc());

        // Get issuer data
        Issuer issuer = Issuer.find("tenantId", tenantId).firstResult();
        if (issuer == null) {
            throw new RuntimeException("No issuer information found for tenant: " + tenantId);
        }

        // Find or create receiver
        Receiver receiver = Receiver.find("tenantId = ?1 and rfc = ?2",
                tenantId, request.getCustomerRfc()).firstResult();

        if (receiver == null) {
            // Create receiver with request data
            Map<String, String> receiverData = new HashMap<>();
            receiverData.put("rfc", request.getCustomerRfc());
            receiverData.put("businessName", request.getName());
            receiverData.put("email", request.getEmail());
            receiverData.put("postalCode", request.getPostalCode());
            receiverData.put("cfdiUsage", request.getCfdiUsage());

            receiverData = dataCleanService.cleanReceiverData(receiverData);
            importReceiver(tenantId, receiverData);

            receiver = Receiver.find("tenantId = ?1 and rfc = ?2",
                    tenantId, request.getCustomerRfc()).firstResult();
        }

        // Find products associated with the ticket token (simulated for hackathon)
        List<Product> products = simulateProductsFromToken(tenantId, request.getTicketToken());

        // Build CFDI
        CfdiDTO cfdi = CfdiDTO.builder()
                .issuerRfc(issuer.getRfc())
                .issuerName(issuer.getBusinessName())
                .receiverRfc(receiver.getRfc())
                .receiverName(receiver.getBusinessName())
                .cfdiUsage(request.getCfdiUsage())
                .paymentForm(request.getPaymentForm())
                .paymentMethod("PUE") // Default: Single Exhibition Payment
                .currency("MXN")
                .series("A")
                .folio(generateFolio())
                .build();

        // Add concepts
        List<ConceptDTO> concepts = new ArrayList<>();
        for (Product product : products) {
            ConceptDTO concept = ConceptDTO.builder()
                    .prodServCode(product.getProdServCode())
                    .description(product.getDescription())
                    .quantity(1) // Default quantity
                    .unit(product.getUnit())
                    .unitPrice(product.getUnitPrice())
                    .amount(product.getUnitPrice()) // amount = quantity * unitPrice
                    .build();
            concepts.add(concept);
        }
        cfdi.setConcepts(concepts);

        return cfdi;
    }

    /**
     * Helper method to generate a folio (simplified for hackathon)
     */
    private String generateFolio() {
        return String.valueOf(10000 + (int)(Math.random() * 90000));
    }

    /**
     * Simulates retrieving products from a ticket token (for hackathon purposes)
     * In a real system, this would query a tickets database
     */
    private List<Product> simulateProductsFromToken(String tenantId, String ticketToken) {
        List<Product> products = new ArrayList<>();

        // Generate different products based on the token
        if (ticketToken.contains("A")) {
            Product p1 = Product.builder()
                    .tenantId(tenantId)
                    .prodServCode("10101501")
                    .description("Consulting service")
                    .unit("Service")
                    .unitPrice(5000.0)
                    .build();
            products.add(p1);
        }

        if (ticketToken.contains("B")) {
            Product p2 = Product.builder()
                    .tenantId(tenantId)
                    .prodServCode("43232408")
                    .description("Software development")
                    .unit("Hour")
                    .unitPrice(1000.0)
                    .build();
            products.add(p2);
        }

        // If no products match the token, add a generic one
        if (products.isEmpty()) {
            Product pDefault = Product.builder()
                    .tenantId(tenantId)
                    .prodServCode("80141607")
                    .description("General service")
                    .unit("N/A")
                    .unitPrice(1500.0)
                    .build();
            products.add(pDefault);
        }

        return products;
    }

    /**
     * Calculate quality score based on data completeness and format validity
     *
     * @param data The cleaned data
     * @param entityType The entity type
     * @return Quality score between 0.0 and 1.0
     */
    private Double calculateQualityScore(Map<String, String> data, String entityType) {
        // Required fields by entity type
        List<String> requiredFields;

        switch (entityType) {
            case "ISSUER":
                requiredFields = List.of("rfc", "businessName", "taxRegime");
                break;

            case "RECEIVER":
                requiredFields = List.of("rfc", "businessName");
                break;

            case "PRODUCT":
                requiredFields = List.of("description");
                break;

            default:
                requiredFields = List.of();
        }

        // Count present required fields
        int presentCount = 0;
        for (String field : requiredFields) {
            if (data.containsKey(field) && data.get(field) != null && !data.get(field).isEmpty()) {
                presentCount++;
            }
        }

        // Basic score based on required fields
        double baseScore = requiredFields.isEmpty() ? 1.0 : (double) presentCount / requiredFields.size();

        // Adjust for special validations (simplified for hackathon)
        // In a real system, this would be more sophisticated
        return Math.min(1.0, baseScore);
    }
}