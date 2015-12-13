package org.tudresden.ecatering.model.stock;

import org.springframework.data.repository.CrudRepository;

public interface StockItemRepository extends CrudRepository<StockItem,Long> {
	
	Iterable<StockItem> findByGrocery(Grocery grocery);

}
