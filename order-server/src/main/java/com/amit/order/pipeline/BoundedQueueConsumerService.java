package com.amit.order.pipeline;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

@Slf4j
@Getter
public class BoundedQueueConsumerService<T> implements AutoCloseable {

    /**
     * Controls the number of dispatches we initiate concurrently.
     */
    private final int concurrency;

    /**
     * The outbound queue.
     */
    private final BlockingQueue<T> queue;

    /**
     * The item consumer
     */
    private final Consumer<T> consumer;

    /**
     * The dispatch executor.
     */
    private final ExecutorService executor;

    public BoundedQueueConsumerService(BlockingQueue<T> queue, Consumer<T> consumer, ExecutorService executor,
            int concurrency) {
        this.queue = queue;
        this.consumer = consumer;
        this.executor = executor;
        this.concurrency = concurrency;
    }

    /**
     * Accept another item for eventual dispatch.
     *
     * @param offering An item
     * @return {@code true} if the queue has room, {@code false} otherwise.
     */
    public boolean offer(T offering) {
        boolean accepted = false;

        // If running, count and offer to the queue.
        if (!executor.isShutdown()) {
            accepted = queue.offer(offering);
        }

        // If the policy says wait, block the thread.
        if (!accepted) {
            accepted = waitForAcceptance(offering);
        }

        // If not queued, count as rejected
        if (!accepted) {
            log.error("Failed to queue work");
        }

        return accepted;
    }


    /**
     * Wait for the offering to be accepted. This blocks the caller of {@link #offer(Object)} method and is only
     * used when blocking-wait policy is in effect.
     *
     * @param offering The item
     * @return {@code true} if the item was queued
     */
    private boolean waitForAcceptance(T offering) {

        try {
            // While our offer is not accepted - keep trying
            while (!getQueue().offer(offering, 1, TimeUnit.MILLISECONDS)) {
                log.debug("Delaying {}", offering);
            }
        }
        catch (InterruptedException e) {

            // Keep the thread interrupt status
            Thread.currentThread().interrupt();

            // Reject the one in hand
            return false;
        }

        return true;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startDispatch() {

        // Initiate the given number of dispatches
        IntStream.range(0, concurrency).forEach(i -> runAsync(this::proceed, executor));
    }

    /**
     * Take the next item, forward it, repeat.
     */
    public void proceed() {
        if (!executor.isShutdown()) {
            supplyAsync(this::take, executor).
                    thenApply(this::forward)
                    .thenRun(this::proceed);
        }
    }

    /**
     * Take an item from the queue.
     */
    public T take() {
        try {
            return queue.take();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    /**
     * Forward the next item to the consumer.
     */
    private T forward(T next) {
        try {
            // Forward it to the dispatcher
            log.debug("Forwarding {}", next);
            consumer.accept(next);

            // Success, update meter.
            log.debug("Successfully forwarded {}", next);
        }
        catch (Exception e) {
            // Forward failed, count it as dropped.
            log.warn("Failed to forward {}", next, e);
        }
        return next;
    }

    /**
     * Shutdown the service and drain all queued items.
     */
    @Override
    @PreDestroy
    public void close() throws Exception {

        int leftOvers = queue.size();

        // Drain the items we accepted.
        List<T> pending = new ArrayList<>(leftOvers);
        leftOvers = queue.drainTo(pending);
        log.info("Forwarding {} left over items before shutting down service", leftOvers);
        pending.forEach(this::forward);
    }
}
