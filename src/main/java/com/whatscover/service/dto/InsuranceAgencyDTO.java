package com.whatscover.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the InsuranceAgency entity.
 */
public class InsuranceAgencyDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String code;

    @Size(max = 200)
    private String name;

    @Size(max = 200)
    private String address_1;

    @Size(max = 200)
    private String address_2;

    @Size(max = 200)
    private String address_3;

    private Long insuranceCompanyId;

    @Size(max = 100)
    private String insuranceCompanyName;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress_1() {
        return address_1;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    public String getAddress_3() {
        return address_3;
    }

    public void setAddress_3(String address_3) {
        this.address_3 = address_3;
    }

    public Long getInsuranceCompanyId() {
        return insuranceCompanyId;
    }

    public void setInsuranceCompanyId(Long insuranceCompanyId) {
        this.insuranceCompanyId = insuranceCompanyId;
    }
    
    public String getInsuranceCompanyName() {
        return insuranceCompanyName;
    }

    public void setInsuranceCompanyName(String insuranceCompanyName) {
        this.insuranceCompanyName = insuranceCompanyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InsuranceAgencyDTO insuranceAgencyDTO = (InsuranceAgencyDTO) o;
        if(insuranceAgencyDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), insuranceAgencyDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "InsuranceAgencyDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", address_1='" + getAddress_1() + "'" +
            ", address_2='" + getAddress_2() + "'" +
            ", address_3='" + getAddress_3() + "'" +
            "}";
    }
}
