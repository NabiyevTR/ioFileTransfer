package ru.ntr.client;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Log4j
public class FileManager {

    private String dir = System.getenv("CLIENT_DIR");

    @Getter
    private final List<FileEntity> files;

    public FileManager() {
        files = getAllFilesInFolder(dir);
    }

    private List<FileEntity> getAllFilesInFolder(String dir) {

        List<FileEntity> files = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(dir))) {
            paths.filter(Files::isRegularFile)
                    .forEach(e -> files.add(new FileEntity(e)));
        } catch (IOException e) {
            log.error("IO error during getting file from directory: ", e);
        }
        return files;
    }

    public void sendFile(Path path) {

        String host = System.getenv("SERVER_HOST");
        int port;
        try {
            port = Integer.parseInt(System.getenv("SERVER_PORT"));
        } catch (NumberFormatException e) {
                        log.error("Wrong port number: ", e);
            return;
        }

        Socket socket;
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            log.error("Can't establish a connection: ", e);
            return;
        }

        try (BufferedOutputStream os = new BufferedOutputStream(socket.getOutputStream());
             DataOutputStream d = new DataOutputStream(os)) {
            log.info("File transfer started. File name: " + path.getFileName());
            d.writeUTF(path.getFileName().toString());
            Files.copy(path, d);
            log.info("File transfer has finished successfully. File name: " + path.getFileName());
        } catch (Exception e) {
            log.error("Error during sending file " + path.getFileName() + ": ", e);
        }

        try {
            socket.close();
            log.info("Socket closed.");
        } catch (IOException e) {
            log.error("Can't close socket: ", e);
        }
    }
}
