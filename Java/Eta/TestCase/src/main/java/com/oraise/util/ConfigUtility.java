package com.oraise.util;

import java.util.Collection;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oraise.exception.ConfigurationException;
import com.refinitiv.ema.access.Data;
import com.refinitiv.ema.access.ElementList;
import com.refinitiv.ema.access.EmaFactory;
import com.refinitiv.ema.access.Map;
import com.refinitiv.ema.access.MapEntry;
import com.refinitiv.ema.access.OmmArray;
import com.refinitiv.ema.access.ReqMsg;
import com.refinitiv.ema.rdm.EmaRdm;

public class ConfigUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtility.class);

    public static final String ENUM_DICT_NAME = "RWFEnum";
    public static final String FIELD_DATA_DICT_NAME = "RWFFld";

    public static ReqMsg createSnapshotRequest(String ric, String serviceName) {
        return EmaFactory.createReqMsg().name(ric).serviceName(serviceName).interestAfterRefresh(false);
    }

    public static ReqMsg createStreamingRequest(String ric, String serviceName, Collection<Integer> filterFids) {
        final ReqMsg msg = EmaFactory.createReqMsg().name(ric).serviceName(serviceName);

        try {
            if (!filterFids.isEmpty()) {
                applyFilter(msg,
                        array -> filterFids.forEach(fid -> array.add(EmaFactory.createOmmArrayEntry().intValue(fid))));
            }
        } catch (Exception e) {
            LOGGER.warn(
                    "Failed to configure filter on request for RIC [{}], subscription will be made without filter: {}",
                    ric, e.getMessage(), e);
        }
        return msg;
    }

    public static Data createConfigMap(TestConfig config) throws ConfigurationException {
        Map configNode = EmaFactory.createMap();
        Map elementMap = EmaFactory.createMap();
        ElementList elementList = EmaFactory.createElementList();
        elementList.add(EmaFactory.createElementEntry().ascii("Channel", "Channel_1"));
        elementList.add(EmaFactory.createElementEntry().doubleValue("TokenReissueRatio", 0.3));

        elementMap.add(EmaFactory.createMapEntry().keyAscii("Consumer_1", MapEntry.MapAction.ADD, elementList));
        elementList.clear();

        elementList.add(EmaFactory.createElementEntry().ascii("DefaultConsumer", "Consumer_1"));
        elementList.add(EmaFactory.createElementEntry().map("ConsumerList", elementMap));
        elementMap.clear();

        configNode.add(EmaFactory.createMapEntry().keyAscii("ConsumerGroup", MapEntry.MapAction.ADD, elementList));
        elementList.clear();

        elementList.add(EmaFactory.createElementEntry().ascii("ChannelType", "ChannelType::RSSL_ENCRYPTED"));
        elementList.add(EmaFactory.createElementEntry().ascii("Host", config.server()));
        elementList.add(EmaFactory.createElementEntry().ascii("Port", config.port()));
        elementList.add(EmaFactory.createElementEntry().intValue("EnableSessionManagement", 1));
        elementList.add(EmaFactory.createElementEntry().intValue("CompressionThreshold", 300));
        elementList.add(EmaFactory.createElementEntry().enumValue("EncryptedProtocolType", 0));

        elementMap.add(EmaFactory.createMapEntry().keyAscii("Channel_1", MapEntry.MapAction.ADD, elementList));
        elementList.clear();

        elementList.add(EmaFactory.createElementEntry().map("ChannelList", elementMap));
        elementMap.clear();

        configNode.add(EmaFactory.createMapEntry().keyAscii("ChannelGroup", MapEntry.MapAction.ADD, elementList));

        elementList.clear();

        elementList.add(EmaFactory.createElementEntry().ascii("DictionaryType", "DictionaryType::FileDictionary"));
        elementList.add(EmaFactory.createElementEntry().ascii("EnumTypeDefFileName",
                config.enumtypeDef().toAbsolutePath().toString()));
        elementList.add(EmaFactory.createElementEntry().ascii("RdmFieldDictionaryFileName",
                config.rdmFieldDictionary().toAbsolutePath().toString()));

        elementList.add(EmaFactory.createElementEntry().ascii("EnumTypeDefItemName", ENUM_DICT_NAME));
        elementList.add(EmaFactory.createElementEntry().ascii("RdmFieldDictionaryItemName", FIELD_DATA_DICT_NAME));
        elementMap
                .add(EmaFactory.createMapEntry().keyAscii("Consumer_Dictionary", MapEntry.MapAction.ADD, elementList));

        elementList.clear();
        elementList.add(EmaFactory.createElementEntry().map("DictionaryList", elementMap));
        elementMap.clear();
        configNode.add(EmaFactory.createMapEntry().keyAscii("DictionaryGroup", MapEntry.MapAction.ADD, elementList));
        elementList.clear();
        return configNode;
    }

    private static void applyFilter(ReqMsg msg, Consumer<OmmArray> arrayConsumer) {
        final OmmArray array = EmaFactory.createOmmArray();
        array.fixedWidth(2); // 2 is meant to indicate same datatype for all elements NOT SIZE OF ARRAY
        arrayConsumer.accept(array);

        ElementList view = EmaFactory.createElementList();
        view.add(EmaFactory.createElementEntry().uintValue(EmaRdm.ENAME_VIEW_TYPE, 1));
        view.add(EmaFactory.createElementEntry().array(EmaRdm.ENAME_VIEW_DATA, array));

        msg.payload(view);
    }

    private ConfigUtility() {
    }

}
