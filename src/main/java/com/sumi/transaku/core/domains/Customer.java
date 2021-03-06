package com.sumi.transaku.core.domains;
// Generated May 29, 2016 4:58:28 PM by Hibernate Tools 4.3.1.Final

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * User generated by hbm2java
 */
@Entity
public class Customer implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String name;
	private String email;
	private String password;

	@Transient
	private String oldPassword;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;
	
	private String gender;
	
	private String idCardNumber;
	private String address;
	private String storeAddress;
	private String latitude;
	private String longitude;
	
	@ManyToOne
	@JoinColumn(name = "city_id")
	private City city;

	@Transient
	private Province province;
	
	private int postalCode;
	private String phone;
	private int isSeller;
	private String businessName;
	private String businessPhone;
	
	@ManyToOne
	@JoinColumn(name = "business_type_id")
	private BusinessType businessType;
	
	@JsonFormat(pattern="yyyy-MM-dd", timezone="Asia/Jakarta")
	private Date createdDate;

	@JsonFormat(pattern="yyyy-MM-dd hh:mm:ss", timezone="Asia/Jakarta")
	private Date expiredDate;

	@Transient
	private String idCardBase64;
	private String idCardPath;
	
	@Transient
	private String pictureBase64;
	private String picturePath;

	private String verificationCode;
	
	@JsonFormat(pattern="yyyy-MM-dd hh:mm:ss", timezone="Asia/Jakarta")
	private Date codeExpired;

	private int isVerified;
	private int status;
	private int enabled;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "seller")
//	@JsonIgnore
	private Set<StoreInventory> storeInventories;
	
	@OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<Sell> itemsSold;

	@OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<Sell> itemsBought;
	
	public Customer() {
	}
	public Customer(Integer id, String name, String email, String password, String gender,
			String phone, int isVerified, int status, int enabled, Set<StoreInventory> storeInventories) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.gender = gender;
		this.isVerified = isVerified;
		this.status = status;
		this.enabled = enabled;
		this.storeInventories = storeInventories;
	}

	
	public Customer(Customer customer) {
		super();
		this.id = customer.getId();
		this.name = customer.getName();
		this.email = customer.getEmail();
		this.password = customer.getPassword();
		this.oldPassword = customer.getOldPassword();
		this.role = customer.getRole();
		this.phone = customer.getPhone();
		this.isSeller = customer.getIsSeller();
		this.createdDate = customer.getCreatedDate();
		this.expiredDate = customer.getExpiredDate();
		this.verificationCode = customer.getVerificationCode();
		this.isVerified = customer.getIsVerified();
		this.status = customer.getStatus();
		this.enabled = customer.getEnabled();
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStoreAddress() {
		return storeAddress;
	}
	public void setStoreAddress(String storeAddress) {
		this.storeAddress = storeAddress;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getIsSeller() {
		return isSeller;
	}

	public void setIsSeller(int isSeller) {
		this.isSeller = isSeller;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getBusinessPhone() {
		return businessPhone;
	}
	public void setBusinessPhone(String businessPhone) {
		this.businessPhone = businessPhone;
	}
	public BusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(BusinessType businessType) {
		this.businessType = businessType;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public int getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(int isVerified) {
		this.isVerified = isVerified;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getEnabled() {
		return enabled;
	}

	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}

	public Set<StoreInventory> getStoreInventories() {
		return storeInventories;
	}
	public void setStoreInventories(Set<StoreInventory> storeInventories) {
		this.storeInventories = storeInventories;
	}
	public String getIdCardNumber() {
		return idCardNumber;
	}

	public void setIdCardNumber(String idCardNumber) {
		this.idCardNumber = idCardNumber;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public int getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(int postalCode) {
		this.postalCode = postalCode;
	}

	public String getIdCardPath() {
		return idCardPath;
	}

	public void setIdCardPath(String idCardPath) {
		this.idCardPath = idCardPath;
	}

	public String getPicturePath() {
		return picturePath;
	}

	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}

	public String getIdCardBase64() {
		return idCardBase64;
	}

	public void setIdCardBase64(String idCardBase64) {
		this.idCardBase64 = idCardBase64;
	}

	public String getPictureBase64() {
		return pictureBase64;
	}

	public void setPictureBase64(String pictureBase64) {
		this.pictureBase64 = pictureBase64;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	public String getVerificationCode() {
		return verificationCode;
	}
	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
	
	public Date getCodeExpired() {
		return codeExpired;
	}
	public void setCodeExpired(Date codeExpired) {
		this.codeExpired = codeExpired;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public Province getProvince() {
		return province;
	}
	public void setProvince(Province province) {
		this.province = province;
	}
	
	
	
}
