package com.sumi.transaku.core.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.Customer;
import com.sumi.transaku.core.domains.Supplier;
import com.sumi.transaku.core.domains.SupplierPurchaseOrder;

public interface SupplierPurchaseOrderRepository extends CrudRepository<SupplierPurchaseOrder, Integer>{

	List<SupplierPurchaseOrder> findAll();
	List<SupplierPurchaseOrder> findBySupplier(Supplier supplier);
	List<SupplierPurchaseOrder> findByCustomer(Customer customer);
	List<SupplierPurchaseOrder> findByStatus(int status);
	List<SupplierPurchaseOrder> findByStatusNotIn(List<Integer> status);
	SupplierPurchaseOrder findByCode(String code);
	SupplierPurchaseOrder findTopBySupplierOrderByIdDesc(Supplier supplier);
	List<SupplierPurchaseOrder> findBySupplierAndCreatedDateBetween(Supplier supplier, Date startDate, Date endDate);
	List<SupplierPurchaseOrder> findByCustomerAndCreatedDateBetween(Customer customer, Date startDate, Date endDate);

	long count();
}
