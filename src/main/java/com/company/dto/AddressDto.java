package com.company.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
public class AddressDto {
    private Long id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String country;
    private String state;
    private String zipCode;



}
