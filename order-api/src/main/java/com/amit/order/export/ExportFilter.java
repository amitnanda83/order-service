package com.amit.order.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Defines the filter which controls the data to be exported.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportFilter {

    /**
     * Date for which data needs to be exported
     */
    private Date date;

    /**
     * Product for which data needs to be exported
     */
    private String product;
}
