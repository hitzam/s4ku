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
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Sell implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String code;
	
	@JsonFormat(pattern="yyyy-MM-dd hh:mm:ss", timezone="Asia/Jakarta")
	private Date createdDate;

	@JsonFormat(pattern="yyyy-MM-dd hh:mm:ss", timezone="Asia/Jakarta")
	private Date updatedDate;

	@ManyToOne
	@JoinColumn(name = "seller_id")
	//@JsonIgnore
	private Customer seller;

	@ManyToOne
	@JoinColumn(name = "buyer_id")
	//@JsonIgnore
	private Customer buyer;
	
	private double totalPrice;
	
	@OneToMany(mappedBy = "sell", cascade = CascadeType.ALL)
	private Set<SellDetail> sellDetails;
	
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "sell")
    @JsonIgnore
	private SellerCapital sellerCapital;
	
	@Transient
	private double sellSummary;

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
	public Customer getSeller() {
		return seller;
	}
	public void setSeller(Customer seller) {
		this.seller = seller;
	}
	public Customer getBuyer() {
		return buyer;
	}
	public void setBuyer(Customer buyer) {
		this.buyer = buyer;
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Set<SellDetail> getSellDetails() {
		return sellDetails;
	}
	public void setSellDetails(Set<SellDetail> sellDetails) {
		this.sellDetails = sellDetails;
	}
	
	public SellerCapital getSellerCapital() {
		return sellerCapital;
	}
	public void setSellerCapital(SellerCapital sellerCapital) {
		this.sellerCapital = sellerCapital;
	}
	public double getSellSummary() {
		return sellSummary;
	}
	public void setSellSummary(double sellSummary) {
		this.sellSummary = sellSummary;
	}
	@Override
	public String toString() {
		return "Sell [id=" + id + ", code=" + code + ", createdDate=" + createdDate + ", updatedDate=" + updatedDate
				+ ", totalPrice=" + totalPrice + ", sellDetails=" + sellDetails + "]";
	}

	
}
