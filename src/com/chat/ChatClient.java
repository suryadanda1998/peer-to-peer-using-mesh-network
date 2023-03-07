package com.chat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class [ChatClient] reads the connectivity information of the peer and starts up the client
 * Starts the Sender and Receiver to handle incoming and outgoing traffic
 *
 * @author surya
 */
public class ChatClient implements Runnable {

    // static references to Sender and Receiver
    static Receiver receiver;
    static Sender sender;

    // current participants
    static List<NodeInfo> currentParticipants = new ArrayList<>();

    // client connectivity information (both for peer and client)
    public static NodeInfo myNodeInfo;
    public static NodeInfo peerNodeInfo;

    // constructor
    public ChatClient(String propertiesFile) {
        Properties properties = null;
        try {
            // get properties from the properties file
            properties = new PropertyHandler(propertiesFile);
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, "Could not open default properties file", ex);
            // abnormal termination of the program
            System.exit(1);
        }

        // get client port
        int myPort = 0;
        try {
            myPort = Integer.parseInt(properties.getProperty("MY_PORT"));
        } catch(NumberFormatException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, "Could not read my port", ex);
            System.exit(1);
        }

        // get Client name
        String myInfo = properties.getProperty("MY_NAME");
        if(myInfo == null) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, "Could not read my name");
            System.exit(1);
        }

        // create my connectivity NodeInfo
        myNodeInfo = new NodeInfo(NetworkUtilities.getMyIP(), myPort, myInfo);

        // get peer default port
        int peerPort = 0;
        try {
            peerPort = Integer.parseInt(properties.getProperty("SERVER_PORT"));
        } catch(NumberFormatException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, "Could not read peer port", ex);
        }
        // get peer default IP address
        String peerIP = properties.getProperty("SERVER_IP");

        if(peerIP == null) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, "Could not read peer IP");
        }

        // create chat peer default connectivity NodeInfo
        if(peerPort != 0 && peerIP != null) {
            peerNodeInfo = new NodeInfo(peerIP, peerPort);
        }
    }

    // entry point to start the chat
    @Override
    public void run() {
        // start the Receiver
        (receiver = new Receiver()).start();
        // start the Sender
        (sender = new Sender()).start();
    }

    // Chat application main method
    public static void main(String[] args) {
        String propertiesFile;

        try{
            propertiesFile = args[0];
        } catch (ArrayIndexOutOfBoundsException ex) {
            propertiesFile = "resources"+ File.separator +"ChatApplicationDefaultConfig.properties";
        }
        // start the chat node
        new Thread(new ChatClient(propertiesFile)).start();
    }
}
