package it.spaghettisource.broadcastsync.infrastructure;

import java.net.DatagramPacket;
import java.util.LinkedList;

/**
 * This class is responsible to store the DatagramPacket that offered by the {@link UdpServer} and pop by the {@link DatagramSequentializer}
 * it is a FIFO so the first datagram arrived, will be the first processed
 * 
 * @author Alessandro D'Ottavio
 * @version 1.0
 *
 */
public class DatagramPacketQueue {

    private LinkedList <DatagramPacket> queue = new LinkedList<>();

    /**
     * add a new datagram at the end of the queue and notify the reader
     * 
     * @param datagram
     * @throws InterruptedException
     */
    public synchronized void offer(DatagramPacket datagram) throws InterruptedException {
        
        //add the value in the stack and notify the reader
    	queue.offer(datagram);
        notify();
    }

    /**
     * remove the first element from the queue
     * 
     * @return
     * @throws InterruptedException
     */
    public synchronized DatagramPacket pop() throws InterruptedException {

    	//if the stack is empty wait till a new datagram is arrived
        while (queue.isEmpty()) {
            wait();
        }

        return queue.poll();
    }
    
    
    /**
     * clear the queue from all the messages
     *  
     */
    public synchronized void clear() {
    	queue.clear();
    }
    
}

