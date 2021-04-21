package ru.ntr.client;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class FileEntity {

    private String name;
    private Path path;
    private String size;

    public FileEntity(Path path) {

        this.name = path.getFileName().toString();
        this.path = path;
        try {
            this.size = Files.size(path) / 1024 + "KB";
        } catch (IOException e) {
            this.size = "0KB";
        }
    }
}
