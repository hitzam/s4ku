package com.sumi.transaku.core.domains;

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

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class SupplierPurchaseOrder implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String code;
	
	@ManyToOne
	@JoinColumn(name = "supplier_id")
	//@JsonIgnore
	private Supplier supplier;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	//@JsonIgnore
	private Customer customer;
	
	private double totalPrice;
	
	@JsonFormat(pattern="yyyy-MM-dd hh:mm:ss", timezone="Asia/Jakarta")
	private Date createdDate;

	@JsonFormat(pattern="yyyy-MM-dd hh:mm:ss", timezone="Asia/Jakarta")
	private Date updatedDate;

	private int status;
	
	@OneToMany(mappedBy = "supplierPurchaseOrder", cascade = CascadeType.ALL)
	private Set<SupplierPurchaseOrderDetail> orderDetails;
	
	@ManyToOne
	@JoinColumn(name = "updated_by")
	//@JsonIgnore
	private User updatedBy;	

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Set<SupplierPurchaseOrderDetail> getOrderDetails() {
		return orderDetails;
	}
	public void setOrderDetails(Set<SupplierPurchaseOrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public User getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	
}
