package com.pyramix.domain.user;

import com.pyramix.domain.common.IdBasedObject;
import com.pyramix.domain.common.SchemaUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "auth_user_role", schema = SchemaUtil.SCHEMA_COMMON)
public class UserRole extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3704996438997906155L;

	//	`user_role` varchar(255) DEFAULT NULL,
	@Column(name="user_role")
	private String role_name;
	
	//	`enabled` char(1) DEFAULT NULL,
	@Column(name="enabled")
	@Convert(converter = org.hibernate.type.TrueFalseConverter.class)
	private boolean enabled;
	
	@Override
	public String toString() {
		return "UserRole [" + super.toString() + ", role_name=" + role_name + ", enabled=" + enabled + "]";
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
