package com.amit.order.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Object to store a time range for which order export needs to be done.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Date {

    /**
     * Till when data should be exported
     */
    private long to;

    /**
     * From when data should be exported
     */
    private long from;
}
