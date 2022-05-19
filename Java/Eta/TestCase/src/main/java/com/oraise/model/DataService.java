package com.oraise.model;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.oraise.exception.SnapshotException;
import com.oraise.exception.SubscriptionException;
import com.refinitiv.ema.access.OmmConsumerClient;

public interface DataService {

    void subscribe(String identifier, Consumer<Data> dataConsumer, Integer... fids) throws SubscriptionException;

    void unsubscribe(String identifier);

    Subscription subscribeWithFaultyClient(String identifer, OmmConsumerClient client) throws SubscriptionException;

    Subscription subscribe(String identifier, Integer... fids) throws SubscriptionException;

    Subscription subscribe(String identifier, Runnable callableClosure, Integer... fids) throws SubscriptionException;

    Snapshot getSnapshot(String identifier) throws SnapshotException;

    CompletableFuture<Snapshot> getSnapshotAsync(String identifier);

    CompletableFuture<Set<String>> resolveChainAsync(String chain);

    Set<String> resolveChain(String chain) throws SnapshotException;

}
