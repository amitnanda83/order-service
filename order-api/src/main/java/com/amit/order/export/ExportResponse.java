package com.amit.order.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response of the export job.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportResponse {

    /**
     * Identified for the export job
     */
    private String id;

    /**
     * Status of the export job
     */
    private Status status;

    /**
     * Path where the data is exported
     */
    private String filePath;
}
