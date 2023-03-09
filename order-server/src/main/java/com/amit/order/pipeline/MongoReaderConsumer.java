package com.amit.order.pipeline;

import com.amit.order.export.ExportRequest;
import com.amit.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

/**
 * Consumer to read data from mongo
 */
@Slf4j
public class MongoReaderConsumer extends DataConsumer<DataBuffer> {

    /**
     * Page number to read
     */
    private int page = 0;

    /**
     * Request having details for data to export
     */
    private final ExportRequest request;

    /**
     * Interface to read data from mongo
     */
    private final OrderService orderService;

    /**
     * Create a consumer capable to reading data from mongo
     */
    public MongoReaderConsumer(String identity, int threadCount, int queueSize, OrderService orderService,
            ExportRequest request) {

        super("mongo-reader-" + identity, threadCount, queueSize);

        this.request = request;
        this.orderService = orderService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {

        log.info("Starting consumer for {}", this.getIdentity());
        IntStream.range(0, 10).mapToObj(counter -> new DataBuffer(100)).forEach(this::offer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {

        log.info("Initializing consumer for {}", this.getIdentity());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void consume(DataBuffer data) {

        log.info("Consuming data for {}", this.getIdentity());
        data.getOrders().addAll(orderService.getRecords(request.getFilter(), 1000, page++));
    }
}
