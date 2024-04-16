package com.pyramix.domain.project;

import java.math.BigDecimal;

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
@Table(name = "project_journal_debitcredit", schema = SchemaUtil.SCHEMA_COMMON)
public class ProjectJournalDebitCredit extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6862512618483510615L;
	
	//	`credit_amount` decimal(19,2) DEFAULT NULL,
	@Column(name = "credit_amount")
	private BigDecimal creditAmount;
	
	//	`debit_amount` decimal(19,2) DEFAULT NULL,
	@Column(name = "debit_amount")
	private BigDecimal debitAmount;

	//	`dbcr_description` varchar(255) DEFAULT NULL,
	@Column(name = "dbcr_description")
	private String dbcrDescription;

	//	`coa_id_fk` bigint DEFAULT NULL,	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "coa_id_fk")
	private Coa_05_Master masterCoa;

	@Override
	public String toString() {
		return "ProjectJournalDebitCredit [" + super.toString() + ", creditAmount=" + creditAmount + ", debitAmount=" + debitAmount
				+ ", dbcrDescription=" + dbcrDescription + ", masterCoa=" + masterCoa + "]";
	}

	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}

	public BigDecimal getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}

	public String getDbcrDescription() {
		return dbcrDescription;
	}

	public void setDbcrDescription(String dbcrDescription) {
		this.dbcrDescription = dbcrDescription;
	}

	public Coa_05_Master getMasterCoa() {
		return masterCoa;
	}

	public void setMasterCoa(Coa_05_Master masterCoa) {
		this.masterCoa = masterCoa;
	}
	
}
