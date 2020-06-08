package com.alexquasar.supplierPriceLoader.dto.supplier;

import com.alexquasar.supplierPriceLoader.entity.PriceItem;

import java.util.List;

public interface SupplierConfig {

    List<PriceItem> loadPriceItems(String filePath) throws Exception;
}
