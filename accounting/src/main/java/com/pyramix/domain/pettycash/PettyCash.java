package com.pyramix.domain.pettycash;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pyramix.domain.common.IdBasedObject;
import com.pyramix.domain.common.SchemaUtil;
import com.pyramix.domain.gl.GeneralLedger;
import com.pyramix.domain.user.User;
import com.pyramix.domain.voucher.VoucherSerialNumber;
import com.pyramix.domain.voucher.VoucherStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "petty_cash", schema = SchemaUtil.SCHEMA_COMMON)
public class PettyCash extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7520263227632797432L;
	
	//	`the_sum_of` decimal(19,2) DEFAULT NULL,
	@Column(name = "the_sum_of")
	private BigDecimal theSumOf;
	
	//	`transaction_date` date DEFAULT NULL,
	@Column(name = "transaction_date")
	@Temporal(TemporalType.DATE)
	private Date transactionDate;
	
	//	`transaction_description` varchar(255) DEFAULT NULL,
	@Column(name = "transaction_description")
	private String transactionDescription;
	
	//	`document_ref` varchar(255) DEFAULT NULL,
	@Column(name = "document_ref")
	private String documentRef;
	
	//	`voucher_no_id_fk` bigint DEFAULT NULL,
	@OneToOne(cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
	@JoinColumn(name = "voucher_no_id_fk")	
	private VoucherSerialNumber voucherNumber;

	//	`user_create_id_fk` bigint DEFAULT NULL,
	@OneToOne
	@JoinColumn(name = "user_create_id_fk")	
	private User userCreate;

	//  `voucher_status` int DEFAULT NULL,
	@Column(name = "voucher_status")
	@Enumerated(EnumType.ORDINAL)
	private VoucherStatus voucherStatus;

	//  `posting_date` date DEFAULT NULL,
	@Column(name = "posting_date")
	@Temporal(TemporalType.DATE)
	private Date postingDate;
	
	//  `posting_voucher_no_id_fk` bigint DEFAULT NULL,	
	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "posting_voucher_no_id_fk")	
	private VoucherSerialNumber postingVoucherNumber;	
	
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(name = "petty_cash_join_debitcredit",
		joinColumns = @JoinColumn(name = "id_petty_cash"),
		inverseJoinColumns = @JoinColumn(name = "id_debitcredit"))	
	private List<PettyCashDebitCredit> pettyCashDebitCredits;

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(name = "petty_cash_join_general_ledger",
		joinColumns = @JoinColumn(name = "id_petty_cash"),
		inverseJoinColumns = @JoinColumn(name = "id_general_ledger"))	
	private List<GeneralLedger> generalLedgers;
	
	@Override
	public String toString() {
		return "PettyCash [id=" + super.getId() + ", createDate=" + super.getCreateDate() 
				+ ", lastModified=" + super.getLastModified() + ", theSumOf=" + theSumOf 
				+ ", transactionDate=" + transactionDate + ", transactionDescription="
				+ transactionDescription + ", documentRef=" + documentRef + ", voucherNumber=" + voucherNumber
				+ ", userCreate=" + userCreate + ", voucherStatus=" + voucherStatus + ", postingDate=" + postingDate
				+ ", postingVoucherNumber=" + postingVoucherNumber + ", pettyCashDebitCredits=" + pettyCashDebitCredits
				+ "]";
	}

	public BigDecimal getTheSumOf() {
		return theSumOf;
	}

	public void setTheSumOf(BigDecimal theSumOf) {
		this.theSumOf = theSumOf;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
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

	public VoucherSerialNumber getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(VoucherSerialNumber voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	public User getUserCreate() {
		return userCreate;
	}

	public void setUserCreate(User userCreate) {
		this.userCreate = userCreate;
	}

	public List<PettyCashDebitCredit> getPettyCashDebitCredits() {
		return pettyCashDebitCredits;
	}

	public void setPettyCashDebitCredits(List<PettyCashDebitCredit> pettyCashDebitCredits) {
		this.pettyCashDebitCredits = pettyCashDebitCredits;
	}

	public VoucherStatus getVoucherStatus() {
		return voucherStatus;
	}

	public void setVoucherStatus(VoucherStatus voucherStatus) {
		this.voucherStatus = voucherStatus;
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

	public List<GeneralLedger> getGeneralLedgers() {
		return generalLedgers;
	}

	public void setGeneralLedgers(List<GeneralLedger> generalLedgers) {
		this.generalLedgers = generalLedgers;
	}

}
