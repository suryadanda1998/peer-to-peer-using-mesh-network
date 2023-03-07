package com.chat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class [ReceiverWorker] handles the communication received from peers based on [MessageType]
 *
 * @author bhavana
 */
public class ReceiverWorker extends Thread implements MessageTypes {

    // Server Connection
    private Socket serverConnection;

    // object stream to read and write data to the net
    private ObjectInputStream readFromNet;
    private ObjectOutputStream writeToNet;

    // reference to class [Message] Object
    private Message message;

    // constructor
    public ReceiverWorker(Socket serverConnection) {
        this.serverConnection = serverConnection;
        try {
            readFromNet = new ObjectInputStream(this.serverConnection.getInputStream());
            writeToNet = new ObjectOutputStream(this.serverConnection.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ReceiverWorker.class.getName()).log(Level.SEVERE, "could not open object streams", ex);
        }
    }

    // thread entry point
    @Override
    public void run() {
        NodeInfo participantsInfo = null;
        Iterator<NodeInfo> participantIterator;

        try {
            // read message
            message = (Message) readFromNet.readObject();
            // close the server connection
            serverConnection.close();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ReceiverWorker.class.getName()).log(Level.SEVERE, "Message could not be read", ex);
            // no use of going further
            System.exit(1);
        }

        // decide what to do depending on the type of message received from the server
        switch (message.getType())
        {
            case JOIN:
                // get the new participant details
                NodeInfo joiningParticipantInfo = (NodeInfo) message.getContent();

                // send current participants list to the new participant
                NodeInfoList nodes = new NodeInfoList(ChatClient.currentParticipants);
                Message newJoinMessage = new Message(JOIN_EVENT, nodes);
                sendRequestToParticipants(newJoinMessage, joiningParticipantInfo);

                // add new participant to the current participants list
                ChatClient.currentParticipants.add(joiningParticipantInfo);

                // update all the participants about the new joining except new participant itself
                participantIterator = ChatClient.currentParticipants.iterator();
                Message newParticipantInfo = new Message(JOIN_EVENT, new NodeInfoList(Collections.singletonList(joiningParticipantInfo)));

                // iterate through all the participants
                while (participantIterator.hasNext()) {
                    participantsInfo = participantIterator.next();
                    if(!(participantsInfo.equals(ChatClient.myNodeInfo) || participantsInfo.equals(joiningParticipantInfo)))
                        sendRequestToParticipants(newParticipantInfo, participantsInfo);
                }

                // print out all the participants in the network
                System.out.println(joiningParticipantInfo.getName() + " joined. All current participants: ");

                participantIterator = ChatClient.currentParticipants.iterator();

                // iterate through all the participants
                while (participantIterator.hasNext()) {
                    participantsInfo = participantIterator.next();
                    System.out.print(participantsInfo.getName() + " ");
                }

                System.out.println();
                break;
            case JOIN_EVENT:
                // handle Join Event and update the current participants list
                NodeInfoList newParticipants = (NodeInfoList) message.getContent();

                ChatClient.currentParticipants.addAll(newParticipants.getCurrentParticipants());

                System.out.println("Participant list updated. All current participants: ");

                participantIterator = ChatClient.currentParticipants.iterator();
                // print out all the participants
                while (participantIterator.hasNext()) {
                    participantsInfo = participantIterator.next();
                    System.out.print(participantsInfo.getName() + " ");
                }
                System.out.println();
                break;
            case LEAVE:
                // remove this participant's info
                NodeInfo leavingParticipantInfo = (NodeInfo) message.getContent();

                if(ChatClient.currentParticipants.remove(leavingParticipantInfo)) {
                    System.err.println(leavingParticipantInfo.getName() + " removed");

                    // show who left
                    System.out.println(leavingParticipantInfo.getName() + " left. Remaining participants: ");
                } else {
                    System.err.println(leavingParticipantInfo.getName() + " not found");
                }

                // print out all the remaining participants
                participantIterator = ChatClient.currentParticipants.iterator();
                while (participantIterator.hasNext()) {
                    participantsInfo = participantIterator.next();
                    System.out.print(participantsInfo.getName() + " ");
                }
                System.out.println();
                break;
            case SHUTDOWN:
                // show the shutdown node initiator node name
                NodeInfo shutdownRequestInitiator = (NodeInfo) message.getContent();
                System.out.println("Received shutdown message from " + shutdownRequestInitiator.getName() + ", shutting down...");
                System.exit(0);
                break;
            case NOTE:
                // display the note
                System.out.println((String) message.getContent());
                System.out.println();
                break;
            default:
                Logger.getLogger(ReceiverWorker.class.getName()).log(Level.SEVERE, "This case should not occur");
        }
    }

    // handles communication between peers
    private void sendRequestToParticipants(Message message, NodeInfo participantDetails) {
        try {
            // open connection to server
            Socket participantConnection = new Socket(participantDetails.getAddress(), participantDetails.getPort());

            // open object streams
            writeToNet = new ObjectOutputStream(participantConnection.getOutputStream());
            readFromNet = new ObjectInputStream(participantConnection.getInputStream());

            // send join request
            writeToNet.writeObject(message);

            // close connection
            participantConnection.close();
        } catch (IOException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, "Error Connecting to server or writing/reading object streams or closing connection", ex);
        }
    }
}
