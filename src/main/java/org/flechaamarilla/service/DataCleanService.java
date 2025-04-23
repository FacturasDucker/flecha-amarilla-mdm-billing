package org.flechaamarilla.service;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Service responsible for cleaning and standardizing data from various sources
 * before storing it in the MDM database.
 */
@ApplicationScoped
@Slf4j
public class DataCleanService {

    // Validation patterns
    private static final Pattern RFC_PATTERN = Pattern.compile("[A-Z&Ñ]{3,4}\\d{6}[A-Z\\d]{3}");
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("\\d{5}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Cleans and standardizes issuer data
     *
     * @param originalData Original data as key-value pairs
     * @return Cleaned and standardized data
     */
    public Map<String, String> cleanIssuerData(Map<String, String> originalData) {
        Map<String, String> cleanData = new HashMap<>();

        // RFC - convert to uppercase and validate format
        String rfc = getValue(originalData, "rfc", "RFC", "tax_id");
        if (rfc != null) {
            rfc = rfc.toUpperCase().trim();
            if (RFC_PATTERN.matcher(rfc).matches()) {
                cleanData.put("rfc", rfc);
            } else {
                log.warn("Invalid RFC format: {}", rfc);
                cleanData.put("rfc", rfc); // We store it even if invalid
            }
        }

        // Business name - normalize spaces
        String businessName = getValue(originalData, "businessName", "business_name", "razon_social", "name", "nombre");
        if (businessName != null) {
            cleanData.put("businessName", normalizeText(businessName));
        }

        // Tax regime - normalize to SAT catalog
        String taxRegime = getValue(originalData, "taxRegime", "tax_regime", "regimen_fiscal", "regimen");
        if (taxRegime != null) {
            cleanData.put("taxRegime", normalizeTaxRegime(taxRegime));
        }

        // Postal code - validate format
        String postalCode = getValue(originalData, "postalCode", "postal_code", "codigo_postal", "cp");
        if (postalCode != null) {
            postalCode = postalCode.trim();
            if (POSTAL_CODE_PATTERN.matcher(postalCode).matches()) {
                cleanData.put("postalCode", postalCode);
            } else {
                log.warn("Invalid postal code format: {}", postalCode);
                cleanData.put("postalCode", postalCode); // We store it even if invalid
            }
        }

        return cleanData;
    }

    /**
     * Cleans and standardizes receiver data
     *
     * @param originalData Original data as key-value pairs
     * @return Cleaned and standardized data
     */
    public Map<String, String> cleanReceiverData(Map<String, String> originalData) {
        Map<String, String> cleanData = new HashMap<>();

        // RFC - convert to uppercase and validate format
        String rfc = getValue(originalData, "rfc", "RFC", "tax_id");
        if (rfc != null) {
            rfc = rfc.toUpperCase().trim();
            if (RFC_PATTERN.matcher(rfc).matches()) {
                cleanData.put("rfc", rfc);
            } else {
                log.warn("Invalid RFC format: {}", rfc);
                cleanData.put("rfc", rfc); // We store it even if invalid
            }
        }

        // Business name - normalize spaces
        String businessName = getValue(originalData, "businessName", "business_name", "razon_social", "name", "nombre");
        if (businessName != null) {
            cleanData.put("businessName", normalizeText(businessName));
        }

        // CFDI usage - normalize to SAT catalog
        String cfdiUsage = getValue(originalData, "cfdiUsage", "cfdi_usage", "uso_cfdi", "uso");
        if (cfdiUsage != null) {
            cleanData.put("cfdiUsage", normalizeCfdiUsage(cfdiUsage));
        }

        // Postal code - validate format
        String postalCode = getValue(originalData, "postalCode", "postal_code", "codigo_postal", "cp");
        if (postalCode != null) {
            postalCode = postalCode.trim();
            if (POSTAL_CODE_PATTERN.matcher(postalCode).matches()) {
                cleanData.put("postalCode", postalCode);
            } else {
                log.warn("Invalid postal code format: {}", postalCode);
                cleanData.put("postalCode", postalCode); // We store it even if invalid
            }
        }

        // Email - validate format
        String email = getValue(originalData, "email", "correo", "mail");
        if (email != null) {
            email = email.trim().toLowerCase();
            if (EMAIL_PATTERN.matcher(email).matches()) {
                cleanData.put("email", email);
            } else {
                log.warn("Invalid email format: {}", email);
                cleanData.put("email", email); // We store it even if invalid
            }
        }

        return cleanData;
    }

    /**
     * Cleans and standardizes product data
     *
     * @param originalData Original data as key-value pairs
     * @return Cleaned and standardized data
     */
    public Map<String, String> cleanProductData(Map<String, String> originalData) {
        Map<String, String> cleanData = new HashMap<>();

        // Product/Service code - normalize to 8 digits
        String prodServCode = getValue(originalData, "prodServCode", "prod_serv_code", "clave_prod_serv", "code");
        if (prodServCode != null) {
            cleanData.put("prodServCode", normalizeProdServCode(prodServCode));
        }

        // Internal code
        String internalCode = getValue(originalData, "internalCode", "internal_code", "codigo_interno");
        if (internalCode != null) {
            cleanData.put("internalCode", internalCode.trim());
        }

        // Description - normalize text
        String description = getValue(originalData, "description", "descripcion");
        if (description != null) {
            cleanData.put("description", normalizeText(description));
        }

        // Unit - normalize text
        String unit = getValue(originalData, "unit", "unidad");
        if (unit != null) {
            cleanData.put("unit", unit.trim());
        }

        // Unit price - parse and validate
        String unitPrice = getValue(originalData, "unitPrice", "unit_price", "precio_unitario", "precio");
        if (unitPrice != null) {
            try {
                // Remove currency symbols and normalize decimal separator
                unitPrice = unitPrice.replaceAll("[^0-9.,]", "").replace(",", ".");
                Double price = Double.parseDouble(unitPrice);
                cleanData.put("unitPrice", price.toString());
            } catch (NumberFormatException e) {
                log.warn("Invalid unit price format: {}", unitPrice);
                cleanData.put("unitPrice", unitPrice); // We store it even if invalid
            }
        }

        return cleanData;
    }

    /**
     * Helper method to search for a value in a map by multiple possible keys
     */
    private String getValue(Map<String, String> map, String... possibleKeys) {
        for (String key : possibleKeys) {
            if (map.containsKey(key) && map.get(key) != null && !map.get(key).trim().isEmpty()) {
                return map.get(key);
            }
        }
        return null;
    }

    /**
     * Normalizes text by removing excess whitespace
     */
    private String normalizeText(String text) {
        if (text == null) return null;

        // Remove multiple spaces and trim
        return text.replaceAll("\\s+", " ").trim();
    }

    /**
     * Normalizes tax regime to SAT catalog codes
     */
    private String normalizeTaxRegime(String regime) {
        if (regime == null) return null;

        // Clean spaces and convert to uppercase
        regime = regime.trim().toUpperCase();

        // Extract numeric code if exists
        if (regime.matches(".*\\d{3}.*")) {
            // Extract 3-digit code
            return regime.replaceAll(".*?(\\d{3}).*", "$1");
        }

        // Basic mapping for common tax regimes (simplified for hackathon)
        Map<String, String> regimeMapping = new HashMap<>();
        regimeMapping.put("GENERAL", "601");
        regimeMapping.put("PERSONAS MORALES", "601");
        regimeMapping.put("PERSONAS FISICAS", "612");
        regimeMapping.put("SIMPLIFICADO", "621");
        regimeMapping.put("RIF", "626");

        // Find match
        for (Map.Entry<String, String> entry : regimeMapping.entrySet()) {
            if (regime.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // If no match, return original
        return regime;
    }

    /**
     * Normalizes CFDI usage to SAT catalog codes
     */
    private String normalizeCfdiUsage(String usage) {
        if (usage == null) return null;

        // Clean spaces and convert to uppercase
        usage = usage.trim().toUpperCase();

        // If already in G_XX format, return it
        if (usage.matches("G_\\d{2}")) {
            return usage;
        }

        // Basic mapping for common usages
        Map<String, String> usageMapping = new HashMap<>();
        usageMapping.put("GASTOS GENERAL", "G_03");
        usageMapping.put("GENERAL", "G_03");
        usageMapping.put("ADQUISICION", "G_01");
        usageMapping.put("COMPRA", "G_01");
        usageMapping.put("HONORARIOS", "G_03");
        usageMapping.put("NOMINA", "CN_01");
        usageMapping.put("EDUCACION", "G_03");

        // Find match
        for (Map.Entry<String, String> entry : usageMapping.entrySet()) {
            if (usage.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // If no match, return G_03 as default
        return "G_03";
    }

    /**
     * Normalizes product/service code to SAT catalog format
     */
    private String normalizeProdServCode(String code) {
        if (code == null) return null;

        // Clean spaces and special characters
        code = code.replaceAll("[^0-9]", "").trim();

        // If it's not 8 digits, try to pad to 8 digits
        if (code.length() < 8) {
            return String.format("%08d", Integer.parseInt(code));
        } else if (code.length() > 8) {
            return code.substring(0, 8);
        }

        return code;
    }
}