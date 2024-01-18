package com.pyramix.domain.gl;

import java.math.BigDecimal;
import java.util.Date;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.common.IdBasedObject;
import com.pyramix.domain.common.SchemaUtil;
import com.pyramix.domain.voucher.VoucherSerialNumber;
import com.pyramix.domain.voucher.VoucherType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "general_ledger", schema = SchemaUtil.SCHEMA_COMMON)
public class GeneralLedger extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2206939237775273645L;

	//	`coa_id_fk` bigint DEFAULT NULL,
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "coa_id_fk")	
	private Coa_05_Master masterCoa;
	
	//	`posting_date` date DEFAULT NULL,
	@Column(name = "posting_date")
	@Temporal(TemporalType.DATE)
	private Date postingDate;
	
	//	`posting_voucher_no_id_fk` bigint DEFAULT NULL,
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "posting_voucher_no_id_fk")
	private VoucherSerialNumber postingVoucherNumber;
	
	//	`credit_amount` decimal(19,2) DEFAULT NULL,
	@Column(name = "credit_amount")
	private BigDecimal creditAmount;
	
	//	`debit_amount` decimal(19,2) DEFAULT NULL,
	@Column(name = "debit_amount")
	private BigDecimal debitAmount;
	
	//	`dbcr_description` varchar(255) DEFAULT NULL,
	@Column(name = "dbcr_description")
	private String dbcrDescription;
	
	//	`transaction_description` varchar(255) DEFAULT NULL,
	@Column(name = "transaction_description")
	private String transactionDescription;
	
	//	`document_ref` varchar(255) DEFAULT NULL,
	@Column(name = "document_ref")
	private String documentRef;
	
	//	`transaction_date` date DEFAULT NULL,
	@Column(name = "transaction_date")
	@Temporal(TemporalType.DATE)
	private Date transactionDate;
	
	//	`voucher_type` int DEFAULT NULL,
	@Column(name = "voucher_type")
	@Enumerated(EnumType.ORDINAL)
	private VoucherType voucherType;
	
	//	`voucher_no_id_fk` bigint DEFAULT NULL,
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "voucher_no_id_fk")
	private VoucherSerialNumber voucherNumber;

	@Override
	public String toString() {
		return "GeneralLedger [" + super.toString() + ", masterCoa=" + masterCoa + ", postingDate=" + postingDate + ", postingVoucherNumber="
				+ postingVoucherNumber + ", creditAmount=" + creditAmount + ", debitAmount=" + debitAmount
				+ ", dbcrDescription=" + dbcrDescription + ", transactionDescription=" + transactionDescription
				+ ", documentRef=" + documentRef + ", transactionDate=" + transactionDate + ", voucherType="
				+ voucherType + ", voucherNumber=" + voucherNumber + "]";
	}

	public Coa_05_Master getMasterCoa() {
		return masterCoa;
	}

	public void setMasterCoa(Coa_05_Master masterCoa) {
		this.masterCoa = masterCoa;
	}

	public Date getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}

	public VoucherSerialNumber getPostingVoucherNumber() {
		return postingVoucherNumber;
	}

	public void setPostingVoucherNumber(VoucherSerialNumber postingVoucherNumber) {
		this.postingVoucherNumber = postingVoucherNumber;
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

	public String getTransactionDescription() {
		return transactionDescription;
	}

	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}

	public String getDocumentRef() {
		return documentRef;
	}

	public void setDocumentRef(String documentRef) {
		this.documentRef = documentRef;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public VoucherType getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(VoucherType voucherType) {
		this.voucherType = voucherType;
	}

	public VoucherSerialNumber getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(VoucherSerialNumber voucherNumber) {
		this.voucherNumber = voucherNumber;
	}
}
