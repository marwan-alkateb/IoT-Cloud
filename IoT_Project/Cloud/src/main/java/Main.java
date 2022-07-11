import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.thrift.transport.TTransportException;

public class Main {
    public static void main(String[] args) throws TTransportException {
        final int COORDINATOR_PORT = Integer.parseInt(System.getenv("COORDINATOR_PORT"));
        final String COORDINATOR_IP = System.getenv("COORDINATOR_IP");
        ExecutorService pool = Executors.newFixedThreadPool(50);

        //// Thrift Client ////
        TSocket thrift_socket = new TSocket(COORDINATOR_IP, COORDINATOR_PORT);
        thrift_socket.open();
        ThriftService.Client thrift_client = new ThriftService.Client(new TBinaryProtocol(thrift_socket));

        try (ServerSocket tcp_socket = new ServerSocket(TcpServer.GATE_CLOUD_PORT))
        {
            while (true) {
                try {
                    Socket accepted_tcp_connection = tcp_socket.accept();
                    Callable<Void> task = new TcpServer.MultiThreaded_TCP(accepted_tcp_connection, thrift_client);
                    pool.submit(task);
                } catch (IOException ex) {

                } catch (TTransportException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException ex) {
            System.err.println("Couldn't start server");
        }
    }
}

