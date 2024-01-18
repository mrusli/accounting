package com.pyramix.domain.voucher;

import java.util.Date;

import com.pyramix.domain.common.IdBasedObject;
import com.pyramix.domain.common.SchemaUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "voucher_serial_number", schema = SchemaUtil.SCHEMA_COMMON)
public class VoucherSerialNumber extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 789758718010560923L;

	//	`voucher_type` int DEFAULT NULL,
	@Column(name = "voucher_type")
	@Enumerated(EnumType.ORDINAL)
	private VoucherType voucherType;
	
	//	`serial_date` date DEFAULT NULL,
	@Column(name = "serial_date")
	@Temporal(TemporalType.DATE)
	private Date serialDate;
	
	//	`serial_no` int NOT NULL,
	@Column(name = "serial_no")
	private int serialNo;
	
	//	`serial_comp` varchar(255) DEFAULT NULL,
	@Column(name = "serial_comp")
	private String serialComp;

	@Override
	public String toString() {
		return "VoucherSerialNumber [" + super.toString() +", voucherType=" + voucherType 
				+ ", serialDate=" + serialDate + ", serialNo=" + serialNo + 
				", serialComp=" + serialComp + "]";
	}

	public VoucherType getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(VoucherType voucherType) {
		this.voucherType = voucherType;
	}

	public Date getSerialDate() {
		return serialDate;
	}

	public void setSerialDate(Date serialDate) {
		this.serialDate = serialDate;
	}

	public int getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(int serialNo) {
		this.serialNo = serialNo;
	}

	public String getSerialComp() {
		return serialComp;
	}

	public void setSerialComp(String serialComp) {
		this.serialComp = serialComp;
	}
	
	
}
