package tcpserver;

import core.Command;
import core.CommandExecutor;
import core.Decoder;
import core.RedisCommand;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

// Single Threaded Tcp server - but concurrent
// Performs IO in non blocking manner - IO Muliplexing
// Event loop -> Redis usages this
// Might be bug prone -  let's see :)
public class AsyncTcpServer {
    Logger logger = LogManager.getLogger(AsyncTcpServer.class);
    public static AsyncTcpServer instance  = new AsyncTcpServer();
    private AsyncTcpServer(){}

    public void runAsyncTcpServer(int portNumber){
        try {
            // Creating selector for selecting selectable channels
            Selector selector = Selector.open();
            // Creating Server socket channel for accepting connection
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            // Binding to local address
            InetSocketAddress address = new InetSocketAddress("localhost", portNumber);
            serverChannel.bind(address);
            // set non blocking nature
            serverChannel.configureBlocking(false);
            // register server socket with selector
            serverChannel.register(selector, SelectionKey.OP_ACCEPT, null);

            for(;;){
                // Blocking call - selects channels which are ready for I/O
                selector.select();
                // get selection keys
                Iterator<SelectionKey > itr =  selector.selectedKeys().iterator();
                while (itr.hasNext()){
                    SelectionKey key = itr.next();

                    if(key.isAcceptable()){
                        // Accept new connection
                        SocketChannel clientChannel = serverChannel.accept();
                        if(clientChannel == null)continue;
                        logger.info("New client is connected with server " + clientChannel );
                        // Set non blocking
                        clientChannel.configureBlocking(false);
                        // Register with selector
                        clientChannel.register(selector,SelectionKey.OP_READ, null );
                    }else if(key.isReadable()){
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        int read = clientChannel.read(buffer);
                        // -1  we see channel is closed
                        //  0   when there is no byte to read
                        //  n   where n denotes number of bytes read
                        if(read == -1){
                            clientChannel.close();
                            itr.remove();
                        }else if(read == 0){
                            continue;
                        }else {
                            // Creates unicode string from bytes read
                            // TODO : move this logic for better place to not break - single responsiblity
                            List<Object > objects =  (List<Object >)new Decoder(buffer.array()).decode();
                            String cmd = (String)objects.get(0);
                            String []args = new String[objects.size() - 1];
                            String res;
                            if(cmd.equals("PING")){
                                for(int i = 1 ; i < objects.size(); i++ ){
                                    args[i - 1] = (String )objects.get(i);
                                }
                                RedisCommand redisCommand = new RedisCommand(Command.PING, args);
                                res = (String )CommandExecutor.instance.execute(redisCommand);
                            }else{
                                res = "-Command Not supported Yet!\r\n";
                            }
                            buffer.clear();
                            buffer.put(res.getBytes());
                            // Write to channel
                            // Flip from read to write
                            buffer.flip();
                            while(buffer.hasRemaining()){
                                clientChannel.write(buffer);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            // chew exception for now
            logger.error("Error occurred!!" , e);
        }
    }
}
