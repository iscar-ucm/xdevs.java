/*
* File: PingMessage.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2024/03/20 (YYYY/MM/DD)
*
* Copyright (C) 2024
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package xdevs.core.simulation.distributed;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * Class to transmit a message to a remote host and receive the response.
 * 
 * This class implements a message to be transmitted to a remote host. It
 * contains a message and a destination host and port. It also contains a method
 * to send the message and receive the response.
 */
public class PingMessage {
    private static final Logger LOGGER = Logger.getLogger(PingMessage.class.getName());
    /**
     * Default destination host.
     */
    public static final String DEFAULT_DESTINATION_HOST = "127.0.0.1";
    /**
     * Default destination port.
     */
    public static final int DEFAULT_DESTINATION_PORT = 5000; 
    
    /**
     * Message for the ping
     */   
    private MessageDistributed message;    
    /**
     * Destination host for the ping
     */
    private String destinationHost;
    /**
     * Destination port for the ping
     */
    private int destinationPort;
    
    /**
     * Constructor for the ping message.
     * 
     * @param message Message for the ping
     * @param destinationHost Destination host for the ping
     * @param destinationPort Destination port for the ping
     */
    public PingMessage(MessageDistributed message, String destinationHost, int destinationPort){
        this.message = message;
        this.destinationHost = destinationHost;
        this.destinationPort = destinationPort;
    }
    
    /**
     * Constructor for the ping message.
     * 
     * @param message Message for the ping
     */
    public PingMessage(MessageDistributed message){
        this(message, DEFAULT_DESTINATION_HOST, DEFAULT_DESTINATION_PORT);
    }
    
    /**
     * Send the message and receive the response.
     * @return The response to the message
     */
    public MessageDistributed ping() {
        MessageDistributed response = null;
        try {
            Socket sc = new Socket(this.destinationHost, this.destinationPort);            
            ObjectOutputStream out = new ObjectOutputStream(sc.getOutputStream());
            out.writeObject(this.message);
            ObjectInputStream in =  new ObjectInputStream (sc.getInputStream());            
            response = (MessageDistributed) in.readObject();
            in.close();
            out.close();
            sc.close();
        } catch (Exception e) {
            LOGGER.severe("Error connecting to " + this.destinationHost + ":" + this.destinationPort + "... (command: " + message.getCommand() + "-" + message.getMessage() +  ")");
            LOGGER.severe(e.getLocalizedMessage());
        } 
        return response;
    }
}
