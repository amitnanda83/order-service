package com.amit.order.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequest {

    private String key;

    private String format;

    private ExportFilter filter;
}
