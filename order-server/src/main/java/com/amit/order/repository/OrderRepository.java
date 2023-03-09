package com.amit.order.repository;

import com.amit.order.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface to interact with Mongo for `order` collection
 */
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    @Query("{productName : ?0, $and : [{ orderDate : { $gte : ?1, $lt : ?2}}]}")
    List<Order> findData(String product, long from, long to, Pageable pageable);
}
