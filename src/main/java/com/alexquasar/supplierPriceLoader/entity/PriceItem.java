package com.alexquasar.supplierPriceLoader.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "price_items")
@Getter
@Setter
public class PriceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vendor;
    private String number;

    @Column(name = "search_vendor")
    private String searchVendor;

    @Column(name = "search_number")
    private String searchNumber;

    private String description;
    private Double price;
    private Integer count;
}
