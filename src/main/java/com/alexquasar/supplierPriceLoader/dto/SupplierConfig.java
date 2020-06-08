package com.alexquasar.supplierPriceLoader.dto;

import com.alexquasar.supplierPriceLoader.entity.PriceItem;

import java.util.List;

public interface SupplierConfig {

    List<PriceItem> loadPriceItems(String filePath) throws Exception;
}
