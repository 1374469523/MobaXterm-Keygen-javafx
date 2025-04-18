package com.yaojing.mobaxtermkeygenjavafx;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MobaXtermKeygen {

    private static final String VariantBase64Table = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    private static final Map<Integer, Character> VariantBase64Dict = new HashMap<>();
    private static final Map<Character, Integer> VariantBase64ReverseDict = new HashMap<>();

    static {
        for (int i = 0; i < VariantBase64Table.length(); i++) {
            VariantBase64Dict.put(i, VariantBase64Table.charAt(i));
            VariantBase64ReverseDict.put(VariantBase64Table.charAt(i), i);
        }
    }

    public static byte[] variantBase64Encode(byte[] bs) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int blocksCount = bs.length / 3;
        int leftBytes = bs.length % 3;

        for (int i = 0; i < blocksCount; i++) {
            int blockStart = i * 3;
            int codingInt = (bs[blockStart] & 0xFF) | ((bs[blockStart + 1] & 0xFF) << 8) | ((bs[blockStart + 2] & 0xFF) << 16);
            char[] block = new char[4];
            block[0] = VariantBase64Dict.get(codingInt & 0x3F);
            block[1] = VariantBase64Dict.get((codingInt >> 6) & 0x3F);
            block[2] = VariantBase64Dict.get((codingInt >> 12) & 0x3F);
            block[3] = VariantBase64Dict.get((codingInt >> 18) & 0x3F);
            try {
                result.write(new String(block).getBytes(StandardCharsets.US_ASCII));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (leftBytes == 0) {
            return result.toByteArray();
        } else if (leftBytes == 1) {
            int codingInt = bs[blocksCount * 3] & 0xFF;
            char[] block = new char[2];
            block[0] = VariantBase64Dict.get(codingInt & 0x3F);
            block[1] = VariantBase64Dict.get((codingInt >> 6) & 0x3F);
            try {
                result.write(new String(block).getBytes(StandardCharsets.US_ASCII));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { // leftBytes == 2
            int blockStart = blocksCount * 3;
            int codingInt = (bs[blockStart] & 0xFF) | ((bs[blockStart + 1] & 0xFF) << 8);
            char[] block = new char[3];
            block[0] = VariantBase64Dict.get(codingInt & 0x3F);
            block[1] = VariantBase64Dict.get((codingInt >> 6) & 0x3F);
            block[2] = VariantBase64Dict.get((codingInt >> 12) & 0x3F);
            try {
                result.write(new String(block).getBytes(StandardCharsets.US_ASCII));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result.toByteArray();
    }

    public static byte[] encryptBytes(int key, byte[] bs) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int currentKey = key;
        for (byte b : bs) {
            int encrypted = (b & 0xFF) ^ ((currentKey >> 8) & 0xFF);
            result.write(encrypted);
            currentKey = (encrypted & currentKey) | 0x482D;
        }
        return result.toByteArray();
    }

    public static class LicenseType {
        public static final int Professional = 1;
        public static final int Educational = 3;
        public static final int Persional = 4;
    }

    public static void generateLicense(int type, int count, String userName, int majorVersion, int minorVersion) throws IOException {
        String licenseString = String.format("%d#%s|%d%d#%d#%d3%d6%d#%d#%d#%d#",
                type, userName, majorVersion, minorVersion,
                count,
                majorVersion, minorVersion, minorVersion,
                0, 0, 0);

        byte[] encrypted = encryptBytes(0x787, licenseString.getBytes(StandardCharsets.US_ASCII));
        byte[] encoded = variantBase64Encode(encrypted);
        String encodedLicenseString = new String(encoded, StandardCharsets.US_ASCII);

        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream("Custom.mxtpro"))) {
            ZipEntry entry = new ZipEntry("Pro.key");
            byte[] data = encodedLicenseString.getBytes(StandardCharsets.US_ASCII);
            entry.setMethod(ZipEntry.STORED);
            entry.setSize(data.length);
            entry.setCompressedSize(data.length);
            CRC32 crc = new CRC32();
            crc.update(data);
            entry.setCrc(crc.getValue());
            zipOut.putNextEntry(entry);
            zipOut.write(data);
            zipOut.closeEntry();
        }
    }

    private static void help() {
        System.out.println("Usage:");
        System.out.println("    java MobaXtermKeygen <UserName> <Version>");
        System.out.println();
        System.out.println("    <UserName>:      The Name licensed to");
        System.out.println("    <Version>:       The Version of MobaXterm");
        System.out.println("                     Example:    10.9");
        System.out.println();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            help();
            return;
        }

        String[] versionParts = args[1].split("\\.");
        int majorVersion = Integer.parseInt(versionParts[0]);
        int minorVersion = Integer.parseInt(versionParts[1]);

        try {
            generateLicense(LicenseType.Professional, 1, args[0], majorVersion, minorVersion);
            System.out.println("[*] Success!");
            System.out.println("[*] File generated: " + new File("Custom.mxtpro").getAbsolutePath());
            System.out.println("[*] Please move or copy the newly-generated file to MobaXterm's installation path.");
        } catch (Exception e) {
            System.out.println("[*] ERROR: " + e.getMessage());
        }
    }
}