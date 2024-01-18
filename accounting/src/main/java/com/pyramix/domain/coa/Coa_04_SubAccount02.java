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
@Table(name = "mm_coa_04_subaccount02", schema = SchemaUtil.SCHEMA_COMMON)
public class Coa_04_SubAccount02 extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5832901511377112178L;

	//  `subaccount02_name` varchar(255) DEFAULT NULL,
	@Column(name = "subaccount02_name")
	private String subAccount02Name;
	
	//	typecoa_no int(11)
	@Column(name = "typecoa_no")
	private int typeCoaNumber;
	
	//	groupcoa_no int(11)
	@Column(name = "groupcoa_no")
	private int groupCoaNumber;	
	
	//	subaccount01coa_no int(11)
	@Column(name = "subaccount01coa_no")
	private int subAccount01CoaNumber;
	
	//  `subaccount02_no` int(11) DEFAULT NULL,
	@Column(name = "subaccount02_no")
	private int subAccount02Number;

	//	mm_coa_04_subaccount02_join_mm_coa_05
	@OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true, fetch=FetchType.LAZY)
	@JoinTable(name = "mm_coa_04_subaccount02_join_mm_coa_05",
			joinColumns = @JoinColumn(name = "id_mm_coa_04"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_05"))
	private List<Coa_05_Master> masters;
	
	//	mm_coa_03_subaccount01_join_mm_coa_04
	@ManyToOne(cascade = { CascadeType.DETACH }, fetch = FetchType.LAZY)
	@JoinTable(name = "mm_coa_03_subaccount01_join_mm_coa_04",
			joinColumns = @JoinColumn(name = "id_mm_coa_04"),
			inverseJoinColumns = @JoinColumn(name = "id_mm_coa_03"))
	private Coa_03_SubAccount01 subAccount01;

	@Override
	public String toString() {
		return "Coa_04_SubAccount02 [" + super.toString() + ", subAccount02Name=" + subAccount02Name 
				+ ", typeCoaNumber=" + typeCoaNumber + ", groupCoaNumber=" + groupCoaNumber 
				+ ", subaccount01CoaNumber=" + subAccount01CoaNumber + ", subAccount02Number=" 
				+ subAccount02Number + "]";
	}

	public String getSubAccount02Name() {
		return subAccount02Name;
	}

	public void setSubAccount02Name(String subAccount02Name) {
		this.subAccount02Name = subAccount02Name;
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

	public int getSubAccount01CoaNumber() {
		return subAccount01CoaNumber;
	}

	public void setSubAccount01CoaNumber(int subAccount01CoaNumber) {
		this.subAccount01CoaNumber = subAccount01CoaNumber;
	}

	public int getSubAccount02Number() {
		return subAccount02Number;
	}

	public void setSubAccount02Number(int subAccount02Number) {
		this.subAccount02Number = subAccount02Number;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Coa_03_SubAccount01 getSubAccount01() {
		return subAccount01;
	}

	public void setSubAccount01(Coa_03_SubAccount01 subAccount01) {
		this.subAccount01 = subAccount01;
	}

	public List<Coa_05_Master> getMasters() {
		return masters;
	}

	public void setMasters(List<Coa_05_Master> masters) {
		this.masters = masters;
	}
}
