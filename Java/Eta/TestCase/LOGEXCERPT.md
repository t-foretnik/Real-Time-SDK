##Log Excerpt

When running [com.oraise.ChainConstituentDataTest#testChainConstituentDataNoLimitFail()](src/test/java/com/oraise/ChainConstituentDataTest.java) we can see the [premature removal](#item-is-removed-prematurely-here) of the handle that is _NOT_ triggered by the client code and finally the resulting [error](#handling-refreshmsg-fails). Log excerpt is broken down into sections and some parts are skipped:

###Regular startup, first snapshot and start of chain resolution

```
#
# ... skipped initialization ...
#
12:27:09.533 [main] DEBUG - [com.oraise.ChainConstituentDataTest@2254127a] Connection successfully established [com.oraise.TestBase]
12:27:09.546 [main] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 2 of StreamId 5 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.547 [main] DEBUG - Registered client with handle [2] for snapshot for identifier [/CONG.DE] [com.oraise.TestBase]
12:27:09.604 [pool-2-thread-1] DEBUG - [/CONG.DE] Snapshot succeeded [com.oraise.client.SnapshotClient]
12:27:09.604 [main] DEBUG - Snapshot for [/CONG.DE] finished successfully [com.oraise.TestBase]
12:27:09.604 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 2 of StreamId 5 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.604 [main] DEBUG - Snapshot for [/CONG.DE] finished, unregistering handle [2] [com.oraise.TestBase]
12:27:09.604 [main] DEBUG - Unregistered handle [2] for snapshot for identifier [/CONG.DE] [com.oraise.TestBase]
12:27:09.608 [main] DEBUG - Received data for [/CONG.DE]: {1=4018, 1025=10:12:09,...,1021=12173.0} [com.oraise.ChainConstituentDataTest]
12:27:09.611 [main] DEBUG - Starting to resolve chain [/.GDAXI] [com.oraise.TestBase]
12:27:09.612 [main] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 3 of StreamId 6 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.615 [main] DEBUG - Registered handle [3] for page request of [0#/.GDAXI] [com.oraise.TestBase]
12:27:09.660 [pool-2-thread-1] DEBUG - [0#/.GDAXI] Snapshot succeeded [com.oraise.client.SnapshotClient]
12:27:09.660 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 3 of StreamId 6 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.660 [pool-2-thread-1] DEBUG - Unregistering handle [3] for page request of [0#/.GDAXI] [com.oraise.TestBase]
12:27:09.665 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 4 of StreamId 7 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.667 [pool-2-thread-1] DEBUG - Registered handle [4] for page request of [1#/.GDAXI] [com.oraise.TestBase]
12:27:09.668 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 3 of StreamId 6 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.712 [pool-2-thread-1] DEBUG - [1#/.GDAXI] Snapshot succeeded [com.oraise.client.SnapshotClient]
12:27:09.712 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 4 of StreamId 7 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.712 [pool-2-thread-1] DEBUG - Unregistering handle [4] for page request of [1#/.GDAXI] [com.oraise.TestBase]
12:27:09.713 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 5 of StreamId 8 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.714 [pool-2-thread-1] DEBUG - Registered handle [5] for page request of [2#/.GDAXI] [com.oraise.TestBase]
12:27:09.714 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 4 of StreamId 7 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.760 [pool-2-thread-1] DEBUG - [2#/.GDAXI] Snapshot succeeded [com.oraise.client.SnapshotClient]
12:27:09.760 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 5 of StreamId 8 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.760 [pool-2-thread-1] DEBUG - Unregistering handle [5] for page request of [2#/.GDAXI] [com.oraise.TestBase]
12:27:09.760 [pool-2-thread-1] DEBUG - Chain resolution for [/.GDAXI] is done. [40] constituent(s) found [com.oraise.TestBase]
12:27:09.760 [pool-2-thread-1] DEBUG - Resolved 40 constituent(s) for chain [/.GDAXI]: [/BASFn.DE, /DHER.DE, /ADSGn.DE, /LINI.DE, /CONG.DE, /FMEG.DE, /HNKG_p.DE, /DB1Gn.DE, /EONGn.DE, /MUVGn.DE, /PUMG.DE, /FREG.DE, /SAPG.DE, /SATG_p.DE, /HFGG.DE, /MBGn.DE, /DBKGn.DE, /1COV.DE, /MTXGn.DE, /ALVG.DE, /HEIG.DE, /RWEG.DE, /AIRG.DE, /MRCG.DE, /DTGGe.DE, /BMWG.DE, /DTEGn.DE, /SY1G.DE, /VOWG_p.DE, /DPWGn.DE, /BAYGn.DE, /VNAn.DE, /QIA.DE, /HNRGn.DE, /SHLG.DE, /PSHG_p.DE, /SIEGn.DE, /IFXGn.DE, /BNRGn.DE, /ZALG.DE] [com.oraise.ChainConstituentDataTest]
12:27:09.761 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 6 of StreamId 9 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.762 [pool-2-thread-1] DEBUG - Registered client with handle [6] for async snapshot for identifier [/BASFn.DE] [com.oraise.TestBase]
12:27:09.767 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 7 of StreamId 10 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.768 [pool-2-thread-1] DEBUG - Registered client with handle [7] for async snapshot for identifier [/DHER.DE] [com.oraise.TestBase]
12:27:09.768 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 8 of StreamId 11 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.769 [pool-2-thread-1] DEBUG - Registered client with handle [8] for async snapshot for identifier [/ADSGn.DE] [com.oraise.TestBase]
12:27:09.770 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 9 of StreamId 12 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.771 [pool-2-thread-1] DEBUG - Registered client with handle [9] for async snapshot for identifier [/LINI.DE] [com.oraise.TestBase]
12:27:09.772 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 10 of StreamId 13 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.773 [pool-2-thread-1] DEBUG - Registered client with handle [10] for async snapshot for identifier [/CONG.DE] [com.oraise.TestBase]
12:27:09.773 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 11 of StreamId 14 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.774 [pool-2-thread-1] DEBUG - Registered client with handle [11] for async snapshot for identifier [/FMEG.DE] [com.oraise.TestBase]
12:27:09.775 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 12 of StreamId 15 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.776 [pool-2-thread-1] DEBUG - Registered client with handle [12] for async snapshot for identifier [/HNKG_p.DE] [com.oraise.TestBase]
12:27:09.776 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 13 of StreamId 16 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.777 [pool-2-thread-1] DEBUG - Registered client with handle [13] for async snapshot for identifier [/DB1Gn.DE] [com.oraise.TestBase]
12:27:09.777 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 14 of StreamId 17 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.778 [pool-2-thread-1] DEBUG - Registered client with handle [14] for async snapshot for identifier [/EONGn.DE] [com.oraise.TestBase]
12:27:09.778 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 15 of StreamId 18 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.779 [pool-2-thread-1] DEBUG - Registered client with handle [15] for async snapshot for identifier [/MUVGn.DE] [com.oraise.TestBase]
12:27:09.780 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 16 of StreamId 19 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.781 [pool-2-thread-1] DEBUG - Registered client with handle [16] for async snapshot for identifier [/PUMG.DE] [com.oraise.TestBase]
12:27:09.781 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 17 of StreamId 20 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.782 [pool-2-thread-1] DEBUG - Registered client with handle [17] for async snapshot for identifier [/FREG.DE] [com.oraise.TestBase]
12:27:09.782 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 18 of StreamId 21 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.783 [pool-2-thread-1] DEBUG - Registered client with handle [18] for async snapshot for identifier [/SAPG.DE] [com.oraise.TestBase]
12:27:09.784 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 19 of StreamId 22 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.785 [pool-2-thread-1] DEBUG - Registered client with handle [19] for async snapshot for identifier [/SATG_p.DE] [com.oraise.TestBase]
12:27:09.785 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 20 of StreamId 23 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.786 [pool-2-thread-1] DEBUG - Registered client with handle [20] for async snapshot for identifier [/HFGG.DE] [com.oraise.TestBase]
12:27:09.787 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 21 of StreamId 24 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.788 [pool-2-thread-1] DEBUG - Registered client with handle [21] for async snapshot for identifier [/MBGn.DE] [com.oraise.TestBase]
12:27:09.788 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 22 of StreamId 25 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.789 [pool-2-thread-1] DEBUG - Registered client with handle [22] for async snapshot for identifier [/DBKGn.DE] [com.oraise.TestBase]
12:27:09.789 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 23 of StreamId 26 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.790 [pool-2-thread-1] DEBUG - Registered client with handle [23] for async snapshot for identifier [/1COV.DE] [com.oraise.TestBase]
12:27:09.791 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 24 of StreamId 27 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.791 [pool-2-thread-1] DEBUG - Registered client with handle [24] for async snapshot for identifier [/MTXGn.DE] [com.oraise.TestBase]
12:27:09.792 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 25 of StreamId 28 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.793 [pool-2-thread-1] DEBUG - Registered client with handle [25] for async snapshot for identifier [/ALVG.DE] [com.oraise.TestBase]
12:27:09.793 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 26 of StreamId 29 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.794 [pool-2-thread-1] DEBUG - Registered client with handle [26] for async snapshot for identifier [/HEIG.DE] [com.oraise.TestBase]
12:27:09.794 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 27 of StreamId 30 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.795 [pool-2-thread-1] DEBUG - Registered client with handle [27] for async snapshot for identifier [/RWEG.DE] [com.oraise.TestBase]
12:27:09.795 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 28 of StreamId 31 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.796 [pool-2-thread-1] DEBUG - Registered client with handle [28] for async snapshot for identifier [/AIRG.DE] [com.oraise.TestBase]
12:27:09.796 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 29 of StreamId 32 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.797 [pool-2-thread-1] DEBUG - Registered client with handle [29] for async snapshot for identifier [/MRCG.DE] [com.oraise.TestBase]
12:27:09.797 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 30 of StreamId 33 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.797 [pool-2-thread-1] DEBUG - Registered client with handle [30] for async snapshot for identifier [/DTGGe.DE] [com.oraise.TestBase]
12:27:09.798 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 31 of StreamId 34 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.798 [pool-2-thread-1] DEBUG - Registered client with handle [31] for async snapshot for identifier [/BMWG.DE] [com.oraise.TestBase]
12:27:09.798 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 32 of StreamId 35 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.799 [pool-2-thread-1] DEBUG - Registered client with handle [32] for async snapshot for identifier [/DTEGn.DE] [com.oraise.TestBase]
12:27:09.799 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 33 of StreamId 36 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.800 [pool-2-thread-1] DEBUG - Registered client with handle [33] for async snapshot for identifier [/SY1G.DE] [com.oraise.TestBase]
12:27:09.800 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 34 of StreamId 37 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.800 [pool-2-thread-1] DEBUG - Registered client with handle [34] for async snapshot for identifier [/VOWG_p.DE] [com.oraise.TestBase]
12:27:09.801 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 35 of StreamId 38 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.801 [pool-2-thread-1] DEBUG - Registered client with handle [35] for async snapshot for identifier [/DPWGn.DE] [com.oraise.TestBase]
12:27:09.801 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 36 of StreamId 39 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.802 [pool-2-thread-1] DEBUG - Registered client with handle [36] for async snapshot for identifier [/BAYGn.DE] [com.oraise.TestBase]
12:27:09.802 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 37 of StreamId 40 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.802 [pool-2-thread-1] DEBUG - Registered client with handle [37] for async snapshot for identifier [/VNAn.DE] [com.oraise.TestBase]
12:27:09.803 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 38 of StreamId 41 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.803 [pool-2-thread-1] DEBUG - Registered client with handle [38] for async snapshot for identifier [/QIA.DE] [com.oraise.TestBase]
12:27:09.803 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 39 of StreamId 42 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.804 [pool-2-thread-1] DEBUG - Registered client with handle [39] for async snapshot for identifier [/HNRGn.DE] [com.oraise.TestBase]
12:27:09.804 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 40 of StreamId 43 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.804 [pool-2-thread-1] DEBUG - Registered client with handle [40] for async snapshot for identifier [/SHLG.DE] [com.oraise.TestBase]
12:27:09.805 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 41 of StreamId 44 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.805 [pool-2-thread-1] DEBUG - Registered client with handle [41] for async snapshot for identifier [/PSHG_p.DE] [com.oraise.TestBase]
12:27:09.805 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 42 of StreamId 45 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.806 [pool-2-thread-1] DEBUG - Registered client with handle [42] for async snapshot for identifier [/SIEGn.DE] [com.oraise.TestBase]
12:27:09.806 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 43 of StreamId 46 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.806 [pool-2-thread-1] DEBUG - Registered client with handle [43] for async snapshot for identifier [/IFXGn.DE] [com.oraise.TestBase]
12:27:09.806 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 44 of StreamId 47 to item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.806 [pool-2-thread-1] DEBUG - Registered client with handle [44] for async snapshot for identifier [/BNRGn.DE] [com.oraise.TestBase]
12:27:09.806 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 45 of StreamId 48 to item map
    Instance name Consumer_1_1
loggerMsgEnd
```
###Item is removed prematurely here
```
 [c.r.ema.access.OmmConsumerImpl]
12:27:09.807 [pool-2-thread-1] DEBUG - Registered client with handle [45] for async snapshot for identifier [/ZALG.DE] [com.oraise.TestBase]
12:27:09.807 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 10 of StreamId 13 from item map
    Instance name Consumer_1_1
loggerMsgEnd
```
###First snapshot results are handled properly
```
 [c.r.ema.access.OmmConsumerImpl]
12:27:09.811 [pool-2-thread-1] DEBUG - [/BASFn.DE] Snapshot succeeded [com.oraise.client.SnapshotClient]
12:27:09.811 [pool-2-thread-1] DEBUG - Async snapshot for [/BASFn.DE] finished, unregistering handle [6] [com.oraise.TestBase]
12:27:09.811 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 6 of StreamId 9 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.811 [pool-2-thread-1] DEBUG - Unregistered handle [6] for async snapshot for identifier [/BASFn.DE] [com.oraise.TestBase]
12:27:09.811 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 6 of StreamId 9 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.857 [pool-2-thread-1] DEBUG - [/ADSGn.DE] Snapshot succeeded [com.oraise.client.SnapshotClient]
12:27:09.857 [pool-2-thread-1] DEBUG - Async snapshot for [/ADSGn.DE] finished, unregistering handle [8] [com.oraise.TestBase]
12:27:09.857 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 8 of StreamId 11 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.857 [pool-2-thread-1] DEBUG - Unregistered handle [8] for async snapshot for identifier [/ADSGn.DE] [com.oraise.TestBase]
12:27:09.857 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 8 of StreamId 11 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.861 [pool-2-thread-1] DEBUG - [/DHER.DE] Snapshot succeeded [com.oraise.client.SnapshotClient]
12:27:09.861 [pool-2-thread-1] DEBUG - Async snapshot for [/DHER.DE] finished, unregistering handle [7] [com.oraise.TestBase]
12:27:09.861 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 7 of StreamId 10 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.861 [pool-2-thread-1] DEBUG - Unregistered handle [7] for async snapshot for identifier [/DHER.DE] [com.oraise.TestBase]
12:27:09.861 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 7 of StreamId 10 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.865 [pool-2-thread-1] DEBUG - [/LINI.DE] Snapshot succeeded [com.oraise.client.SnapshotClient]
12:27:09.865 [pool-2-thread-1] DEBUG - Async snapshot for [/LINI.DE] finished, unregistering handle [9] [com.oraise.TestBase]
12:27:09.865 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 9 of StreamId 12 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.865 [pool-2-thread-1] DEBUG - Unregistered handle [9] for async snapshot for identifier [/LINI.DE] [com.oraise.TestBase]
12:27:09.865 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 9 of StreamId 12 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.869 [pool-2-thread-1] DEBUG - [/FMEG.DE] Snapshot succeeded [com.oraise.client.SnapshotClient]
12:27:09.869 [pool-2-thread-1] DEBUG - Async snapshot for [/FMEG.DE] finished, unregistering handle [11] [com.oraise.TestBase]
12:27:09.869 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 11 of StreamId 14 from item map
    Instance name Consumer_1_1
loggerMsgEnd

 [c.r.ema.access.OmmConsumerImpl]
12:27:09.869 [pool-2-thread-1] DEBUG - Unregistered handle [11] for async snapshot for identifier [/FMEG.DE] [com.oraise.TestBase]
12:27:09.870 [pool-2-thread-1] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Removed Item 11 of StreamId 14 from item map
    Instance name Consumer_1_1
loggerMsgEnd
```
###Handling RefreshMsg fails

Item 10 /.CONG.DE has been removed

```
 [c.r.ema.access.OmmConsumerImpl]
Exception in thread "pool-2-thread-1" java.lang.NullPointerException
    at com.refinitiv.ema.access.ItemCallbackClientConsumer.notifyOnAllMsg(ItemCallbackClient.java:2561)
    at com.refinitiv.ema.access.ItemCallbackClient.processRefreshMsg(ItemCallbackClient.java:1788)
    at com.refinitiv.ema.access.ItemCallbackClient.defaultMsgCallback(ItemCallbackClient.java:1626)
    at com.refinitiv.eta.valueadd.reactor.Reactor.sendDefaultMsgCallback(Reactor.java:2084)
    at com.refinitiv.eta.valueadd.reactor.Reactor.sendAndHandleDefaultMsgCallback(Reactor.java:2099)
    at com.refinitiv.eta.valueadd.reactor.WlItemHandler.callbackUser(WlItemHandler.java:2943)
    at com.refinitiv.eta.valueadd.reactor.WlItemHandler.readRefreshMsg(WlItemHandler.java:2174)
    at com.refinitiv.eta.valueadd.reactor.WlItemHandler.readMsg(WlItemHandler.java:1908)
    at com.refinitiv.eta.valueadd.reactor.Watchlist.readMsg(Watchlist.java:300)
    at com.refinitiv.eta.valueadd.reactor.Reactor.processRwfMessage(Reactor.java:3143)
    at com.refinitiv.eta.valueadd.reactor.Reactor.performChannelRead(Reactor.java:3465)
    at com.refinitiv.eta.valueadd.reactor.Reactor.dispatchChannel(Reactor.java:2672)
    at com.refinitiv.eta.valueadd.reactor.ReactorChannel.dispatch(ReactorChannel.java:610)
    at com.refinitiv.ema.access.OmmBaseImpl.rsslReactorDispatchLoop(OmmBaseImpl.java:1543)
    at com.refinitiv.ema.access.OmmBaseImpl.run(OmmBaseImpl.java:1685)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
    at java.lang.Thread.run(Thread.java:750)
12:27:19.628 [main] TRACE - loggerMsg
    ClientName: ItemCallbackClient
    Severity: Trace
    Text:    Added Item 46 of StreamId 49 to item map
    Instance name Consumer_1_1
loggerMsgEnd

#
# ... skipped the rest of the logs
#
```