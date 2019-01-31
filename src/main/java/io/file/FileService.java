package io.file;

import application.ApplicationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileService {

    private static FileService instance;

    private FileService() {
    }

    public static FileService getInstance() {
        if (instance == null)
            instance = new FileService();
        return instance;
    }

    public Stream<String> readFileToStream(String path) {
        return readFile(path, s -> s.collect(Collectors.toList()).stream());
    }

    public List<Path> toFileList(boolean isDirectory, String path) {
        if (!isDirectory)
            return Collections.singletonList(Paths.get(path));
        try {
            return Files.list(Paths.get(path)).collect(Collectors.toList());
        } catch (IOException e) {
            throw new ApplicationException(String.format("Directory %s could not be loaded", path), e);
        }
    }

    private <T> T readFile(String path, Function<Stream<String>, T> converter) {
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            return converter.apply(stream);
        } catch (IOException e) {
            throw new ApplicationException(String.format("File %s could not be loaded", path), e);
        }
    }

    public String readFileToString(String path) {
        return readFile(path, s -> s.collect(Collectors.joining("\n")));
    }

    public void writeToFile(String path, String content) {
        createParent(path);
        try {
            Files.write(Paths.get(path), content.getBytes());
        } catch (IOException e) {
            throw new ApplicationException(String.format("File %s could not be saved", path), e);
        }
    }

    private void createParent(String path) {
        Path parent = Paths.get(path).normalize().getParent();
        if (parent != null && !Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new ApplicationException(String.format("File %s could not be saved", path), e);
            }
        }
    }

    public void deleteFile(String path) {
        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new ApplicationException(String.format("File %s could not be deleted", path), e);
        }
    }

    public void appendToCSV(String path, List<List<Object>> content) {
        writeToCSV(path, content, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    public void writeToCSV(String path, List<List<Object>> content) {
        deleteFile(path);
        writeToCSV(path, content, StandardOpenOption.CREATE);
    }

    private void writeToCSV(String path, List<List<Object>> content, OpenOption... openOptions) {
        createParent(path);
        try (
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), openOptions);
                CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
        ) {
            for (List<Object> record : content) {
                printer.printRecord(record);
            }
            printer.flush();
        } catch (IOException e) {
            throw new ApplicationException(String.format("File %s could not be saved", path), e);
        }
    }
}
