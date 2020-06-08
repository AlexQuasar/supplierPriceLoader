package com.alexquasar.supplierPriceLoader.dto.supplier;

import org.springframework.stereotype.Service;

@Service
public class SupplierIdentifier {

    public SupplierConfig getSupplierConfig(String supplierName) {
        SupplierConfig supplier = null;

        if (SupplierDeliverOnTime.NAME.equals(supplierName)) {
            supplier = new SupplierDeliverOnTime();
        }

        return supplier;
    }
}
