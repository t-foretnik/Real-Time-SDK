package com.oraise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oraise.exception.ConfigurationException;
import com.oraise.exception.SnapshotException;
import com.oraise.model.Snapshot;

/**
 * Test to prove that the snapshot mechanics work
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
class SnapShotTest extends TestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnapShotTest.class);

    @Test
    void testSnapshot() {
        final Snapshot snapshot;
        try {
            snapshot = getSnapshot(CONTINENTAL_DELAYED);
        } catch (SnapshotException se) {
            fail(se.getMessage());
            return;
        }

        assertNotNull(snapshot);
        assertTrue(snapshot.success());
        assertEquals(CONTINENTAL_DELAYED, snapshot.identifier());
        assertNotNull(snapshot.data());
        assertFalse(snapshot.data().isEmpty());

        LOGGER.debug("Received data for [{}]: {}", snapshot.identifier(), snapshot.data());
    }

    @Test
    void testSnapshotAsync() {

        Snapshot snapshot = null;
        try {
            snapshot = getSnapshotAsync(CONTINENTAL_DELAYED).get(TEST_CONFIG.snapshotTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (ConfigurationException ce) {
            fail("Setup snapshot timeout properly!");
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            fail("Interrupted while waiting for snapshot response!");
        } catch (Exception e) {
            fail(e.getMessage());
        }

        assertNotNull(snapshot);
        assertTrue(snapshot.success());
        assertEquals(CONTINENTAL_DELAYED, snapshot.identifier());
        assertNotNull(snapshot.data());
        assertFalse(snapshot.data().isEmpty());

        LOGGER.debug("Received async data for [{}]: {}", snapshot.identifier(), snapshot.data());
    }

}
