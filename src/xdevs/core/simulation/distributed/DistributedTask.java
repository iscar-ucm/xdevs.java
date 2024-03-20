/*
* File: DistributedTask.java
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

import java.util.concurrent.Callable;

/**
 * Task for distributed simulation
 *
 * This class implements a task for distributed simulation. It extends the
 * Callable class and implements the call method to execute the task.
 */
public class DistributedTask implements Callable<String> {
    /**
     * Host for the task
     */
    protected String host;
    /**
     * Port for the task
     */
    protected Integer port;
    /**
     * Command for the task
     */
    protected int command;
    /**
     * Message for the task
     */
    protected String message;
    
    /**
     * Constructor for the distributed task.
     *
     * @param host Host for the task
     * @param port Port for the task
     * @param command Command for the task
     * @param message Message for the task
     */
    public DistributedTask(String host, Integer port, int command, String message) {
        this.host = host;
        this.port = port;
        this.command = command;
        this.message = message;        
    }
    
    @Override
    public String call() {
        MessageDistributed md = new MessageDistributed(this.command, this.message);
        PingMessage pm = new PingMessage(md, host, port);
        md = pm.ping();        
        return md.getMessage();
    }
}
