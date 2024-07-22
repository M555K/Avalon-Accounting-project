package com.company.entity;

import com.company.entity.common.BaseEntity;
import com.company.enums.ClientVendorType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "clients_vendors")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientVendor extends BaseEntity {
    private String clientVendorName;
    @Enumerated(EnumType.STRING)
    private ClientVendorType clientVendorType;
    private String phone;
    private String website;
    @ManyToOne
    private Address address;
    @ManyToOne// ask if One vendor to one address
    private Company company;

}
