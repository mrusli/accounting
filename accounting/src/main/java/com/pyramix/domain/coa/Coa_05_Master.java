package com.pyramix.domain.coa;

import com.pyramix.domain.common.IdBasedObject;
import com.pyramix.domain.common.SchemaUtil;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mm_coa_05_master", schema = SchemaUtil.SCHEMA_COMMON)
public class Coa_05_Master extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3328997869973243113L;

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

	//	mm_coa_01_accountType_join_mm_coa_05
	@ManyToOne(cascade = { CascadeType.DETACH }, fetch = FetchType.LAZY)
	@JoinTable(name = "mm_coa_01_accounttype_join_mm_coa_05",
			joinColumns = @JoinColumn(name = "id_mm_coa_05"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_01"))
	private Coa_01_AccountType accountType;
	
	//	mm_coa_02_accountGroup_join_mm_coa_05
	@ManyToOne(cascade = { CascadeType.DETACH }, fetch = FetchType.LAZY)
	@JoinTable(name = "mm_coa_02_accountgroup_join_mm_coa_05",
			joinColumns = @JoinColumn(name = "id_mm_coa_05"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_02"))
	private Coa_02_AccountGroup accountGroup;
	
	//	mm_coa_03_subaccount01_join_mm_coa_05
	@ManyToOne(cascade = { CascadeType.DETACH }, fetch = FetchType.LAZY)
	@JoinTable(name = "mm_coa_03_subaccount01_join_mm_coa_05",
			joinColumns = @JoinColumn(name = "id_mm_coa_05"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_03"))
	private Coa_03_SubAccount01 subAccount01;
	
	//	mm_coa_04_subaccount02_join_mm_coa_05
	@ManyToOne(cascade = { CascadeType.DETACH }, fetch = FetchType.LAZY)
	@JoinTable(name = "mm_coa_04_subaccount02_join_mm_coa_05",
			joinColumns = @JoinColumn(name = "id_mm_coa_05"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_04"))
	private Coa_04_SubAccount02 subAccount02;

	@Override
	public String toString() {
		return "Coa_05_Master [" + super.toString() + ", masterCoaName=" + masterCoaName + ", typeCoaNumber=" + typeCoaNumber
				+ ", groupCoaNumber=" + groupCoaNumber + ", subaccount01CoaNumber=" + subaccount01CoaNumber
				+ ", subaccount02CoaNumber=" + subaccount02CoaNumber + ", masterCoaNumber=" + masterCoaNumber
				+ ", masterCoaComp=" + masterCoaComp + ", creditAccount=" + creditAccount + ", restricted=" + restricted
				+ ", active=" + active + "]";
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

	public Coa_04_SubAccount02 getSubAccount02() {
		return subAccount02;
	}

	public void setSubAccount02(Coa_04_SubAccount02 subAccount02) {
		this.subAccount02 = subAccount02;
	}

	public Coa_01_AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(Coa_01_AccountType accountType) {
		this.accountType = accountType;
	}

	public Coa_02_AccountGroup getAccountGroup() {
		return accountGroup;
	}

	public void setAccountGroup(Coa_02_AccountGroup accountGroup) {
		this.accountGroup = accountGroup;
	}

	public Coa_03_SubAccount01 getSubAccount01() {
		return subAccount01;
	}

	public void setSubAccount01(Coa_03_SubAccount01 subAccount01) {
		this.subAccount01 = subAccount01;
	}

}
