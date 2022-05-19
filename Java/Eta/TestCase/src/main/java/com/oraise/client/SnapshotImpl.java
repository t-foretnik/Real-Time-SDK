package com.oraise.client;

import java.util.HashMap;
import java.util.Map;

import com.oraise.model.Snapshot;

class SnapshotImpl extends DataImpl implements Snapshot {

    private final boolean success;

    public SnapshotImpl(String identifier, Map<Integer, String> data) {
        super(identifier, data);
        this.success = true;
    }

    public SnapshotImpl(String identifier) {
        super(identifier, new HashMap<>());
        this.success = false;
    }

    @Override
    public boolean success() {
        return success;
    }

}
