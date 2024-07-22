package com.company.dto;

import com.company.enums.ClientVendorType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientVendorDto {
    private Long id;
    private String clientVendorName;
    private ClientVendorType clientVendorType;
    private String phone;
    private String website;
    private AddressDto address;
    private CompanyDto company;

}
