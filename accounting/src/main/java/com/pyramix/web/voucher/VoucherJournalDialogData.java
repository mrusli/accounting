package com.pyramix.web.voucher;

import java.math.BigDecimal;
import java.util.List;

import com.pyramix.domain.voucher.VoucherJournalDebitCredit;
import com.pyramix.domain.voucher.VoucherType;

public class VoucherJournalDialogData {

	private final String[] dataStateDef = {"View", "Edit"};
	
	private VoucherType voucherType;
	
	private String transactionDescription;
	
	private BigDecimal amount;
	
	private boolean credit;

	private List<VoucherJournalDebitCredit> voucherJournalDebitCredit;
	
	private String dataState;
	
	public VoucherType getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(VoucherType voucherType) {
		this.voucherType = voucherType;
	}

	public String getTransactionDescription() {
		return transactionDescription;
	}

	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public boolean isCredit() {
		return credit;
	}

	public void setCredit(boolean credit) {
		this.credit = credit;
	}

	public List<VoucherJournalDebitCredit> getVoucherJournalDebitCredit() {
		return voucherJournalDebitCredit;
	}

	public void setVoucherJournalDebitCredit(List<VoucherJournalDebitCredit> voucherJournalDebitCredit) {
		this.voucherJournalDebitCredit = voucherJournalDebitCredit;
	}

	public String getDataState() {
		return dataState;
	}

	public void setDataState(String dataState) {
		this.dataState = dataState;
	}

	public String[] getDataStateDef() {
		return dataStateDef;
	}
}
