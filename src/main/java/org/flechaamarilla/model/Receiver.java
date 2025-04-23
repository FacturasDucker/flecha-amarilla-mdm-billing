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
 * Entity representing a Receiver (Customer that receives invoices)
 */
@Entity
@Table(name = "receivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Receiver extends PanacheEntity {

    /**
     * Tenant ID for multi-tenancy support
     */
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    /**
     * RFC (Tax ID) of the receiver
     */
    @Column(nullable = false)
    private String rfc;

    /**
     * Business name or full name of the receiver
     */
    @Column(name = "business_name", nullable = false)
    private String businessName;

    /**
     * CFDI usage code according to SAT catalog
     */
    @Column(name = "cfdi_usage")
    private String cfdiUsage;

    /**
     * Postal code of the receiver's address
     */
    @Column(name = "postal_code")
    private String postalCode;

    /**
     * Email address for sending the invoice
     */
    @Column
    private String email;

    /**
     * Static builder method as an alternative to SuperBuilder
     */
    public static ReceiverBuilder builder() {
        return new ReceiverBuilder();
    }

    /**
     * Manual builder class as an alternative to Lombok's SuperBuilder
     */
    public static class ReceiverBuilder {
        private final Receiver receiver = new Receiver();

        public ReceiverBuilder tenantId(String tenantId) {
            receiver.setTenantId(tenantId);
            return this;
        }

        public ReceiverBuilder rfc(String rfc) {
            receiver.setRfc(rfc);
            return this;
        }

        public ReceiverBuilder businessName(String businessName) {
            receiver.setBusinessName(businessName);
            return this;
        }

        public ReceiverBuilder cfdiUsage(String cfdiUsage) {
            receiver.setCfdiUsage(cfdiUsage);
            return this;
        }

        public ReceiverBuilder postalCode(String postalCode) {
            receiver.setPostalCode(postalCode);
            return this;
        }

        public ReceiverBuilder email(String email) {
            receiver.setEmail(email);
            return this;
        }

        public Receiver build() {
            return receiver;
        }
    }
}