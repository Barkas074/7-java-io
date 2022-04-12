package com.company;

import org.omg.CORBA.Environment;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
    private static Path PATH = Paths.get("");
    private static String NAME = "myvcs.dat";

    public static void main(String[] args) {
        switch (args[0]) {
            case "init":
                init();
                break;
            case "commit":
                commit(args);
                break;
            case "status":
                status();
                break;
            case "log":
                log();
                break;
            case "diff":
                diff();
                break;
            case "checkout":
                checkout();
                break;
            default:
                break;
        }
    }

    private static void init() {
        try {
            Files.createFile(Paths.get(NAME));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Initialized");
    }

    private static void commit(String[] args) {
        List<Path> files = new ArrayList<>();
        Files.exists(PATH);
        Files.isDirectory(PATH);
        try {
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(PATH)) {
                for (Path child : dirStream) {
                    if (!Files.isDirectory(child) && Files.getLastModifiedTime(child).toMillis() > Files.getLastModifiedTime(Paths.get(NAME)).toMillis()) {
                        files.add(child);
                    }
                }
            }
            List<String> lines = Files.readAllLines(Paths.get(NAME), StandardCharsets.UTF_8);
            List<String> revision = new ArrayList<>();
            for (String line : lines){
                if (line.contains("Revision "))
                    revision.add(line);
            }
            String textFormat =
                    "Revision " + (revision.size() == 0 ? 1 : (Integer.parseInt(revision.get(revision.size() - 1).split(" ")[1]) + 1)) + System.lineSeparator() +
                    "Date: " + new SimpleDateFormat("hh:mm:ss dd.MM.yyyy").format(new Date()) + System.lineSeparator() +
                    "Files: " + files.size() + System.lineSeparator();
            String textOutput = textFormat;
            textFormat += "---" + System.lineSeparator();
            for (Path file : files) {
                List<String> linesFile = Files.readAllLines(file, StandardCharsets.UTF_8);
                textFormat +=
                        file + " (" + Files.size(file) + " bytes)" + System.lineSeparator() +
                        "|||" + System.lineSeparator();
                for (String line : linesFile){
                    textFormat += line + System.lineSeparator();
                }
                textFormat += "|||" + System.lineSeparator();
            }
            textFormat += "---" + System.lineSeparator();
            if (args.length == 3) {
                if (args[1].equals("-m")){
                    textFormat += "Comment: " + args[2] + System.lineSeparator();
                    textOutput += "Comment: " + args[2] + System.lineSeparator();
                }
            }
            textFormat += System.lineSeparator();
            textOutput += System.lineSeparator();
            try {
                FileWriter fileWriter = new FileWriter(NAME, true);
                PrintWriter writer = new PrintWriter(fileWriter);
                writer.print(textFormat);
                writer.close();
                System.out.println(textOutput);
            } catch (FileNotFoundException notFoundException) {
                System.out.println(notFoundException.getMessage());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void status() {

    }

    private static void log() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(NAME), StandardCharsets.UTF_8);
            String parseLines = new String();
            boolean listFiles = false;
            for (String line : lines) {
                if (line.contains("---"))
                    listFiles = listFiles ? false : true;
                else if (!listFiles)
                    parseLines += line + System.lineSeparator();
            }
            System.out.println(parseLines);
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private static void diff() {

    }

    private static void checkout() {

    }
}
