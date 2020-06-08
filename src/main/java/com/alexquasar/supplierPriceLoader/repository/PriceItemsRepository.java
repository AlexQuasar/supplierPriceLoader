package com.alexquasar.supplierPriceLoader.repository;

import com.alexquasar.supplierPriceLoader.entity.PriceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceItemsRepository extends JpaRepository<PriceItem, Long> {
}
