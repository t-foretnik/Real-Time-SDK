package com.oraise.client;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oraise.model.Snapshot;
import com.refinitiv.ema.access.OmmConsumerEvent;
import com.refinitiv.ema.access.RefreshMsg;
import com.refinitiv.ema.access.StatusMsg;
import com.refinitiv.ema.rdm.DataDictionary;

public class SnapshotClient extends AbstractMarketDataClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnapshotClient.class);

    private final CompletableFuture<Snapshot> fSnapshot;

    public SnapshotClient(String identifier, DataDictionary dataDictionary, CompletableFuture<Snapshot> fSnapshot) {
        super(identifier, dataDictionary);
        this.fSnapshot = fSnapshot;
    }

    @Override
    public void onStatusMsg(StatusMsg statusMsg, OmmConsumerEvent consumerEvent) {

        try {
            // if we receive a status message instead of refresh it's an error
            if (!fSnapshot.isDone()) {
                final String errorMsg;
                if (statusMsg.hasState()) {
                    errorMsg = statusMsg.state().statusText();
                } else {
                    errorMsg = "Failed for unknown reason";
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("[{}] Snapshot request failed: {}", identifier(), errorMsg);
                }
                fSnapshot.complete(new SnapshotImpl(identifier()));
            }
        } catch (Exception e) {
            LOGGER.error("[{}] Failed while handling status msg: {}", this, e.getMessage());
        }
    }

    @Override
    public void onRefreshMsg(RefreshMsg refreshMsg, OmmConsumerEvent consumerEvent) {
        try {
            if (!fSnapshot.isDone()) {
                SnapshotImpl snapshot = new SnapshotImpl(identifier(), unpack(refreshMsg.payload().fieldList()));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("[{}] Snapshot succeeded", identifier());
                }
                fSnapshot.complete(snapshot);
            } else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("[{}] Snapshot response was too late", identifier());
            }
        } catch (Exception e) {
            LOGGER.error("[{}] Failed while handling refresh msg: {}", identifier(), e.getMessage());
        }
    }

}
