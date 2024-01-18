package com.pyramix.domain.gl;

import java.math.BigDecimal;
import java.util.Date;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.common.IdBasedObject;
import com.pyramix.domain.common.SchemaUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "balance", schema = SchemaUtil.SCHEMA_COMMON)
public class Balance extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3054076467494602058L;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "coa_id_fk")	
	private Coa_05_Master coa_05_Master;
	
	@Column(name = "debit_total")
	private BigDecimal debitTotal;
	
	@Column(name = "credit_total")
	private BigDecimal creditTotal;
	
	@Column(name = "balance_amount")
	private BigDecimal balanceAmount;
	
	@Column(name = "reset_balance")
	@Convert(converter = org.hibernate.type.TrueFalseConverter.class)
	private boolean resetBalance;
	
	@Column(name = "closing_date")
	@Temporal(TemporalType.DATE)
	private Date closingDate;
	
	@Override
	public String toString() {
		return "AccountClose [" + super.toString() + ", coa_05_Master=" + coa_05_Master + ", debitTotal=" 
				+ debitTotal + ", creditTotal="	+ creditTotal + ", closeAmount=" + getBalanceAmount() 
				+ ", resetBalance=" + resetBalance + ", closingDate=" + closingDate + "]";
	}

	public Coa_05_Master getCoa_05_Master() {
		return coa_05_Master;
	}

	public void setCoa_05_Master(Coa_05_Master coa_05_Master) {
		this.coa_05_Master = coa_05_Master;
	}

	public BigDecimal getDebitTotal() {
		return debitTotal;
	}

	public void setDebitTotal(BigDecimal debitTotal) {
		this.debitTotal = debitTotal;
	}

	public BigDecimal getCreditTotal() {
		return creditTotal;
	}

	public void setCreditTotal(BigDecimal creditTotal) {
		this.creditTotal = creditTotal;
	}

	public boolean isResetBalance() {
		return resetBalance;
	}

	public void setResetBalance(boolean resetBalance) {
		this.resetBalance = resetBalance;
	}

	public Date getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(Date closingDate) {
		this.closingDate = closingDate;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}
}
