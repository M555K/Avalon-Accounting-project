package com.company.dto;

import com.company.entity.Address;
import com.company.enums.CompanyStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyDto {
    @JsonIgnore
    private Long id;
    private CompanyStatus companyStatus;
    private String phone;
    private String title;
    private String website;
    private AddressDto address;

}
