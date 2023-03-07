package com.chat;

import java.io.Serializable;
import java.util.Objects;

/**
 * Class [NodeInfo] to represent the Address, Port, Name of a host
 *
 * @author surya
 */
public class NodeInfo  implements Serializable {

    String address;
    int port;
    String name = null;

    /**
     * Constructor to accept all the details of a host
     *
     * @param address
     * @param port
     * @param name
     */
    public NodeInfo(String address, int port, String name) {
        this.address = address;
        this.port = port;
        this.name = name;
    }

    /**
     * Constructor which handles when name is null
     *
     * @param address
     * @param port
     */
    public NodeInfo(String address, int port) {
        this.address = address;
        this.port = port;
    }

    // Getter methods
    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    // overriding hash code and equals method handle comparison between NodeInfo objects
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeInfo nodeInfo = (NodeInfo) o;
        return port == nodeInfo.port && Objects.equals(address, nodeInfo.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, address);
    }
}
