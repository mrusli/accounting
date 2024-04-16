package com.pyramix.domain.project;

import java.math.BigDecimal;
import java.util.Date;

import com.pyramix.domain.common.IdBasedObject;
import com.pyramix.domain.common.SchemaUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "project", schema = SchemaUtil.SCHEMA_COMMON)
public class Project extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 321225959035740618L;

	//	`project_name` varchar(255) DEFAULT NULL,
	@Column(name = "project_name")
	private String projectName;
	
	//	`company_name` varchar(255) DEFAULT NULL,
	@Column(name = "company_name")
	private String companyName;
	
	//	`contract_number` varchar(255) DEFAULT NULL,
	@Column(name = "contract_number")
	private String contractNumber;
	
	//	`project_sum` decimal(19,2) DEFAULT NULL,
	@Column(name = "project_sum")
	private BigDecimal projectSum;
	
	//	`project_sum_ppn` decimal(19,2) DEFAULT NULL,
	@Column(name = "project_sum_ppn")
	private BigDecimal projectSumPpn;
	
	//	`project_sum_pph` decimal(19,2) DEFAULT NULL,
	@Column(name = "project_sum_pph")
	private BigDecimal projectSumPph;
	
	//	`project_sum_finance` decimal(19,2) DEFAULT NULL,
	@Column(name = "project_sum_finance")
	private BigDecimal proectSumFinance;
	
	//	`contract_start_date` date DEFAULT NULL,
	@Column(name = "contract_start_date")
	private Date contractStartDate;
	
	//	`contract_end_date` date DEFAULT NULL,
	@Column(name = "contract_end_date")
	private Date contractEndDate;

	//	`active` char(1) DEFAULT NULL,
	@Column(name = "active")
	@Convert(converter = org.hibernate.type.TrueFalseConverter.class)
	private boolean active;
	
	@Override
	public String toString() {
		return "Project [" + super.toString() + ", projectName=" + projectName + ", companyName=" + companyName + ", contractNumber="
				+ contractNumber + ", projectSum=" + projectSum + ", projectSumPpn=" + projectSumPpn
				+ ", projectSumPph=" + projectSumPph + ", proectSumFinance=" + proectSumFinance + ", contractStartDate="
				+ contractStartDate + ", contractEndDate=" + contractEndDate + "]";
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public BigDecimal getProjectSum() {
		return projectSum;
	}

	public void setProjectSum(BigDecimal projectSum) {
		this.projectSum = projectSum;
	}

	public BigDecimal getProjectSumPpn() {
		return projectSumPpn;
	}

	public void setProjectSumPpn(BigDecimal projectSumPpn) {
		this.projectSumPpn = projectSumPpn;
	}

	public BigDecimal getProjectSumPph() {
		return projectSumPph;
	}

	public void setProjectSumPph(BigDecimal projectSumPph) {
		this.projectSumPph = projectSumPph;
	}

	public BigDecimal getProectSumFinance() {
		return proectSumFinance;
	}

	public void setProectSumFinance(BigDecimal proectSumFinance) {
		this.proectSumFinance = proectSumFinance;
	}

	public Date getContractStartDate() {
		return contractStartDate;
	}

	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
	}

	public Date getContractEndDate() {
		return contractEndDate;
	}

	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = contractEndDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	
}
