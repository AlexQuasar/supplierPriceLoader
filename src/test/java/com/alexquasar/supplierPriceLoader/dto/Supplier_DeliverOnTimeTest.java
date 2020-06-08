package com.alexquasar.supplierPriceLoader.dto;

import com.alexquasar.supplierPriceLoader.entity.PriceItem;
import lombok.SneakyThrows;
import org.junit.Test;

import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Supplier_DeliverOnTimeTest {

    private final String resourceFile = "loadFile.csv";
    private Supplier_DeliverOnTime supplierDeliverOnTime = new Supplier_DeliverOnTime();

    @Test
    @SneakyThrows
    public void loadPriceItemsTestCount() {
        String filePath = getFilePath();
        assertNotEquals("", filePath);

        List<PriceItem> priceItems = supplierDeliverOnTime.loadPriceItems(filePath);

        assertEquals(10, priceItems.size());
        assertEquals(5, priceItems.get(0).getCount().intValue());
        assertEquals(6, priceItems.get(1).getCount().intValue());
        assertEquals(7, priceItems.get(2).getCount().intValue());
    }

    @Test
    @SneakyThrows
    public void loadPriceItemsTestAllColumns() {
        String filePath = getFilePath();
        assertNotEquals("", filePath);

        List<PriceItem> priceItems = supplierDeliverOnTime.loadPriceItems(filePath);

        assertNotEquals(0, priceItems.size());
        for (PriceItem priceItem : priceItems) {
            assertNotEquals("", priceItem.getVendor());
            assertNotEquals("", priceItem.getNumber());
            assertNotEquals("", priceItem.getSearchVendor());
            assertNotEquals("", priceItem.getSearchNumber());
            assertNotEquals("", priceItem.getDescription());
            assertNotEquals(0, priceItem.getPrice());
            assertNotEquals(0, priceItem.getCount().intValue());
        }
    }

    private String getFilePath() {
        URL schema = Supplier_DeliverOnTime.class.getClassLoader().getResource(resourceFile);
        if (schema != null) {
            return schema.getPath().replace("%20", " ");
        }
        return "";
    }
}