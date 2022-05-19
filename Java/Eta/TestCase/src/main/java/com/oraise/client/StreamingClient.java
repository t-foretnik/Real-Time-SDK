package com.oraise.client;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oraise.model.Data;
import com.refinitiv.ema.access.FieldList;
import com.refinitiv.ema.access.OmmConsumerEvent;
import com.refinitiv.ema.access.RefreshMsg;
import com.refinitiv.ema.access.StatusMsg;
import com.refinitiv.ema.access.UpdateMsg;
import com.refinitiv.ema.rdm.DataDictionary;

public class StreamingClient extends AbstractMarketDataClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingClient.class);

    private final Consumer<Data> dataConsumer;
    private final CompletableFuture<String> fStatus;

    public StreamingClient(String identifier, DataDictionary dataDictionary, Consumer<Data> dataConsumer,
            CompletableFuture<String> fStatus) {
        super(identifier, dataDictionary);
        this.fStatus = fStatus;
        this.dataConsumer = dataConsumer;
    }

    @Override
    public void onStatusMsg(StatusMsg statusMsg, OmmConsumerEvent consumerEvent) {
        // if we receive a status message instead of refresh it's an error
        if (!fStatus.isDone()) {
            final String errorMsg;
            if (statusMsg.hasState()) {
                errorMsg = statusMsg.state().statusText();
            } else {
                errorMsg = "Failed for unknown reason";
            }
            LOGGER.debug("[{}] received status: {}", identifier(), errorMsg);
            fStatus.complete(errorMsg);
        }
    }

    @Override
    public void onRefreshMsg(RefreshMsg refreshMsg, OmmConsumerEvent consumerEvent) {

        if (!fStatus.isDone()) {
            fStatus.complete("OK");
        }

        consumeData(refreshMsg.payload().fieldList());
        callClosure(consumerEvent.closure());
    }

    @Override
    public void onUpdateMsg(UpdateMsg updateMsg, OmmConsumerEvent consumerEvent) {
        consumeData(updateMsg.payload().fieldList());
        callClosure(consumerEvent.closure());
    }

    private void callClosure(Object closure) {
        if (closure instanceof Runnable) {
            ((Runnable) closure).run();
        }
    }

    private void consumeData(FieldList fieldList) {
        dataConsumer.accept(new DataImpl(identifier(), unpack(fieldList)));
    }
}
