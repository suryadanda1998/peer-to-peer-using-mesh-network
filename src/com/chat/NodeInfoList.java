package com.chat;

import java.io.Serializable;
import java.util.List;

/**
 * class [NodeInfoList] to share the current participant list with others in the Network
 *
 * @author surya
 */
public class NodeInfoList implements Serializable {

    List<NodeInfo> currentParticipants;

    public NodeInfoList(List<NodeInfo> currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    // gets the current participant list
    public List<NodeInfo> getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(List<NodeInfo> currentParticipants) {
        this.currentParticipants = currentParticipants;
    }
}
