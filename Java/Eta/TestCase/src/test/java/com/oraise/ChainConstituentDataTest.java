package com.oraise;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oraise.model.Snapshot;

/**
 * Test to show that {@link com.refinitiv.ema.access.OmmBaseImpl#run} method
 * will terminate the worker thread when encountering a {@link RuntimeException}
 * like i.e. {@link NullPointerException}.
 * 
 * The root cause in this test case is that
 * {@link com.refinitiv.ema.access.ItemCallbackClient.CallbackClient._eventImpl._item}
 * is returned to pool (and _client is set to <code>null</code>)
 * <strong>BEFORE</strong> the initial image
 * ({@link com.refinitiv.ema.access.RefreshMsg} is handled. When the message is
 * handled
 * {@link com.refinitiv.ema.access.ItemCallbackClient.CallbackClient._eventImpl._item#client()}
 * returns <code>null</code> and the {@link NullPointerException} is thrown
 * which breaks the Thread executors main loop.
 * 
 * Note: When limiting the amount of parallel snapshots to 4 or less the error
 * does not occur, but this is not about asynchronous processing it should
 * merely show that the dispatch mechanics are prone to unchecked exceptions,
 * which will render the process useless.
 * 
 * Most unfortunate there is no feedback to api users.
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
class ChainConstituentDataTest extends TestBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChainConstituentDataTest.class);

    @Test
    void testChainConstituentDataLimitedTo4Success() {
        try {
            List<Snapshot> constituentsSnapshots = retrieveData(4).get(TEST_CONFIG.snapshotTimeoutSeconds() * 2,
                    TimeUnit.SECONDS);
            assertNotNull(constituentsSnapshots);
            assertFalse(constituentsSnapshots.isEmpty());
            constituentsSnapshots.forEach(snapshot -> {
                assertNotNull(snapshot);
                assertNotNull(snapshot.identifier());
                assertNotNull(snapshot.data());
                assertFalse(snapshot.data().isEmpty());
                LOGGER.debug("Received data for [{}]: {}", snapshot.identifier(), snapshot.data());
            });
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            fail("Interrupted while fetching snapshots for chain!");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testChainConstituentDataNoLimitFail() {
        assertThrows(TimeoutException.class,
                () -> retrieveData(0).get(TEST_CONFIG.snapshotTimeoutSeconds() * 2, TimeUnit.SECONDS));

    }

    private CompletableFuture<List<Snapshot>> retrieveData(int limit) {
        CompletableFuture<List<Snapshot>> fConstituentData = new CompletableFuture<>();

        resolveChainAsync(CHAIN_GDAXI_DELAYED).whenComplete((constituents, error) -> {
            if (error != null) {
                fConstituentData.completeExceptionally(error);
            } else {
                LOGGER.debug("Resolved {} constituent(s) for chain [{}]: {}", constituents.size(), CHAIN_GDAXI_DELAYED,
                        constituents);
                Map<String, CompletableFuture<Snapshot>> fSnapshots = new HashMap<>();
                for (String constituent : constituents) {
                    fSnapshots.put(constituent, getSnapshotAsync(constituent));
                    if (limit > 0 && fSnapshots.size() >= limit) {
                        break;
                    }
                }

                CompletableFuture.allOf(fSnapshots.values().toArray(new CompletableFuture[fSnapshots.size()]))
                        .whenComplete((state, ignored) -> {
                            final List<Snapshot> snapshots = new ArrayList<>();

                            // extract results
                            fSnapshots.entrySet().forEach(fSnapshot -> {
                                try {
                                    // Since the composite future is done all child futures are also done
                                    // However it is not guaranteed that the did not finish exceptionally
                                    Snapshot snapshot = fSnapshot.getValue().getNow(null);
                                    if (snapshot != null) {
                                        LOGGER.debug("Received data for [{}]", fSnapshot.getKey());
                                        snapshots.add(snapshot);
                                    } else {
                                        LOGGER.debug("Received no data for [{}]", fSnapshot.getKey());
                                    }
                                } catch (Exception e) {
                                    LOGGER.debug("Received exception for snapshot of [{}]: {}", fSnapshot.getKey(),
                                            e.getMessage());
                                }
                            });

                            fConstituentData.complete(snapshots);
                        });
            }
        });

        return fConstituentData;
    }

}
