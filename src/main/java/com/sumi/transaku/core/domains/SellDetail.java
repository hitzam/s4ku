package com.sumi.transaku.core.domains;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class SellDetail implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "sell_id")
	@JsonIgnore
	private Sell sell;

	@ManyToOne
	@JoinColumn(name = "item_id")
	//@JsonIgnore
	private StoreInventory inventory;
	
	private double qty;
	private double price;
	private double subTotal;
	
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
	public StoreInventory getInventory() {
		return inventory;
	}
	public void setInventory(StoreInventory inventory) {
		this.inventory = inventory;
	}
	public double getQty() {
		return qty;
	}
	public void setQty(double qty) {
		this.qty = qty;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}
	
	@Override
	public String toString() {
		return "SellDetail [id=" + id + ", inventory=" + inventory + ", qty=" + qty + ", price="
				+ price + ", subTotal=" + subTotal + "]";
	}
	
	
}
