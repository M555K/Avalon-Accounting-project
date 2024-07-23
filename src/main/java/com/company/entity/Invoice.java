package com.company.entity;

import com.company.entity.common.BaseEntity;
import com.company.enums.InvoiceStatus;
import com.company.enums.InvoiceType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "companies")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class Invoice extends BaseEntity {
    private String invoiceNo;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;
    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;
    private LocalDate date;
    @JoinColumn(name = "client_vendor_id")
    @ManyToOne
    private ClientVendor clientVendor;
    @JoinColumn(name = "company_id")
    @ManyToOne
    private Company company;

}
