package com.pyramix.web.gl;

import java.math.BigDecimal;

public class LedgerReport {

	private String transDate;
	private String coa;
	private String description;
	private BigDecimal debit;
	private BigDecimal credit;
	
	public LedgerReport(String transDate, String coa, String description, BigDecimal debit, BigDecimal credit) {
		super();
		this.transDate = transDate;
		this.coa = coa;
		this.description = description;
		this.debit = debit;
		this.credit = credit;
	}

	@Override
	public String toString() {
		return "LedgerReport [transDate=" + transDate + ", coa=" + coa + ", description=" + description + ", debit="
				+ debit + ", credit=" + credit + "]";
	}

	public String getTransDate() {
		return transDate;
	}
	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}
	public String getCoa() {
		return coa;
	}
	public void setCoa(String coa) {
		this.coa = coa;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	
}
