package com.alexquasar.supplierPriceLoader.dto.supplier;

import com.alexquasar.supplierPriceLoader.entity.PriceItem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SupplierDeliverOnTime implements SupplierConfig {

    public static final String NAME = "deliverOnTime@mail.ru";
    private final String regex = "[^\\da-zA-Zа-яёА-ЯЁ0-9]";

    @Override
    public List<PriceItem> loadPriceItems(String filePath) throws Exception {
        List<PriceItem> priceItems = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line = null;
            Scanner scanner = null;
            int index = 0;

            reader.readLine(); // skip first line
            while ((line = reader.readLine()) != null) {
                PriceItem priceItem = new PriceItem();
                scanner = new Scanner(line);
                scanner.useDelimiter(";");
                while (scanner.hasNext()) {
                    String data = scanner.next();
                    if (index == 1) {
                        priceItem.setVendor(data);
                        priceItem.setSearchVendor(deleteExtraCharacters(data));
                    } else if (index == 3) {
                        data = data.length() > 512 ? data.substring(0, 511) : data;
                        priceItem.setDescription(data);
                    } else if (index == 6) {
                        priceItem.setPrice(Double.valueOf(data.replace(",", ".")));
                    } else if (index == 8) {
                        priceItem.setCount(getCount(data));
                    } else if (index == 10) {
                        priceItem.setNumber(data);
                        priceItem.setSearchNumber(deleteExtraCharacters(data));
                    }
                    index++;
                }
                index = 0;

                priceItems.add(priceItem);
            }
        }

        return priceItems;
    }

    private String deleteExtraCharacters(String str) {
        return str.replaceAll(regex, "").toUpperCase();
    }

    private Integer getCount(String stringCount) {
        int lastCharIndex = stringCount.length();
        if (stringCount.matches("<(.*)||>(.*)")) {
            stringCount = stringCount.substring(1, lastCharIndex);
        } else if (stringCount.matches("(.*)-(.*)")) {
            char[] chars = stringCount.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char ch = chars[i];
                if (ch == '-') {
                    stringCount = stringCount.substring(i + 1, lastCharIndex);
                }
            }
        }

        return Integer.valueOf(stringCount);
    }
}
