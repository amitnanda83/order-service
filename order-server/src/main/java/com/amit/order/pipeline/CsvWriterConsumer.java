package com.amit.order.pipeline;

import com.amit.order.batch.StatusResponder;
import com.amit.order.entity.Order;
import com.amit.order.export.Status;
import com.amit.order.util.JobParamUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Consumer to write data to a csv file
 */
@Slf4j
public class CsvWriterConsumer extends DataConsumer<DataBuffer> {

    /**
     * Printer to write csv data
     */
    private CSVPrinter csvPrinter;

    /**
     * Interface to inform about status to running job
     */
    private final StatusResponder statusResponder;

    /**
     * Store params for the job getting executed
     */
    Map<String, String> params = Maps.newHashMap();

    /**
     * Create a new consumer to write collected data to a CSV file
     */
    public CsvWriterConsumer(String identity, int threadCount, int queueSize, StatusResponder statusResponder) {
        super("csv-writer-" + identity, threadCount, queueSize);

        this.statusResponder = statusResponder;

        params.put("key", identity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {

        log.info("Starting consumer for {}", this.getIdentity());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {

        log.info("Initializing consumer for {}", this.getIdentity());

        String name = "pipeline-" + System.nanoTime();
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(JobParamUtil.getFilePath(name, "csv")));
            csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            csvPrinter.printRecord("id", "productName", "customerName", "orderDate");
            params.put("path", JobParamUtil.getFilePath(name, "csv"));
        }
        catch (IOException e) {
            log.error("Failed to initialize csv-writer with error : {}", e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void consume(DataBuffer data) {

        log.info("Consuming data for {}", this.getIdentity());
        log.info("Received data of size {}", data.getOrders().size());

        if (!data.getOrders().isEmpty()) {
            try {
                writeToFile(data);
                statusResponder.sendStatus(params, Status.IN_PROGRESS);
            }
            catch (IOException e) {
                log.error("Failed to write data with error : {}", e.getMessage());
            }
        }
        else {
            stop();
            statusResponder.sendStatus(params, Status.COMPLETED);
        }
    }

    /**
     * Helper function to write a data to a file
     */
    private void writeToFile(DataBuffer data) throws IOException {

        for (Order order : data.getOrders()) {
            csvPrinter.printRecord(order.getId(), order.getProductName(), order.getCustomerName(),
                    order.getOrderDate());
        }

        csvPrinter.flush();
        data.getOrders().clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        try {
            super.stop();
        }
        catch (Exception e) {
            log.error("Error stopping the stream", e);
        }
    }
}
