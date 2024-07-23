package com.company.entity;

import com.company.entity.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "invoices_products")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class InvoiceProduct extends BaseEntity {
    private int quantity;
    private BigDecimal price;
    private int tax;
    private BigDecimal profitLoss;
    private int remainingQuantity;
    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
    @ManyToOne
    @JoinColumn(name= "product_id")
    private Product product;


}
