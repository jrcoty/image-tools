package com.blackrose;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import java.sql.Timestamp;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;

public class Comparator {
    private String rootFolder = "C:\\";
    private boolean exact = true;

    private List<Long> comparisons = new ArrayList<>();

    private FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File f, String name) {
            return name.matches(".+.jp\\w+");
        }
    };

    public Comparator() {

    }

    public Comparator(String rootFolder, boolean exact) {
        this.rootFolder = rootFolder;
        this.exact = exact;
    }

    public void compare() throws IOException {
        String[] files;

        System.out.println("Starting at " + new Timestamp(System.currentTimeMillis()));
        System.out.println();

        StringBuilder possibleDuplicates = new StringBuilder();

        try (ProgressBar pb = new ProgressBar("Progress", 100, 1000, System.err, ProgressBarStyle.ASCII, "", 1, false, null, ChronoUnit.SECONDS, 0L, Duration.ZERO)) {
            File f = new File(rootFolder);

            files = f.list(filter);

            int fileCount = files.length;

            System.out.println(fileCount + " images found");

            // No need to run if there is nothing
            // to run against
            if (fileCount < 2) {
                return;
            }

            int stepAmount = 100 / fileCount;

            for (String file : files) {

                pb.stepBy(stepAmount);

                for (String otherFile : files) {
                    if (file.compareTo(otherFile) != 0) {
                        long checkSum = getCheckSums(file.getBytes(), otherFile.getBytes());

                        if (!comparisons.contains(checkSum)) {
                            comparisons.add(checkSum);

                            BufferedImage img1 = ImageIO.read(new File(rootFolder + file));
                            BufferedImage img2 = ImageIO.read(new File(rootFolder + otherFile));

                            double p = getDifferencePercent(img1, img2);

                            if (p <= 15.00) {
                                possibleDuplicates.append(String.format("Found possible duplicates with %,.2f%% difference:", p) + " " + file + " - " + otherFile + "\n");
                            }
                        }
                    }
                }
            }

            pb.setExtraMessage("Comparing...");
        }
        catch (Exception ex) {
            System.out.println("Error: " + ex.toString());
        }

        System.out.println(possibleDuplicates.toString());
        System.out.println("Finished at " + new Timestamp(System.currentTimeMillis()));
        System.out.println();
        System.out.println("Hit any key to continue...");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String input = reader.readLine();
    }

    private double getDifferencePercent(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();
        int width2 = img2.getWidth();
        int height2 = img2.getHeight();

        // If we want to compare photos of the exact same dimensions,
        // which will make it go faster
        if ((width != width2 || height != height2) && exact) {
            //throw new IllegalArgumentException(String.format("Images must have the same dimensions: (%d,%d) vs. (%d,%d)", width, height, width2, height2));
            return 100.0;
        }

        int sHeight = Math.min(height, height2);
        int sWidth = Math.min(width, width2);

        long diff = 0;

        for (int y = 0; y < sHeight; y++) {
            for (int x = 0; x < sWidth; x++) {
                diff += pixelDiff(img1.getRGB(x, y), img2.getRGB(x, y));
            }
        }

        long maxDiff = 3L * 255 * sWidth * sHeight;

        return 100.0 * diff / maxDiff;
    }

    private int pixelDiff(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >>  8) & 0xff;
        int b1 =  rgb1        & 0xff;
        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >>  8) & 0xff;
        int b2 =  rgb2        & 0xff;

        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }

    private long getCheckSums(byte[] bytes, byte[] otherBytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);

        Checksum otherCrc32 = new CRC32();
        otherCrc32.update(otherBytes, 0, otherBytes.length);

        return crc32.getValue() + otherCrc32.getValue();
    }

    private int getSteps(int stepAmount) {
        if (stepAmount == 0) {
            return 0;
        }
        else {
            return stepAmount + getSteps(stepAmount - 1);
        }
    }
}