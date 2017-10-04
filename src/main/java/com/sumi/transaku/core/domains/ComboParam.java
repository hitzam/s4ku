package com.sumi.transaku.core.domains;

import java.util.List;

public class ComboParam{

	
	private List<Province> provinces;
	private List<City> cities;
	private List<BusinessType> businessTypes;
	private List<InventoryCategory> inventoryCategories;
	private List<QtyUnit> qtyUnits;
	private List<Role> roles;
	
	
	public List<Province> getProvinces() {
		return provinces;
	}
	public void setProvinces(List<Province> provinces) {
		this.provinces = provinces;
	}
	public List<City> getCities() {
		return cities;
	}
	public void setCities(List<City> cities) {
		this.cities = cities;
	}
	public List<BusinessType> getBusinessTypes() {
		return businessTypes;
	}
	public void setBusinessTypes(List<BusinessType> businessTypes) {
		this.businessTypes = businessTypes;
	}
	public List<InventoryCategory> getInventoryCategories() {
		return inventoryCategories;
	}
	public void setInventoryCategories(List<InventoryCategory> inventoryCategories) {
		this.inventoryCategories = inventoryCategories;
	}
	public List<QtyUnit> getQtyUnits() {
		return qtyUnits;
	}
	public void setQtyUnits(List<QtyUnit> qtyUnits) {
		this.qtyUnits = qtyUnits;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	
}
