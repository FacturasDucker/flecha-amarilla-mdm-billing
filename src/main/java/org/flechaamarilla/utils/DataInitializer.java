package org.flechaamarilla.utils;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.flechaamarilla.model.BusinessUnit;
import org.flechaamarilla.model.FieldMapping;
import org.flechaamarilla.service.BusinessUnitService;

import java.util.HashMap;
import java.util.Map;

/**
 * Initializes sample data for the MVP
 */
@ApplicationScoped
@Slf4j
public class DataInitializer {

    @Inject
    BusinessUnitService businessUnitService;

    /**
     * Method called on application startup to initialize data
     *
     * @param event Startup event
     */
    @Transactional
    public void onStart(@Observes StartupEvent event) {
        log.info("Initializing sample data for the MVP");

        // Only create data if no business units exist
        if (BusinessUnit.count() == 0) {
            createSampleBusinessUnits();
        }
    }

    /**
     * Creates sample business units with their field mappings
     */
    private void createSampleBusinessUnits() {
        // Business Unit 1 - Standard names
        BusinessUnit bu1 = new BusinessUnit();
        bu1.setName("Business Unit 1");
        bu1.setDescription("Sample business unit with standard field names");
        bu1.setRfcEmitter("FACW951024M98");
        bu1.setEmitterName("Empresa Estándar S.A. de C.V.");
        bu1.setDefaultCurrency("MXN");
        bu1.setSeries("A");
        bu1.persist();

        // Business Unit 2 - Different field names
        BusinessUnit bu2 = new BusinessUnit();
        bu2.setName("Business Unit 2");
        bu2.setDescription("Sample business unit with different field names");
        bu2.setRfcEmitter("XAXX010101000");
        bu2.setEmitterName("Empresa Diferente S.A. de C.V.");
        bu2.setDefaultCurrency("MXN");
        bu2.setSeries("B");
        bu2.persist();

        // Business Unit 3 - Yet another set of field names
        BusinessUnit bu3 = new BusinessUnit();
        bu3.setName("Business Unit 3");
        bu3.setDescription("Sample business unit with another set of field names");
        bu3.setRfcEmitter("FACW951024M98");
        bu3.setEmitterName("Empresa Alternativa S.A. de C.V.");
        bu3.setDefaultCurrency("MXN");
        bu3.setSeries("C");
        bu3.persist();

        // Create field mappings for each business unit
        createFieldMappingsForBu1(bu1);
        createFieldMappingsForBu2(bu2);
        createFieldMappingsForBu3(bu3);

        log.info("Created 3 sample business units with their field mappings");
    }

    /**
     * Creates field mappings for business unit 1 (standard names)
     *
     * @param bu The business unit
     */
    private void createFieldMappingsForBu1(BusinessUnit bu) {
        Map<String, String> mappings = new HashMap<>();
        mappings.put("claveProdServ", "claveProdServ");
        mappings.put("descripcion", "descripcion");
        mappings.put("cantidad", "cantidad");
        mappings.put("unidad", "unidad");
        mappings.put("valorUnitario", "valorUnitario");
        mappings.put("importe", "importe");

        createMappings(mappings, bu);
    }

    /**
     * Creates field mappings for business unit 2 (different names)
     *
     * @param bu The business unit
     */
    private void createFieldMappingsForBu2(BusinessUnit bu) {
        Map<String, String> mappings = new HashMap<>();
        mappings.put("productCode", "claveProdServ");
        mappings.put("productName", "descripcion");
        mappings.put("qty", "cantidad");
        mappings.put("unit", "unidad");
        mappings.put("price", "valorUnitario");
        mappings.put("total", "importe");

        createMappings(mappings, bu);
    }

    /**
     * Creates field mappings for business unit 3 (another set of names)
     *
     * @param bu The business unit
     */
    private void createFieldMappingsForBu3(BusinessUnit bu) {
        Map<String, String> mappings = new HashMap<>();
        mappings.put("claveProducto", "claveProdServ");
        mappings.put("concepto", "descripcion");
        mappings.put("cantidadProducto", "cantidad");
        mappings.put("unidadMedida", "unidad");
        mappings.put("precioUnitario", "valorUnitario");
        mappings.put("precioTotal", "importe");

        createMappings(mappings, bu);
    }

    /**
     * Helper method to create field mappings from a map
     *
     * @param mappings Map of source field to standard field
     * @param bu The business unit
     */
    private void createMappings(Map<String, String> mappings, BusinessUnit bu) {
        for (Map.Entry<String, String> entry : mappings.entrySet()) {
            FieldMapping mapping = new FieldMapping();
            mapping.setSourceFieldName(entry.getKey());
            mapping.setStandardFieldName(entry.getValue());
            mapping.setBusinessUnit(bu);
            mapping.persist();
        }
    }
}