/*
* File: MessageDistributed.java
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import xdevs.core.util.Constants;

/**
 * Message for distributed simulation.
 * 
 * This class implements a message for distributed simulation. It contains a
 * command, a message and a collection of values.
 */
public class MessageDistributed implements Serializable{
    /**
     * Command for the message
     */
    private int command;
    /**
     * Message for the message
     */
    private String message;
    /**
     * Collection of values for the message
     */
    private Collection<?> valuesPort;
    
    /**
     * Constructor for the distributed message.
     * 
     * @param command Command for the message
     * @param message Message for the message
     * @param valuesPort Collection of values for the message
     */
    public MessageDistributed(int command, String message, Collection<?> valuesPort){
        this.command = command;
        this.message = message;
        this.valuesPort = valuesPort;
    }
    
    /**
     * Constructor for the distributed message.
     * 
     * @param command Command for the message
     * @param message Message for the message
     */
    public MessageDistributed(int command, String message){
        this(command,message,new ArrayList<>());
    }

    /**
     * Constructor for the distributed message.
     * 
     * @param message Message for the message
     */
    public MessageDistributed(String message){
        this(Constants.DS_NONE,message);
    }
    
    /**
     * Constructor for the distributed message.
     */
    public MessageDistributed(){
        this("");
    }
    
    /**
     * Get the command for the message.
     * @return Command for the message
     */
    public int getCommand() {
        return command;
    }
    
    /**
     * Set the command for the message.
     * @param command Command for the message
     */
    public void setCommand(int command) {    
        this.command = command;
    }

    /**
     * Get the message for the message.
     * @return Message for the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the message for the message.
     * @param message Message for the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Get the collection of values for the message.
     * @return Collection of values for the message
     */
    public Collection <?> getValuesPort() {
        return valuesPort;
    }

    /**
     * Set the collection of values for the message.
     * @param valuesPort Collection of values for the message
     */
    public void setValuesPort(Collection <?> valuesPort) {
        this.valuesPort = (Collection<?>) valuesPort;
    }

    @Override
    public String toString() {
        return "MessageDistributed{" + "command=" + command + ", message=" + message + ", ports=" + valuesPort + '}';
    }
        
}
