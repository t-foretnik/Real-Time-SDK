package com.oraise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oraise.model.Data;
import com.oraise.model.Subscription;

/**
 * Test to prove that basic subscription mechanics work Merely relying on the
 * initial image to be received in time
 * 
 *
 * @author Tobias Foretnik, oraise GmbH
 * 
 *         <pre>
 *  Mary-Somerville-Stra&szlig;e 10,
 *  28359 Bremen,
 *  Germany
 *  &copy;2010-2022 oraise GmbH
 *         </pre>
 *         <p>
 *         All rights reserved. Redistribution and use in source and binary
 *         forms, with or without modification, is not permitted unless
 *         explicitly done so in writing by oraise GmbH.
 *         </p>
 */
class SubscriptionTest extends TestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionTest.class);

    private static final class CountingConsumer implements Consumer<Data> {

        private final List<Data> data;
        private final CountDownLatch counter;
        private final int awaitedUpdates;

        private CountingConsumer(int awaitedUpdates) {
            this.counter = new CountDownLatch(awaitedUpdates);
            this.awaitedUpdates = awaitedUpdates;
            this.data = new ArrayList<>();
        }

        public void accept(Data t) {
            data.add(t);
            counter.countDown();
        };

        public void await(int waitSeconds) {
            SubscriptionTest.await(counter, waitSeconds, awaitedUpdates);
        }
    }

    @Test
    void testSubscribe() {

        CountingConsumer consumer = new CountingConsumer(1);

        try {
            subscribe(CONTINENTAL_DELAYED, consumer, 22, 25);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        consumer.await(15);
        assertFalse(consumer.data.isEmpty());
        consumer.data.forEach(d -> {
            assertNotNull(d);
            assertEquals(CONTINENTAL_DELAYED, d.identifier());
            assertNotNull(d.data());
            assertFalse(d.data().isEmpty());
            LOGGER.debug("Receiced data: {}", d.data());
        });

        unsubscribe(CONTINENTAL_DELAYED);
    }

    @Test
    void testSubscription() {
        Subscription stored = null;

        final CountDownLatch countDown = new CountDownLatch(1);

        try (Subscription s = subscribe(CONTINENTAL_DELAYED, countDown::countDown, 22, 25)) {
            stored = s;
            assertFalse(s.isClosed());
            assertEquals(CONTINENTAL_DELAYED, s.identifier());
            await(countDown, 10, 1);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        assertNotNull(stored);
        assertTrue(stored.isClosed());
        assertNotNull(stored.datas());
        assertFalse(stored.datas().isEmpty());
        stored.datas().forEach(d -> {
            assertNotNull(d);
            assertEquals(CONTINENTAL_DELAYED, d.identifier());
            assertNotNull(d.data());
            assertFalse(d.data().isEmpty());
            LOGGER.debug("Received data: {}", d.data());
        });

    }

    private static void await(CountDownLatch counter, int waitSeconds, int expected) {
        try {
            assertTrue(counter.await(waitSeconds, TimeUnit.SECONDS));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            fail("Interrupted while waiting for at least " + expected + ", current count is " + counter.getCount());
        }
    }
}
