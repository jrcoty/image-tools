package com.blackrose;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.lang3.StringUtils;

public class Organizer {

    private String startingFolder;
    private final String imageFolder = "Images";
    private final String unknownFolder = "Unknown";

    private FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File f, String name) {
            return name.matches(".+.jp\\w+");
        }
    };

    public Organizer() {
        startingFolder = "C:\\";
    }

    public Organizer(String startingFolder) {
        this.startingFolder = startingFolder;
    }

    public void organize() {
        String[] files;

        try {
            File f = new File(startingFolder);

            files = f.list(filter);

            for (String file : files) {
                System.out.print(file + " -> ");

                moveFile(startingFolder + "\\" + file);
            }

            System.out.println(files.length + " files processed");
        }
        catch (Exception ex) {
            System.out.println("Error: " + ex.toString());
        }
    }

    private void moveFile(String filePath) {
        File file = new File(filePath);

        String directoryPath = checkAndMakeDir(file.getName().substring(0, 4));

        File newFile = new File(directoryPath + "\\" + file.getName());

        System.out.println(file.renameTo(newFile) ? newFile.toString() : "Unable to move");
    }

    private String checkAndMakeDir(String yearCheck) {
        String directoryPath;

        // Check if the file has year
        // at the beginning
        if (StringUtils.isNumeric(yearCheck)) {
            directoryPath = startingFolder + "\\" + imageFolder + "\\" + yearCheck;
        }
        else {
            directoryPath = startingFolder + "\\" + imageFolder + "\\" + unknownFolder;
        }

        File directory = new File(directoryPath);

        directory.mkdirs();

        return directoryPath;
    }
}
