package com.amit.order.pipeline;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * This is the common base class for consumers to be created for a {@link DataPipeline}
 *
 * @param <T> Type of data a consumer need to work on
 */
@Slf4j
public abstract class DataConsumer<T> {

    /**
     * Identity of the consumer
     */
    @Getter
    protected final String identity;

    /**
     * The next consumer to receive in the data in the chain
     */
    protected DataConsumer<T> nextConsumer;

    /**
     * The thread executor for the consumer action
     */
    protected ExecutorService executor;

    /**
     * The queue/thread/consumer component
     */
    protected BoundedQueueConsumerService<T> service;

    public DataConsumer(String identity, int threadCount, int queueSize) {

        this.identity = identity;

        // Create the thread factory
        ThreadFactory tfb = new ThreadFactoryBuilder().setNameFormat(identity + "-%d").build();

        // Create the executor for the consumer with the number of threads asked for
        this.executor = Executors.newFixedThreadPool(threadCount, tfb);

        // Initialize the queue for the consumer with the size required
        BlockingQueue<T> queue = new ArrayBlockingQueue<>(queueSize);

        // Create the service
        this.service = new BoundedQueueConsumerService<>(queue, this::accept, executor, threadCount);
        service.startDispatch();
    }

    /**
     * Consumer function for the {@link BoundedQueueConsumerService}. This function will be invoked when there is
     * something to work with.
     * <p>
     * Post data is processed by this consumer, that is passed to the next consumer in the pipeline for it to consume.
     * </p>
     *
     * @param data to be processed by this consumer
     */
    private void accept(T data) {

        consume(data);

        // Current consumer is done with the data, pass the data to the next one
        Optional.ofNullable(nextConsumer).ifPresent(consumer -> consumer.offer(data));
    }

    /**
     * Offer the item to the next consumer. Each consumer invokes this once it has consumed the data
     */
    public void offer(T data) {

        service.offer(data);
    }

    /**
     * Interface to set the next consumer in the chain.
     * <p>
     * Once the current consumer accept and process the data, in passes that on to {@link #nextConsumer} that has to
     * work on the data.
     * </p>
     *
     * @param consumer to which data is sent by the current consumer
     */
    public void setNextInChain(DataConsumer<T> consumer) {

        this.nextConsumer = consumer;
    }

    /**
     * Stop the consumer. All the work to be done is complete, stop the consumer.
     */
    public void stop() {

        log.info("Stopping pipeline : {}", getIdentity());

        try {
            executor.shutdownNow();
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        }
    }

    /**
     * Start the consumer
     */
    public abstract void start();

    /**
     * Prepare the consumer.
     * <p>
     * Here we should be initialize anything that we might need to the consumer to be ready to work
     * </p>
     */
    public abstract void initialize();

    /**
     * Each consumer implements this how data need to be consumed by it.
     *
     * @param data to be consumed
     */
    protected abstract void consume(T data);
}
