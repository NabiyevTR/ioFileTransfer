package ru.ntr.server;

import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j
public class Server {
    private final ConcurrentLinkedQueue<ClientHandler> clients;

    public Server(int port) {
      clients = new ConcurrentLinkedQueue<>();
      try (ServerSocket serverSocket = new ServerSocket(port)) {

          log.info("Server started on port " + port + ".");

          while (true) {
              Socket socket = serverSocket.accept();
              log.info("Client accepted: " + socket.getRemoteSocketAddress());
              ClientHandler handler = new ClientHandler(socket, this);
              clients.add(handler);
              new Thread(handler).start();
          }
      } catch (IOException e) {
          log.info("Server failed" , e);
      }
    }

    public void kickClient(ClientHandler handler) {
        clients.remove(handler);
    }

    public static void main(String[] args) {

        int port;

        try {
            port = Integer.parseInt(System.getenv("SERVER_PORT"));
        } catch (NumberFormatException e) {
            log.error("Wrong port: ", e);
            return;
        }
       new Server(port);

    }
}
