package com.pyramix.domain.creditcard;

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

@Entity
@Table(name = "credit_card_coa", schema = SchemaUtil.SCHEMA_COMMON)
public class CreditCardCoa extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3126937708758233367L;

	//  `mastercoa_name` varchar(255) DEFAULT NULL
	@Column(name = "mastercoa_name")
	private String masterCoaName;
	
	//	`typecoa_no` INT(11) NULL DEFAULT NULL
	@Column(name = "typecoa_no")
	private int typeCoaNumber;
	
	//	groupcoa_no int(11)
	@Column(name = "groupcoa_no")
	private int groupCoaNumber;	
	
	//	subaccount01coa_no int(11)
	@Column(name = "subaccount01coa_no")
	private int subaccount01CoaNumber;	
	
	//	subaccount02coa_no int(11)
	@Column(name = "subaccount02coa_no")
	private int subaccount02CoaNumber;
	
	//  `mastercoa_no` int(11) DEFAULT NULL,
	@Column(name = "mastercoa_no")
	private int masterCoaNumber;
	
	//  UNIQUE KEY `mastercoa_comp_UNIQUE` (`mastercoa_comp`),
	//  `mastercoa_comp` varchar(255) DEFAULT NULL,
	@Column(name = "mastercoa_comp")
	private String masterCoaComp;

	//  `credit_account` char(1) DEFAULT NULL,
	@Column(name = "credit_account")
	@Convert(converter = org.hibernate.type.TrueFalseConverter.class)
	private boolean creditAccount;
	
	//  `restricted` char(1) DEFAULT NULL,
	@Column(name = "restricted")
	@Convert(converter = org.hibernate.type.TrueFalseConverter.class)
	private boolean restricted;
	
	//  `active` char(1) DEFAULT NULL,
	@Column(name = "active")
	@Convert(converter = org.hibernate.type.TrueFalseConverter.class)
	private boolean active;

	//	`mm_coa_05_id_fk` bigint DEFAULT NULL,	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "mm_coa_05_id_fk")
	private Coa_05_Master coa_05_Master;

	@Override
	public String toString() {
		return "CreditCardCoa [" + super.toString() + ", masterCoaName=" + masterCoaName + ", typeCoaNumber=" + typeCoaNumber
				+ ", groupCoaNumber=" + groupCoaNumber + ", subaccount01CoaNumber=" + subaccount01CoaNumber
				+ ", subaccount02CoaNumber=" + subaccount02CoaNumber + ", masterCoaNumber=" + masterCoaNumber
				+ ", masterCoaComp=" + masterCoaComp + ", creditAccount=" + creditAccount + ", restricted=" + restricted
				+ ", active=" + active + ", coa_05_Master=" + coa_05_Master + "]";
	}

	public String getMasterCoaName() {
		return masterCoaName;
	}

	public void setMasterCoaName(String masterCoaName) {
		this.masterCoaName = masterCoaName;
	}

	public int getTypeCoaNumber() {
		return typeCoaNumber;
	}

	public void setTypeCoaNumber(int typeCoaNumber) {
		this.typeCoaNumber = typeCoaNumber;
	}

	public int getGroupCoaNumber() {
		return groupCoaNumber;
	}

	public void setGroupCoaNumber(int groupCoaNumber) {
		this.groupCoaNumber = groupCoaNumber;
	}

	public int getSubaccount01CoaNumber() {
		return subaccount01CoaNumber;
	}

	public void setSubaccount01CoaNumber(int subaccount01CoaNumber) {
		this.subaccount01CoaNumber = subaccount01CoaNumber;
	}

	public int getSubaccount02CoaNumber() {
		return subaccount02CoaNumber;
	}

	public void setSubaccount02CoaNumber(int subaccount02CoaNumber) {
		this.subaccount02CoaNumber = subaccount02CoaNumber;
	}

	public int getMasterCoaNumber() {
		return masterCoaNumber;
	}

	public void setMasterCoaNumber(int masterCoaNumber) {
		this.masterCoaNumber = masterCoaNumber;
	}

	public String getMasterCoaComp() {
		return masterCoaComp;
	}

	public void setMasterCoaComp(String masterCoaComp) {
		this.masterCoaComp = masterCoaComp;
	}

	public boolean isCreditAccount() {
		return creditAccount;
	}

	public void setCreditAccount(boolean creditAccount) {
		this.creditAccount = creditAccount;
	}

	public boolean isRestricted() {
		return restricted;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Coa_05_Master getCoa_05_Master() {
		return coa_05_Master;
	}

	public void setCoa_05_Master(Coa_05_Master coa_05_Master) {
		this.coa_05_Master = coa_05_Master;
	}
	
	
}
