package com.oraise;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oraise.exception.SnapshotException;

/**
 * Test to prove that chain resolution mechanics work
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
class ChainResolutionTest extends TestBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChainResolutionTest.class);

    @Test
    void resolveChain() {
        try {
            Set<String> constituents = resolveChain(CHAIN_GDAXI_DELAYED);

            assertNotNull(constituents);
            assertFalse(constituents.isEmpty());
            LOGGER.debug("Resolved [{}] to {}", CHAIN_GDAXI_DELAYED, constituents);
        } catch (SnapshotException se) {
            fail(se.getMessage());
            return;
        }

    }

    @Test
    void resolveChainAsync() {
        CompletableFuture<Set<String>> fConstituents = resolveChainAsync(CHAIN_GDAXI_DELAYED);

        try {
            Set<String> constituents = fConstituents.get(600, TimeUnit.SECONDS);

            assertNotNull(constituents);
            assertFalse(constituents.isEmpty());
            LOGGER.debug("Resolved [{}] asynchronously to {}", CHAIN_GDAXI_DELAYED, constituents);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            fail("Interrupted while waiting for chain to be resolved!");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
