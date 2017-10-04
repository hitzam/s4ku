package com.sumi.transaku.core.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.sumi.transaku.core.domains.SupplierPurchaseOrder;
import com.sumi.transaku.core.domains.SupplierPurchaseOrderDetail;

public interface SupplierPurchaseOrderDetailRepository extends CrudRepository<SupplierPurchaseOrderDetail, Integer>{

	List<SupplierPurchaseOrderDetail> findAll();
	List<SupplierPurchaseOrderDetail> findBySupplierPurchaseOrder(SupplierPurchaseOrder purchaseOrder);

}
