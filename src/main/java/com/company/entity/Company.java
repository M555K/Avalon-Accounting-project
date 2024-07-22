package com.company.entity;

import com.company.dto.AddressDto;
import com.company.entity.common.BaseEntity;
import com.company.enums.CompanyStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "companies")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class Company extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private CompanyStatus companyStatus;
    private String phone;
    private String title;
    private String website;
    @ManyToOne
    private Address address;
}
