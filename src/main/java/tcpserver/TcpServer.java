package tcpserver;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

// Single Threaded Tcp server
// can accept only one TCP connection at a time
// Inspiration is from Youtube
// Might be bug prone -  let's see :)
public class TcpServer {

    Logger logger = LogManager.getLogger(TcpServer.class);

    public void runTcpServer(int portNumber){
        int concurrentConnections = 0;
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            for(;;){
                Socket clientSocket = serverSocket.accept();
                InputStream in = null;
                OutputStream out = null;
                // Read from Accepted socket
                try {
                    ++concurrentConnections;
                    // log number of connections
                    logger.info("New client is connected with server " + clientSocket.toString() +
                            "No of concurrent connections are " + concurrentConnections);
                    in = clientSocket.getInputStream();
                    out = clientSocket.getOutputStream();

                    // Allowing read of 1024 bytes in a go from input stream
                    byte[] buff = new byte[1024];
                    int result = 0;
                    // read from socket
                    for( ;(result = in.read(buff)) != -1 ;){
                        String string = new String(buff, 0 , result);
                        logger.info(string);
                        // write to output stream and flush
                        out.write(string.getBytes());
                        out.flush();
                    }
                }catch (Exception e){
                    // log failure
                    // close client socket
                    logger.error("Error while serving client ", e);
                }finally {
                    // reduce number of sockets
                    --concurrentConnections;
                    // close input socket  stream
                    in.close();
                    // close output socket stream
                    out.close();
                    // close socket
                    clientSocket.close();
                }
            }
        }catch (Exception  e){
            // LOG failure
            logger.error("Error while creating Tcp server ", e);
        }
    }
}
