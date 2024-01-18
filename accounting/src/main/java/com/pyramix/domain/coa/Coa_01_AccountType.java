package com.pyramix.domain.coa;

import java.util.List;

import com.pyramix.domain.common.IdBasedObject;
import com.pyramix.domain.common.SchemaUtil;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "mm_coa_01_accounttype", schema = SchemaUtil.SCHEMA_COMMON)
public class Coa_01_AccountType extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7494921544118946827L;

	//  `account_type_name` varchar(255) DEFAULT NULL,
	@Column(name = "account_type_name")
	private String accountTypeName;
	
	//  `account_type_no` int(11) DEFAULT NULL,
	@Column(name = "account_type_no")
	private int accountTypeNumber;

	//	mm_coa_01_accounttype_join_mm_coa_02
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch=FetchType.LAZY)
	@JoinTable(name = "mm_coa_01_accounttype_join_mm_coa_02",
		joinColumns = @JoinColumn(name = "id_mm_coa_01"),
		inverseJoinColumns = @JoinColumn(name = "id_mm_coa_02"))
	private List<Coa_02_AccountGroup> accountGroups;

	@Override
	public String toString() {
		return "Coa_01_AccountType [" + super.toString() + ", accountTypeName=" + accountTypeName 
				+ ", accountTypeNumber=" + accountTypeNumber + "]";
	}

	public String getAccountTypeName() {
		return accountTypeName;
	}

	public void setAccountTypeName(String accountTypeName) {
		this.accountTypeName = accountTypeName;
	}

	public int getAccountTypeNumber() {
		return accountTypeNumber;
	}

	public void setAccountTypeNumber(int accountTypeNumber) {
		this.accountTypeNumber = accountTypeNumber;
	}

	public List<Coa_02_AccountGroup> getAccountGroups() {
		return accountGroups;
	}

	public void setAccountGroups(List<Coa_02_AccountGroup> accountGroups) {
		this.accountGroups = accountGroups;
	}	
}
