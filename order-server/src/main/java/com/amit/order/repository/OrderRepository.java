package com.amit.order.repository;

import com.amit.order.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Interface to interact with Mongo for `order` collection
 */
public interface OrderRepository extends MongoRepository<Order, String> {
}
