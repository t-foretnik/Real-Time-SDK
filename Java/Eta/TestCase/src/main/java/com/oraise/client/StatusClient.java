package com.oraise.client;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.refinitiv.ema.access.OmmConsumerEvent;
import com.refinitiv.ema.access.OmmState;
import com.refinitiv.ema.access.OmmState.DataState;
import com.refinitiv.ema.access.RefreshMsg;
import com.refinitiv.ema.access.StatusMsg;

public class StatusClient extends NoopClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusClient.class);

    private int lastKnownState = DataState.SUSPECT;
    private final CompletableFuture<Void> fStartup;

    public StatusClient(CompletableFuture<Void> fStartUp) {
        this.fStartup = fStartUp;
    }

    @Override
    public void onRefreshMsg(RefreshMsg refreshMsg, OmmConsumerEvent consumerEvent) {
        LOGGER.debug("Received refreshMessage");
        handleState(refreshMsg.state());
    }

    @Override
    public void onStatusMsg(StatusMsg statusMsg, OmmConsumerEvent consumerEvent) {
        if (!statusMsg.hasState()) {
            LOGGER.debug("Ignoring status message without state");
            return;
        }
        handleState(statusMsg.state());
    }

    private void handleState(OmmState state) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received status message: Code: {}, StreamState: {}, DataState: {}, Text: {}",
                    state.codeAsString(), state.streamStateAsString(), state.dataStateAsString(), state.statusText());
        }
        switch (state.dataState()) {
        case DataState.OK:
            signalOk();
            break;
        case DataState.SUSPECT:
            signalSuspect(state.streamState());
            break;
        case DataState.NO_CHANGE:
        default:
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Received data state: {}", state.dataStateAsString());
            }
        }
    }

    private void signalOk() {

        switch (lastKnownState) {
        case DataState.SUSPECT:
            if (fStartup.isDone()) {
                LOGGER.info("Data state is OK again");
            } else {
                LOGGER.info("Successfully connected. Data state is OK");
                fStartup.complete(null);
            }
            lastKnownState = DataState.OK;
            break;
        case DataState.OK:
        default:
            LOGGER.debug("State is unchanged OK");
            break;
        }
    }

    private void signalSuspect(int streamState) {
        switch (lastKnownState) {
        case DataState.OK:
            if (!fStartup.isDone()) {
                LOGGER.error("Connection on startup is in SUSPECT state. Stream state is: {}", streamState);
            }

            lastKnownState = DataState.SUSPECT;
            break;
        case DataState.SUSPECT:
        default:
            LOGGER.debug("State is unchanged SUSPECT");
            break;
        }
    }
}
