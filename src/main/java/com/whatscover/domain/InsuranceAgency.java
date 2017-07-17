package com.whatscover.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A InsuranceAgency.
 */
@Entity
@Table(name = "insurance_agency")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "insuranceagency")
public class InsuranceAgency implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "code", length = 100, nullable = false)
    private String code;

    @Size(max = 200)
    @Column(name = "name", length = 200)
    private String name;

    @Size(max = 200)
    @Column(name = "address_1", length = 200)
    private String address_1;

    @Size(max = 200)
    @Column(name = "address_2", length = 200)
    private String address_2;

    @Size(max = 200)
    @Column(name = "address_3", length = 200)
    private String address_3;

    @ManyToOne
    private InsuranceCompany insuranceCompany;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public InsuranceAgency code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public InsuranceAgency name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress_1() {
        return address_1;
    }

    public InsuranceAgency address_1(String address_1) {
        this.address_1 = address_1;
        return this;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public InsuranceAgency address_2(String address_2) {
        this.address_2 = address_2;
        return this;
    }

    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    public String getAddress_3() {
        return address_3;
    }

    public InsuranceAgency address_3(String address_3) {
        this.address_3 = address_3;
        return this;
    }

    public void setAddress_3(String address_3) {
        this.address_3 = address_3;
    }

    public InsuranceCompany getInsuranceCompany() {
        return insuranceCompany;
    }

    public InsuranceAgency insuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
        return this;
    }

    public void setInsuranceCompany(InsuranceCompany insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InsuranceAgency insuranceAgency = (InsuranceAgency) o;
        if (insuranceAgency.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), insuranceAgency.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "InsuranceAgency{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", address_1='" + getAddress_1() + "'" +
            ", address_2='" + getAddress_2() + "'" +
            ", address_3='" + getAddress_3() + "'" +
            "}";
    }
}