package com.amit.order.pipeline;

import com.amit.order.batch.StatusResponder;
import com.amit.order.export.ExportRequest;
import com.amit.order.service.OrderService;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * This class defines and controls the data <i>collection</i> and <i>writing</i> of data.
 * <p>
 * The data flow is achieved by chaining <i>consumers</i>, each of which handles a specific task thus creating a
 * <b>data pipeline</b>
 * </p>
 */
@Slf4j
public class DataPipeline {

    /**
     * Identifier of the pipeline
     */
    private final String jobName;

    /**
     * List of consumers part of the pipeline
     */
    @Getter
    List<DataConsumer<DataBuffer>> consumers = Lists.newArrayList();

    public DataPipeline(String jobName, OrderService orderService, StatusResponder statusResponder,
            ExportRequest request) {

        this.jobName = jobName;

        MongoReaderConsumer readerConsumer = new MongoReaderConsumer(jobName, 1, 100, orderService, request);
        CsvWriterConsumer writerConsumer = new CsvWriterConsumer(jobName, 1, 100, statusResponder);

        readerConsumer.setNextInChain(writerConsumer);
        writerConsumer.setNextInChain(readerConsumer);

        consumers.addAll(Arrays.asList(readerConsumer, writerConsumer));
        consumers.forEach(DataConsumer::initialize);
    }

    public void start() {
        consumers.forEach(DataConsumer::start);
        log.info("Started Data Pipeline for {}", jobName);
    }
}
