## What's the purpose of this project?

This test cases should prove that unchecked exceptions are not handled properly and will break the thread executor main loop causing the OmmConsumer instance to be no longer functional.

Sources contain a very limited model to retrieve snapshots and subscribe to instruments.
The test sources contain a minimal implementation configuring an OmmConsumer instance.

- [com.oraise.SubscriptionTest](./src/test/java/com/oraise/SubscriptionTest.java) proves that the subscription mechanics work
- [com.oraise.SnapShotTest](./src/test/java/com/oraise/SnapShotTest.java) proves that the snapshot mechanics work
- [com.oraise.ChainResolutionTest](./src/test/java/com/oraise/ChainResolutionTest.java) proves that the chain resolution mechanics work
- [com.oraise.FaultyClientTest](./src/test/java/com/oraise/FaultyClientTest.java) demonstrates how easy unchecked exceptions can break the thread executor main loop
- [com.oraise.ChainConstituentDataTest](./src/test/java/com/oraise/ChainConstituentDataTest.java) shows that there are even internal reasons for unchecked exceptions breaking the thread executor main loop, so the issue is not limited to poorly implemented clients

## Why are the tests disabled by default?

In order to run the tests user-specific configuration is required. A valid account to access the Refinitiv services and a Java keystore are required. Our own account settings are not included in the test code for obvious reasons.

The basic test configuration is read from [test.properties](./test.properties) which can be either modified or any file *.properties can be placed in the project root directory which will be read in addition to [test.properties](./test.properties) and overwrite the place holders for the required configuration parameters.

```
# account settings
#
username=GE-A-00000000-0-1234
password=abcdefghijklmnopqrstuvwxyz012345
applicationId=c0ffeec0ffeec0ffeec0ffeec0ffeec0ffeebabe

# keystore settings
#
keystore=<Java Keystore File>
keystorePassword=<Password For Keystore>

# predefined values
#
serviceName=ELEKTRON_DD
server=eu-west-1-aws-1-med.optimized-pricing-api.refinitiv.net
port=14002
enumtypeDef=./enumtype.def
rdmFieldDictionary=./RDMFieldDictionary
startUpTimeoutSeconds=15
snapshotTimeoutSeconds=5
subscriptionTimeoutSeconds=5
```

## How to use these test cases?

The tests can be run from any IDE or by removing the exclusion from [build.gradle](build.gradle). Make sure to provide the necessary configuration values in order to make the tests work.

## The tests are all successful, so what's the problem?

The tests are written to prove the current faulty behavior and thus expect certain actions to fail. Assertions are made to prove that the exceptions occur and not that actions completed successfully 