-- Initial test data for MDM Billing Hackathon

-- Test issuer for tenant 'tenant1'
INSERT INTO issuers(tenant_id, rfc, business_name, tax_regime, postal_code)
VALUES ('tenant1', 'DEMO010101AAA', 'Demo Company S.A.', '601', '01000');

-- Test receiver for tenant 'tenant1'
INSERT INTO receivers(tenant_id, rfc, business_name, cfdi_usage, postal_code, email)
VALUES ('tenant1', 'CUST010101BBB', 'Customer Name', 'G_03', '06500', 'customer@example.com');

-- Test product for tenant 'tenant1'
INSERT INTO products(tenant_id, prod_serv_code, internal_code, description, unit, unit_price)
VALUES ('tenant1', '10101501', 'PROD001', 'Test Product', 'Unit', 150.00);