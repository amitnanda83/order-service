package com.amit.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "order")
public class Order {

    @Id
    private String id;

    @Indexed
    private long orderDate;

    @Indexed
    private String productName;

    private String customerName;
}
