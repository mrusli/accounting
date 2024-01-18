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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "mm_coa_02_accountgroup", schema = SchemaUtil.SCHEMA_COMMON)
public class Coa_02_AccountGroup extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4848577379250932146L;

	//  `account_group_name` varchar(255) DEFAULT NULL,
	@Column(name = "account_group_name")
	private String accountGroupName;
	
	//	typecoa_no int(11)
	@Column(name = "typecoa_no")
	private int typeCoaNumber;
	
	//  `account_group_no` int(11) DEFAULT NULL,
	@Column(name = "account_group_no")
	private int accountGroupNumber;

	//	mm_coa_01_accounttype_join_mm_coa_02
	@ManyToOne(cascade = { CascadeType.DETACH }, fetch = FetchType.LAZY)
	@JoinTable(name = "mm_coa_01_accounttype_join_mm_coa_02",
			joinColumns = @JoinColumn(name = "id_mm_coa_02"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_01"))
	private Coa_01_AccountType accountType;

	//	mm_coa_02_accountgroup_join_mm_coa_03
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch=FetchType.LAZY)
	@JoinTable(name = "mm_coa_02_accountgroup_join_mm_coa_03",
			joinColumns = @JoinColumn(name = "id_mm_coa_02"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_03"))
	private List<Coa_03_SubAccount01> subAccount01s;

	@Override
	public String toString() {
		return "Coa_02_AccountGroup [" + super.toString() +", accountGroupName=" 
				+ accountGroupName + ", typeCoaNumber=" + getTypeCoaNumber()
				+ ", accountGroupNumber=" + accountGroupNumber + "]";
	}

	public String getAccountGroupName() {
		return accountGroupName;
	}

	public void setAccountGroupName(String accountGroupName) {
		this.accountGroupName = accountGroupName;
	}

	public int getAccountGroupNumber() {
		return accountGroupNumber;
	}

	public void setAccountGroupNumber(int accountGroupNumber) {
		this.accountGroupNumber = accountGroupNumber;
	}

	public Coa_01_AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(Coa_01_AccountType accountType) {
		this.accountType = accountType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getTypeCoaNumber() {
		return typeCoaNumber;
	}

	public void setTypeCoaNumber(int typeCoaNumber) {
		this.typeCoaNumber = typeCoaNumber;
	}

	public List<Coa_03_SubAccount01> getSubAccount01s() {
		return subAccount01s;
	}

	public void setSubAccount01s(List<Coa_03_SubAccount01> subAccount01s) {
		this.subAccount01s = subAccount01s;
	}
}
