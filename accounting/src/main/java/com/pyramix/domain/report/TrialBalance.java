package com.pyramix.domain.report;

import java.math.BigDecimal;
import java.util.Date;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.common.IdBasedObject;
import com.pyramix.domain.common.SchemaUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "balance_trial", schema = SchemaUtil.SCHEMA_COMMON)
public class TrialBalance extends IdBasedObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3065168971525950594L;

	@Column(name = "report_end_date")
	private Date reportEndDate;

	@Column(name = "coa_type_name")
	private String coaTypeName;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "coa_id_fk")
	private Coa_05_Master coa_05_Master;
	
	@Column(name = "debit")
	private BigDecimal debit;
	
	@Column(name = "credit")
	private BigDecimal credit;

	@Override
	public String toString() {
		return "TrialBalance [" + super.toString() + ", reportEndDate=" + reportEndDate + ", coaTypeName=" + coaTypeName + ", coa_05_Master="
				+ coa_05_Master + ", debit=" + debit + ", credit=" + credit + "]";
	}

	public Coa_05_Master getCoa_05_Master() {
		return coa_05_Master;
	}

	public void setCoa_05_Master(Coa_05_Master coa_05_Master) {
		this.coa_05_Master = coa_05_Master;
	}

	public String getCoaTypeName() {
		return coaTypeName;
	}

	public void setCoaTypeName(String coaTypeName) {
		this.coaTypeName = coaTypeName;
	}

	public BigDecimal getDebit() {
		return debit;
	}

	public void setDebit(BigDecimal debit) {
		this.debit = debit;
	}

	public BigDecimal getCredit() {
		return credit;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}

	public Date getReportEndDate() {
		return reportEndDate;
	}

	public void setReportEndDate(Date reportEndDate) {
		this.reportEndDate = reportEndDate;
	}
	
}
