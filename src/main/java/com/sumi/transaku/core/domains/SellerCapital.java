package com.sumi.transaku.core.domains;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class SellerCapital implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "seller_id")
	//@JsonIgnore
	private Customer seller;
	
	@OneToOne
    @JoinColumn(name = "sell_id")
	private Sell sell;
	
	
	private double mutation;
	private double balance;
	private String trxType;
	private String note;
	
	@JsonFormat(pattern="yyyy-MM-dd hh:mm:ss", timezone="Asia/Jakarta")
	private Date trxDate;
	
	@Transient
	@JsonIgnore
	private String inventoryCode;
	
	public SellerCapital(){};
	
	public SellerCapital(Customer seller, double mutation, String note) {
		super();
		this.seller = seller;
		this.mutation = mutation;
		this.note = note;
	}
	public SellerCapital(Customer seller, double mutation, Sell sell) {
		super();
		this.seller = seller;
		this.mutation = mutation;
		this.sell = sell;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Sell getSell() {
		return sell;
	}
	public void setSell(Sell sell) {
		this.sell = sell;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public String getTrxType() {
		return trxType;
	}
	public void setTrxType(String trxType) {
		this.trxType = trxType;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Date getTrxDate() {
		return trxDate;
	}
	public void setTrxDate(Date trxDate) {
		this.trxDate = trxDate;
	}
	public Customer getSeller() {
		return seller;
	}
	public void setSeller(Customer seller) {
		this.seller = seller;
	}
	public double getMutation() {
		return mutation;
	}
	public void setMutation(double mutation) {
		this.mutation = mutation;
	}
	public String getInventoryCode() {
		return inventoryCode;
	}
	public void setInventoryCode(String inventoryCode) {
		this.inventoryCode = inventoryCode;
	}
	
	
	
}
