package com.pyramix.domain.report;

import java.math.BigDecimal;

import com.pyramix.domain.coa.Coa_05_Master;

public class TrialBalance {
	private String coaTypeName;

	private Coa_05_Master coa_05_Master;
	
	private BigDecimal totalDebit;
	
	private BigDecimal totalCredit;

	@Override
	public String toString() {
		return "TrialBalance [coa_05_Master=" + coa_05_Master + ", totalDebit=" + totalDebit + ", totalCredit="
				+ totalCredit + "]";
	}

	public Coa_05_Master getCoa_05_Master() {
		return coa_05_Master;
	}

	public void setCoa_05_Master(Coa_05_Master coa_05_Master) {
		this.coa_05_Master = coa_05_Master;
	}

	public BigDecimal getTotalDebit() {
		return totalDebit;
	}

	public void setTotalDebit(BigDecimal totalDebit) {
		this.totalDebit = totalDebit;
	}

	public BigDecimal getTotalCredit() {
		return totalCredit;
	}

	public void setTotalCredit(BigDecimal totalCredit) {
		this.totalCredit = totalCredit;
	}

	public String getCoaTypeName() {
		return coaTypeName;
	}

	public void setCoaTypeName(String coaTypeName) {
		this.coaTypeName = coaTypeName;
	}
	
}
