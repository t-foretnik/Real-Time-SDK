package com.oraise;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oraise.client.SnapshotClient;
import com.oraise.client.StatusClient;
import com.oraise.client.StreamingClient;
import com.oraise.exception.ConfigurationException;
import com.oraise.exception.SnapshotException;
import com.oraise.exception.SubscriptionException;
import com.oraise.model.Data;
import com.oraise.model.DataService;
import com.oraise.model.Snapshot;
import com.oraise.model.Subscription;
import com.oraise.util.ConfigUtility;
import com.oraise.util.TestConfig;
import com.refinitiv.ema.access.ChannelInformation;
import com.refinitiv.ema.access.EmaFactory;
import com.refinitiv.ema.access.OmmConsumer;
import com.refinitiv.ema.access.OmmConsumerClient;
import com.refinitiv.ema.access.OmmConsumerConfig;
import com.refinitiv.ema.access.ReqMsg;
import com.refinitiv.ema.rdm.DataDictionary;
import com.refinitiv.ema.rdm.EmaRdm;

public abstract class TestBase implements DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestBase.class);

    // identifiers used in multiple tests
    protected static final String CONTINENTAL_DELAYED = "/CONG.DE";
    protected static final String CHAIN_GDAXI_DELAYED = "/.GDAXI";

    // chain related constants
    private static final String CHAIN_FIRST_PAGE = "0#";
    public static final List<Integer> CHAIN_LINKS_FIDS = Collections
            .unmodifiableList(Arrays.asList(800, 801, 802, 803, 804, 805, 806, 807, 808, 809, 810, 811, 812, 813));

    private static final Set<Integer> CHAIN_FIELDS = new HashSet<>();
    private static final Integer CHAIN_NEXT_LINK = 815;
    static {
        CHAIN_FIELDS.addAll(CHAIN_LINKS_FIDS);
        CHAIN_FIELDS.add(CHAIN_NEXT_LINK);
    }

    // configuration parameters used through all tests
    protected static TestConfig TEST_CONFIG = TestConfig.instance();

    private OmmConsumer ommConsumer;
    private String serviceName;
    private DataDictionary dataDictionary;
    private final Map<String, Long> handlesByIdentifier = new ConcurrentHashMap<>();
    private Long statusHandle;

    private static class SubscriptionImpl implements Subscription, Consumer<Data> {

        private final String identifier;
        private final Runnable unsubscribeCallback;
        private final List<Data> datas;
        private boolean closed = false;

        private SubscriptionImpl(String identifier, Runnable unsubscribeCallback) {
            this.identifier = identifier;
            this.unsubscribeCallback = unsubscribeCallback;
            this.datas = Collections.synchronizedList(new ArrayList<>());
        }

        @Override
        public void accept(Data data) {
            this.datas.add(data);
        }

        @Override
        public void close() throws Exception {
            assertNotNull(unsubscribeCallback);
            unsubscribeCallback.run();
            closed = true;
        }

        @Override
        public String identifier() {
            return identifier;
        }

        @Override
        public List<Data> datas() {
            return new ArrayList<>(datas);
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

    }

    @BeforeEach
    public void setup() {
        try {
            serviceName = TEST_CONFIG.serviceName();
            ommConsumer = createOmmConsumer();

        } catch (ConfigurationException ce) {
            fail(ce.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        if (ommConsumer != null) {
            ommConsumer.unregister(statusHandle);
            Iterator<Entry<String, Long>> itHandles = handlesByIdentifier.entrySet().iterator();
            while (itHandles.hasNext()) {
                Entry<String, Long> e = itHandles.next();
                ommConsumer.unregister(e.getValue());
                itHandles.remove();
            }
            ommConsumer.uninitialize();
        }

        if (dataDictionary != null) {
            dataDictionary.clear();
            dataDictionary = null;
        }
        statusHandle = null;
        handlesByIdentifier.clear();
        serviceName = null;
    }

    @Override
    public Snapshot getSnapshot(String identifier) throws SnapshotException {
        assertNotNull(ommConsumer);
        assertNotNull(serviceName);
        CompletableFuture<Snapshot> fSnapshot = new CompletableFuture<>();
        final long handle = ommConsumer.registerClient(ConfigUtility.createSnapshotRequest(identifier, serviceName),
                new SnapshotClient(identifier, dataDictionary, fSnapshot));

        final Snapshot data;
        try {
            data = fSnapshot.get(TEST_CONFIG.snapshotTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (ConfigurationException ce) {
            throw new SnapshotException("Snapshot timeout is not configured properly!");
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new SnapshotException("Interrupted while waiting for snapshot response");
        } catch (Exception ex) {
            throw new SnapshotException(ex);
        } finally {
            ommConsumer.unregister(handle);
        }
        return data;
    }

    @Override
    public CompletableFuture<Snapshot> getSnapshotAsync(String identifier) {
        final CompletableFuture<Snapshot> fSnapshot = new CompletableFuture<>();
        CompletableFuture<Snapshot> inner = new CompletableFuture<>();
        final long handle = ommConsumer.registerClient(ConfigUtility.createSnapshotRequest(identifier, serviceName),
                new SnapshotClient(identifier, dataDictionary, inner));
        inner.whenComplete((result, ex) -> {
            LOGGER.debug("Async snapshot for [{}] finished", identifier);
            ommConsumer.unregister(handle);
            if (!fSnapshot.isDone()) {
                if (ex != null) {
                    fSnapshot.completeExceptionally(ex);
                } else {
                    fSnapshot.complete(result);
                }
            }
        });
        return fSnapshot;
    }

    @Override
    public Subscription subscribeWithFaultyClient(String identifier, OmmConsumerClient client)
            throws SubscriptionException {
        assertNotNull(ommConsumer);
        assertNotNull(serviceName);
        SubscriptionImpl s = new SubscriptionImpl(identifier, () -> this.unsubscribe(identifier));
        CompletableFuture<String> fStatus = new CompletableFuture<>();

        final long handle = ommConsumer.registerClient(
                ConfigUtility.createStreamingRequest(identifier, serviceName, Collections.emptyList()), client);

        final String status;
        try {
            status = fStatus.get(TEST_CONFIG.subscriptionTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            ommConsumer.unregister(handle);
            throw new SubscriptionException("Interrupted while waiting for subscription outcome!");
        } catch (Exception e) {
            ommConsumer.unregister(handle);
            throw new SubscriptionException(e);
        }

        LOGGER.debug("Received status: {}", status);
        this.handlesByIdentifier.put(identifier, handle);
        return s;
    }

    @Override
    public void subscribe(String identifier, Consumer<Data> dataConsumer, Integer... filterFids)
            throws SubscriptionException {
        assertNotNull(ommConsumer);
        assertNotNull(serviceName);
        CompletableFuture<String> fStatus = new CompletableFuture<>();
        final long handle = ommConsumer.registerClient(
                ConfigUtility.createStreamingRequest(identifier, serviceName, Arrays.asList(filterFids)),
                new StreamingClient(identifier, dataDictionary, dataConsumer, fStatus));

        final String status;
        try {
            status = fStatus.get(TEST_CONFIG.subscriptionTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            ommConsumer.unregister(handle);
            throw new SubscriptionException("Interrupted while waiting for subscription outcome!");
        } catch (Exception e) {
            ommConsumer.unregister(handle);
            throw new SubscriptionException(e);
        }
        LOGGER.debug("Received status: {}", status);
        this.handlesByIdentifier.put(identifier, handle);
    }

    @Override
    public Subscription subscribe(String identifier, Integer... filterFids) throws SubscriptionException {
        return subscribe(identifier, (Runnable) null, filterFids);
    }

    @Override
    public Subscription subscribe(String identifier, Runnable callableClosure, Integer... filterFids)
            throws SubscriptionException {
        assertNotNull(ommConsumer);
        assertNotNull(serviceName);
        SubscriptionImpl s = new SubscriptionImpl(identifier, () -> this.unsubscribe(identifier));
        CompletableFuture<String> fStatus = new CompletableFuture<>();

        final long handle = ommConsumer.registerClient(
                ConfigUtility.createStreamingRequest(identifier, serviceName, Arrays.asList(filterFids)),
                new StreamingClient(identifier, dataDictionary, s, fStatus), callableClosure);

        final String status;
        try {
            status = fStatus.get(TEST_CONFIG.subscriptionTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            ommConsumer.unregister(handle);
            throw new SubscriptionException("Interrupted while waiting for subscription outcome!");
        } catch (Exception e) {
            ommConsumer.unregister(handle);
            throw new SubscriptionException(e);
        }

        LOGGER.debug("Received status: {}", status);
        this.handlesByIdentifier.put(identifier, handle);
        return s;

    }

    @Override
    public void unsubscribe(String identifier) {
        assertNotNull(ommConsumer);
        Optional.ofNullable(handlesByIdentifier.remove(identifier)).ifPresent(ommConsumer::unregister);
    }

    @Override
    public Set<String> resolveChain(String chain) throws SnapshotException {
        assertNotNull(ommConsumer);
        assertNotNull(serviceName);

        final Set<String> data;
        try {
            data = resolveChainAsync(chain).get(TEST_CONFIG.snapshotTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (ConfigurationException ce) {
            throw new SnapshotException("Snapshot timeout used for chain resolutionis not configured properly!");
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new SnapshotException("Interrupted while waiting for chain resolution response");
        } catch (Exception ex) {
            throw new SnapshotException(ex);
        }
        return data;
    }

    @Override
    public CompletableFuture<Set<String>> resolveChainAsync(String chain) {
        assertNotNull(ommConsumer);
        assertNotNull(serviceName);
        final CompletableFuture<Set<String>> fChain = new CompletableFuture<>();
        doResoleChain(chain, CHAIN_FIRST_PAGE + chain, new HashSet<>(), fChain);
        return fChain;
    }

    private void doResoleChain(String chain, String page, Set<String> constituents,
            CompletableFuture<Set<String>> fChain) {
        CompletableFuture<Snapshot> fSnapshot = new CompletableFuture<>();
        ReqMsg request = ConfigUtility.createStreamingRequest(page, serviceName, CHAIN_FIELDS)
                .interestAfterRefresh(false);
        final long handle = ommConsumer.registerClient(request, new SnapshotClient(page, dataDictionary, fSnapshot));
        LOGGER.debug("Registered handle [{}] for page request of [{}]", handle, page);

        fSnapshot.whenComplete((pageResponse, ex) -> {
            // we do not want to mess around with the original result
            try {
                ommConsumer.unregister(handle);
                LOGGER.debug("Unregistering handle [{}] for page request of [{}]", handle, page);
            } catch (Exception e) {
                LOGGER.warn("Failed to unregister handle [{}] for page request [{}]: {}", handle, page, e.getMessage(),
                        e);
            }

            if (!fChain.isDone()) {
                handlePageResponse(pageResponse, ex, chain, constituents, fChain);
            }
        });
    }

    private void extractNextPageOrCompleteChainResolution(Snapshot pageResponse, String chain, Set<String> constituents,
            CompletableFuture<Set<String>> fChain) {
        // extract constituents
        for (Integer link : CHAIN_LINKS_FIDS) {
            Optional.ofNullable(pageResponse.data().get(link)).filter(constituent -> constituent != null &&
            // chains typically contain themselves so we have to test against
            // the chain name
                    !chain.equals(constituent))
                    // field content is simply the identifier name
                    .map(Object::toString).ifPresent(constituents::add);
        }

        // find "link" to next page ignoring it when it starts with '0#'
        // (which will mean we reached the end of the chain) or it's simply
        // not there which means basically the same
        String nextLink = Optional.ofNullable(pageResponse.data().get(CHAIN_NEXT_LINK)).map(Object::toString)
                .filter(next -> !next.startsWith(CHAIN_FIRST_PAGE)).orElse(null);

        // we have another identifier to extract the constituents from
        if (nextLink != null) {
            doResoleChain(chain, nextLink, constituents, fChain);
        } else {
            // either we gathered all constituents or the field is simply not there
            // so the we can submit all the constituents we received so far
            fChain.complete(constituents);
        }
    }

    private void handlePageResponse(Snapshot pageResponse, Throwable ex, String chain, Set<String> constituents,
            CompletableFuture<Set<String>> fChain) {
        if (pageResponse != null) {
            if (pageResponse.success()) {
                // no fields in response - our journey is over here
                if (pageResponse.data().isEmpty()) {
                    fChain.completeExceptionally(new Exception("Received empty data for '" + pageResponse.identifier()
                            + "' while resolving chain '" + chain + "'"));
                } else if (!fChain.isDone()) {
                    extractNextPageOrCompleteChainResolution(pageResponse, chain, constituents, fChain);
                }
            } else {
                fChain.completeExceptionally(new Exception("An error occured while resolving chain '" + chain + "'!"));
            }
        } else if (ex != null) {
            fChain.completeExceptionally(new Exception("Failed to resolve chain '" + chain + "': " + ex.getMessage()));
        }
    }

    private OmmConsumer createOmmConsumer() throws ConfigurationException {
        OmmConsumerConfig consumerConfig = EmaFactory.createOmmConsumerConfig()
                .config(ConfigUtility.createConfigMap(TEST_CONFIG)).clientId(TEST_CONFIG.applicationId())
                .username(TEST_CONFIG.username()).password(TEST_CONFIG.password())
                .tunnelingKeyStoreFile(TEST_CONFIG.keystore().toAbsolutePath().toString())
                .tunnelingKeyStorePasswd(TEST_CONFIG.keystorePassword());

        OmmConsumer candidate = EmaFactory.createOmmConsumer(consumerConfig);
        ChannelInformation ci = EmaFactory.createChannelInformation();
        candidate.channelInformation(ci);

        dataDictionary = EmaFactory.createDataDictionary();
        dataDictionary.loadEnumTypeDictionary(TEST_CONFIG.enumtypeDef().toAbsolutePath().toString());
        dataDictionary.loadFieldDictionary(TEST_CONFIG.rdmFieldDictionary().toAbsolutePath().toString());

        CompletableFuture<Void> fStartup = new CompletableFuture<>();
        statusHandle = candidate.registerClient(EmaFactory.createReqMsg().domainType(EmaRdm.MMT_LOGIN),
                new StatusClient(fStartup));
        boolean failed = true;
        try {
            fStartup.get(TEST_CONFIG.startupTimeoutSeconds(), TimeUnit.SECONDS);
            LOGGER.debug("[{}] Connection successfully established", this);
            failed = false;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new ConfigurationException("Connection creation was aborted");
        } catch (TimeoutException te) {
            throw new ConfigurationException("Connection creation timed out");
        } catch (Exception e) {
            throw new ConfigurationException("Connection creation failed: " + e.getMessage());
        } finally {
            if (failed) {
                LOGGER.debug("Cleaning up failed consumer instance");
                candidate.uninitialize();
            }
        }

        return candidate;
    }

}
