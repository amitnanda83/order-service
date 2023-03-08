package com.amit.order.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request definig the data to be exported
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequest {

    /**
     * Identifier for the export job
     */
    private String key;

    /**
     * Format in whcih data needs to be exported
     */
    private String format;

    /**
     * Filters to control the data to be exported
     */
    private ExportFilter filter;
}
