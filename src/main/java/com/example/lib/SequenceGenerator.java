package com.example.lib;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;

public class SequenceGenerator {
    private static final int UNUSED_BITS = 1; // Sign bit, Unused (always set to 0)
    private static final int EPOCH_BITS = 47;
    private static final int NODE_ID_BITS = 6;
    private static final int SEQUENCE_BITS = 10;

    private static final int maxNodeId = (int) (Math.pow(2, NODE_ID_BITS) - 1);
    private static final int maxSequence = (int) (Math.pow(2, SEQUENCE_BITS) - 1);

    // Custom Epoch (Thursday, August 18, 2022 9:14:10 PM GMT+07:00)
    private static final long CUSTOM_EPOCH = 1660832050000L;

    private final int nodeId;

    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;

    // Create SequenceGenerator with a nodeId
    public SequenceGenerator(int nodeId) {
        if (nodeId < 0 || nodeId > maxNodeId) {
            throw new IllegalArgumentException(String.format("NodeId must be between %d and %d", 0, maxNodeId));
        }
        this.nodeId = nodeId;
    }

    // Let SequenceGenerator generate a nodeId
    public SequenceGenerator() {
        this.nodeId = createNodeId();
    }

    private int createNodeId() {
        int nodeId;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        sb.append(String.format("%02X", b));
                    }
                }
            }
            nodeId = sb.toString().hashCode();
        } catch (Exception e) {
            nodeId = new SecureRandom().nextInt();
        }
        nodeId = nodeId & maxNodeId;
        return nodeId;
    }

    // Get current timestamp in milliseconds, adjust for the custom epoch.
    private static long now() {
        return System.currentTimeMillis() - CUSTOM_EPOCH;
    }

    // Block and wait till next millisecond
    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = now();
        }
        return currentTimestamp;
    }

    public synchronized long nextLong() {
        long currentTimestamp = now();

        if (currentTimestamp < lastTimestamp) {
            throw new IllegalStateException("Invalid System Clock!");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                // Sequence Exhausted, wait till next millisecond.
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            // reset sequence to start with zero for the next millisecond
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;
        long id = currentTimestamp << (NODE_ID_BITS + SEQUENCE_BITS);
        id |= (nodeId << SEQUENCE_BITS);
        id |= sequence;
        return id;
    }

}
