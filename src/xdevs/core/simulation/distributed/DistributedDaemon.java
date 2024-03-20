/*
* File: DistributedDaemon.java
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Daemon for distributed simulation
 *
 * This class implements a daemon for distributed simulation. It listens for
 * incoming connections and processes the messages received.
 */
public class DistributedDaemon {
    private static final Logger LOGGER = Logger.getLogger(DistributedDaemon.class.getName());
    /**
     * Port for the daemon
     */
    private int port;
    /**
     * Simulator for the daemon
     */
    private SimulatorDistributed sd;
    
    /**
     * Constructor for the distributed daemon.
     * @param port Port for the daemon
     * @param sd Simulator for the daemon
     */
    public DistributedDaemon(int port, SimulatorDistributed sd){
        this.port = port;
        this.sd = sd;
    }
    
    /**
     * Start the daemon
     */
    public void start() {
        BlockingDeque<Socket> queue = new LinkedBlockingDeque<>();

        Thread producer = new Thread(() -> {
            try {
                ServerSocketChannel ssc = ServerSocketChannel.open();
                ssc.socket().bind(new InetSocketAddress(port));
                ssc.configureBlocking(false);

                while (!sd.isGetOut()) {

                    SocketChannel sc = ssc.accept();
                    if (sc != null) {
                        queue.put(sc.socket());
                    }
                }
                ssc.close();
            } catch (Exception e) {
                LOGGER.severe(e.getLocalizedMessage());
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                while(!sd.isGetOut()) {
                    Socket socket = queue.poll(100, TimeUnit.MILLISECONDS);
                    if(socket!=null) {
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        MessageDistributed msg = (MessageDistributed) in.readObject();
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(sd.interpreter(msg));
                        in.close();
                        out.close();
                        socket.close();
                    }
                }
            } catch (InterruptedException | IOException | ClassNotFoundException e) {
                LOGGER.severe(e.getLocalizedMessage());
            }
        });
        producer.start();
        consumer.start();
    }    
}
