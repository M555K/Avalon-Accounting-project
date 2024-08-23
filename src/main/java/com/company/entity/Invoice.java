package com.company.entity;

import com.company.entity.common.BaseEntity;
import com.company.enums.InvoiceStatus;
import com.company.enums.InvoiceType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "invoices")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Invoice extends BaseEntity {
    private String invoiceNo;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;
    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;
    private LocalDate date;
    @ManyToOne
    private ClientVendor clientVendor;
    @ManyToOne
    private Company company;

}
