package org.flechaamarilla.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing a Product or Service to be invoiced
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Product extends PanacheEntity {

    /**
     * Tenant ID for multi-tenancy support
     */
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    /**
     * SAT product/service code
     */
    @Column(name = "prod_serv_code")
    private String prodServCode;

    /**
     * Internal product code
     */
    @Column(name = "internal_code")
    private String internalCode;

    /**
     * Product or service description
     */
    @Column(nullable = false)
    private String description;

    /**
     * Unit of measure
     */
    @Column
    private String unit;

    /**
     * Unit price
     */
    @Column(name = "unit_price")
    private Double unitPrice;

    /**
     * Static builder method as an alternative to SuperBuilder
     */
    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    /**
     * Manual builder class as an alternative to Lombok's SuperBuilder
     */
    public static class ProductBuilder {
        private final Product product = new Product();

        public ProductBuilder tenantId(String tenantId) {
            product.setTenantId(tenantId);
            return this;
        }

        public ProductBuilder prodServCode(String prodServCode) {
            product.setProdServCode(prodServCode);
            return this;
        }

        public ProductBuilder internalCode(String internalCode) {
            product.setInternalCode(internalCode);
            return this;
        }

        public ProductBuilder description(String description) {
            product.setDescription(description);
            return this;
        }

        public ProductBuilder unit(String unit) {
            product.setUnit(unit);
            return this;
        }

        public ProductBuilder unitPrice(Double unitPrice) {
            product.setUnitPrice(unitPrice);
            return this;
        }

        public Product build() {
            return product;
        }
    }
}