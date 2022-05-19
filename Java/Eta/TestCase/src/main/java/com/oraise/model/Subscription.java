package com.oraise.model;

import java.util.List;

public interface Subscription extends AutoCloseable {

    String identifier();

    List<Data> datas();

    boolean isClosed();
}
