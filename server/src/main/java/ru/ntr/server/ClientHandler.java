package ru.ntr.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Log4j
@RequiredArgsConstructor
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final Server server;
    private final String serverDir = System.getenv("SERVER_DIR");
    private DataInputStream dis;
    private BufferedInputStream bis;

    public void run() {
        try {
            bis = new BufferedInputStream(socket.getInputStream());
            dis = new DataInputStream(bis);
            String fileName = dis.readUTF();
            Files.copy(dis, Paths.get(serverDir, fileName), REPLACE_EXISTING);
            log.info("Server has received from client file " + fileName + " successfully.");
        } catch (IOException e) {
            log.error("Connection was broken: ", e);
        } finally {
            server.kickClient(this);
            try {
                log.info("Trying to close connection with client " + socket.getRemoteSocketAddress() + ".");
                socket.close();
                log.info("Connection was closed successfully.");
            } catch (IOException e) {
                log.error("Failed to close connection with client:", e);
            }
        }
    }
}
