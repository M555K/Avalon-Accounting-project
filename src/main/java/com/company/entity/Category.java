package com.company.entity;

import com.company.entity.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
@JsonIgnoreProperties(ignoreUnknown = true)

public class Category extends BaseEntity {
    private String description;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

}
