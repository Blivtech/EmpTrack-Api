package com.emptrack.api.dto;

import com.emptrack.api.model.TblCompany;
import com.emptrack.api.model.TblShift;
import lombok.Data;
import java.util.List;

@Data
public class CompanyResponse {
    private Long         id;
    private String       btCode;
    private String       companyCode;
    private String       name;
    private String       address;
    private String       city;
    private String       state;
    private String       phone;
    private String       email;
    private String       logo;
    private Integer      status;
    private List<TblShift> shifts;

    public CompanyResponse(TblCompany company, List<TblShift> shifts) {
        this.id          = company.getId();
        this.btCode      = company.getBtCode();
        this.companyCode = company.getCompanyCode();
        this.name        = company.getName();
        this.address     = company.getAddress();
        this.city        = company.getCity();
        this.state       = company.getState();
        this.phone       = company.getPhone();
        this.email       = company.getEmail();
        this.logo        = company.getLogo();
        this.status      = company.getStatus();
        this.shifts      = shifts;
    }
}