package com.oraise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;

import com.oraise.exception.SnapshotException;
import com.oraise.exception.SubscriptionException;
import com.oraise.model.Snapshot;
import com.refinitiv.ema.access.AckMsg;
import com.refinitiv.ema.access.GenericMsg;
import com.refinitiv.ema.access.Msg;
import com.refinitiv.ema.access.OmmConsumerClient;
import com.refinitiv.ema.access.OmmConsumerEvent;
import com.refinitiv.ema.access.RefreshMsg;
import com.refinitiv.ema.access.StatusMsg;
import com.refinitiv.ema.access.UpdateMsg;

/**
 * This is a minimum trivial example how the main loop can be broken.
 * 
 * Note: However client implementors should care not to throw unchecked
 * exceptions, but this is merely an example. The
 * {@link ChainConstituentDataTest} demonstrates the same problem without
 * deliberately raising exceptions on its own
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
class FaultyClientTest extends TestBase {

    private static class IWillBreakYourMainLoop extends RuntimeException {
        private static final long serialVersionUID = 2442710065722145914L;

        public IWillBreakYourMainLoop(String msg) {
            super(msg);
        }
    }

    private class FaultyClient implements OmmConsumerClient {

        private CountDownLatch countdown = new CountDownLatch(1);

        private void raiseException(String origin) {
            countdown.countDown();
            throw new IWillBreakYourMainLoop("Exception raised from '" + origin + "'!");
        }

        @Override
        public void onRefreshMsg(RefreshMsg refreshMsg, OmmConsumerEvent consumerEvent) {
            raiseException("onRefreshMsg");
        }

        @Override
        public void onUpdateMsg(UpdateMsg updateMsg, OmmConsumerEvent consumerEvent) {
            raiseException("onUpdateMsg");
        }

        @Override
        public void onStatusMsg(StatusMsg statusMsg, OmmConsumerEvent consumerEvent) {
            raiseException("onStatusMsg");
        }

        @Override
        public void onGenericMsg(GenericMsg genericMsg, OmmConsumerEvent consumerEvent) {
            raiseException("onGenericMsg");
        }

        @Override
        public void onAckMsg(AckMsg ackMsg, OmmConsumerEvent consumerEvent) {
            raiseException("onAckMsg");
        }

        @Override
        public void onAllMsg(Msg msg, OmmConsumerEvent consumerEvent) {
            raiseException("onAllMsg");
        }

    }

    @Test
    void testFaultyClient() {

        final Snapshot firstSnapshot;
        try {
            firstSnapshot = getSnapshot(CONTINENTAL_DELAYED);
        } catch (SnapshotException se) {
            fail(se.getMessage());
            return;
        }

        assertNotNull(firstSnapshot);
        assertEquals(CONTINENTAL_DELAYED, firstSnapshot.identifier());
        assertNotNull(firstSnapshot.data());
        assertFalse(firstSnapshot.data().isEmpty());

        FaultyClient client = new FaultyClient();
        assertThrows(SubscriptionException.class, () -> subscribeWithFaultyClient(CONTINENTAL_DELAYED, client));
        assertEquals(0, client.countdown.getCount());

        // main loop will be broken by now, next snapshot request should fail
        assertThrows(SnapshotException.class, () -> getSnapshot(CONTINENTAL_DELAYED));

    }

}
