package com.osintegrators.example;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * @deprecated please use {@link com.sap.hana.cloud.samples.granny.model.Contact} instead
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "Address.findAll", query = "select a from Address a") })
public class Address {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String _id; // TODO revert back to Long once the srv/ui layer are updated to use new model!

	private String name;
	private String address;
	private String phone;
	private String email;

	public Address(String name, String address, String phoneNumber, String email) {
		this.name = name;
		this.address = address;
		this.phone = phoneNumber;
		this.email = email;
	}

	// ADDED Default Constructor Because of Build Errors in pom.xml
	public Address() {
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phoneNumber) {
		this.phone = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
