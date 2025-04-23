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
 * Entity representing an Issuer (Company that issues invoices)
 */
@Entity
@Table(name = "issuers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Issuer extends PanacheEntity {

    /**
     * Tenant ID for multi-tenancy support
     */
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    /**
     * RFC (Tax ID) of the issuer
     */
    @Column(nullable = false)
    private String rfc;

    /**
     * Business name of the issuer
     */
    @Column(name = "business_name", nullable = false)
    private String businessName;

    /**
     * Tax regime code according to SAT catalog
     */
    @Column(name = "tax_regime")
    private String taxRegime;

    /**
     * Postal code of the issuer's address
     */
    @Column(name = "postal_code")
    private String postalCode;

    /**
     * Static builder method as an alternative to SuperBuilder
     */
    public static IssuerBuilder builder() {
        return new IssuerBuilder();
    }

    /**
     * Manual builder class as an alternative to Lombok's SuperBuilder
     */
    public static class IssuerBuilder {
        private final Issuer issuer = new Issuer();

        public IssuerBuilder tenantId(String tenantId) {
            issuer.setTenantId(tenantId);
            return this;
        }

        public IssuerBuilder rfc(String rfc) {
            issuer.setRfc(rfc);
            return this;
        }

        public IssuerBuilder businessName(String businessName) {
            issuer.setBusinessName(businessName);
            return this;
        }

        public IssuerBuilder taxRegime(String taxRegime) {
            issuer.setTaxRegime(taxRegime);
            return this;
        }

        public IssuerBuilder postalCode(String postalCode) {
            issuer.setPostalCode(postalCode);
            return this;
        }

        public Issuer build() {
            return issuer;
        }
    }
}