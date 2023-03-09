package com.amit.order.util;

import com.amit.order.export.ExportRequest;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.io.File;
import java.util.Date;
import java.util.Map;

import static java.lang.System.getProperty;

/**
 * Helper class to create {@link JobParameters} from the export request received
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JobParamUtil {

    private final static String QUOTE = "\"";

    /**
     * Create {@link JobParameters} from request received
     */
    public static JobParameters createParams(ExportRequest request) {

        return new JobParametersBuilder()
                .addDate("date", new Date())
                .addString("key", request.getKey())
                .addString("type", request.getFormat())
                .addLong("to", request.getFilter().getDate().getTo())
                .addLong("from", request.getFilter().getDate().getFrom())
                .addString("path", getFilePath(request.getKey(), request.getFormat()))
                .addString("product", QUOTE + request.getFilter().getProduct() + QUOTE)
                .toJobParameters();
    }

    public static String getFilePath(String name, String format) {
        return getProperty("user.dir") + File.separator + "output" + File.separator + name + "." + format;
    }

    public static Map<String, String> convertToMap(JobParameters params) {

        Map<String, String> entries = Maps.newHashMap();
        params.getParameters().forEach((key, value) -> entries.put(key, value.toString()));

        return entries;
    }
}
