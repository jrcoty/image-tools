package com.blackrose;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        int choice = 0;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        do {
            System.out.println("Image Tools v0.1 - Black Rose Funeral Home");
            System.out.println("Author: CultD");
            System.out.println();
            System.out.println("1: Run Compare");
            System.out.println("2: Run Organizer");
            System.out.println("3: Exit");

            try {
                choice = Integer.parseInt(reader.readLine());

                switch(choice) {
                    case 1: runCompare(reader);
                            break;
                    case 2: runOrganizer(reader);
                            break;
                    default: break;
                }
            }
            catch (Exception ex) {
                System.out.println("Invalid option.");
            }

            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
        while (choice != 3);
    }

    private static void runCompare(BufferedReader reader) throws IOException {
        System.out.print("Enter Path: ");

        String path = reader.readLine();

        System.out.println();

        System.out.print("Exact Compare (true\\false)? ");

        String exact = reader.readLine();

        System.out.println("Path supplied:  " + path);
        System.out.println("Exact Compare: " + exact);

        Comparator comparator = new Comparator(path, checkExact(exact));

        comparator.compare();
    }

    private static boolean checkExact(String exact) {

        return true;
    }

    private static void runOrganizer(BufferedReader reader) throws IOException {
        System.out.print("Enter Path: ");

        String path = reader.readLine();

        System.out.println();

        Organizer organizer = new Organizer(path);

        organizer.organize();
    }

    private static boolean checkArgs(String[] args) {
        if (args.length == 0 || args.length < 2) {
            return false;
        }

        try {
            Paths.get(args[0]);
        }
        catch (InvalidPathException ex) {
            return false;
        }

        try {
            boolean check = Boolean.parseBoolean(args[1]);
        }
        catch (Exception ex) {
            return false;
        }

        return true;
    }
}