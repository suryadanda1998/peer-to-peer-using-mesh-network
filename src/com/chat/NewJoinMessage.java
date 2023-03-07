package com.chat;

import java.io.Serializable;
import java.util.List;

/**
 *  Class [NewJoinMessage] Defines a JOIN_EVENT Message that has a message type and content.
 *
 *  Instances of this class can be sent over a network, using object streams.
 *  Message types are defined in MessageTypes
 *
 * @author surya
 */
public class NewJoinMessage extends Message implements Serializable {

    // constructor
    public NewJoinMessage(int type, Object content) {
        super(type, content);
    }

    // accessor method
    public List<NodeInfo> getChatParticipants() {
        List< NodeInfo> currentParticipants = (List<NodeInfo>) this.content;
        return currentParticipants;
    }
}
