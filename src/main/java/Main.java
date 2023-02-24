import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tcpserver.TcpServer;

public class Main {

    static Logger logger = LogManager.getLogger(Main.class);
    // Main java class for Jedis
    public static void main(String[] args) {
        // Adding shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Stopping TCP Server")));
        // starting tcp server
        TcpServer tcpServer = new TcpServer();
        logger.info("Starting TCP Server on port " + 8379);
        tcpServer.runTcpServer(8379);
    }
}
