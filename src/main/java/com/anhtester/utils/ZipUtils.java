/*
 * Copyright (c) 2022 Anh Tester
 * Automation Framework Selenium
 */

package com.anhtester.utils;

import com.anhtester.constants.FrameworkConstants;
import org.zeroturnaround.zip.ZipUtil;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private ZipUtils() {
        super();
    }

    /* Make Zip file of Extent Reports in Project Root folder */
    public static void zipReportFolder() {
        if (FrameworkConstants.ZIP_FOLDER.toLowerCase().trim().equals(FrameworkConstants.YES)) {
            if ((FrameworkConstants.ZIP_FOLDER_PATH != null && !FrameworkConstants.ZIP_FOLDER_PATH.isEmpty()) && (FrameworkConstants.ZIP_FOLDER_NAME != null && !FrameworkConstants.ZIP_FOLDER_NAME.isEmpty())) {
                File sourceFolder = new File(FrameworkConstants.ZIP_FOLDER_PATH);
                if (!sourceFolder.exists()) {
                    LogUtils.warn("Skip zip: folder does not exist - " + FrameworkConstants.ZIP_FOLDER_PATH);
                    return;
                }
                ZipUtil.pack(sourceFolder, new File(FrameworkConstants.ZIP_FOLDER_NAME));
                LogUtils.info("Zipped " + FrameworkConstants.ZIPPED_EXTENT_REPORTS_FOLDER + " successfully !!");
            } else {
                File sourceFolder = new File(FrameworkConstants.EXTENT_REPORT_FOLDER_PATH);
                if (!sourceFolder.exists()) {
                    LogUtils.warn("Skip zip: folder does not exist - " + FrameworkConstants.EXTENT_REPORT_FOLDER_PATH);
                    return;
                }
                ZipUtil.pack(sourceFolder, new File(FrameworkConstants.ZIPPED_EXTENT_REPORTS_FOLDER));
                LogUtils.info("Zipped " + FrameworkConstants.ZIPPED_EXTENT_REPORTS_FOLDER + " successfully !!");
            }
        }
    }

    public static void zipFolder(String FolderPath, String ZipName) {
        File folder = new File(FolderPath);
        if (!folder.exists()) {
            LogUtils.warn("Skip zipFolder: source folder does not exist - " + FolderPath);
            return;
        }
        ZipUtil.pack(folder, new File(ZipName + ".zip"));
        LogUtils.info("Zipped " + FolderPath + " successfully !!");
    }

    public static void zipFile(String FilePath, String ZipName) {
        String sourceFile = FilePath;
        FileOutputStream fos = null;
        try {
            File fileToZip = new File(sourceFile);
            if (!fileToZip.exists()) {
                LogUtils.warn("Skip zipFile: source file does not exist - " + FilePath);
                return;
            }
            fos = new FileOutputStream(ZipName + ".zip");
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            FileInputStream fis = null;
            fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }

            zipOut.close();
            fis.close();
            fos.close();

            LogUtils.info("Zipped " + FilePath + " successfully !!");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void unZip(String FileZipPath, String FolderOutput) {
        File zipFile = new File(FileZipPath);
        if (!zipFile.exists()) {
            LogUtils.warn("Skip unZip: source zip does not exist - " + FileZipPath);
            return;
        }
        ZipUtil.unpack(zipFile, new File(FolderOutput));
        LogUtils.info("Unzipped " + FileZipPath + " successfully !!");
    }

    public static void unZipFile(String FileZipPath, String FolderOutput) {
        try {
            String fileZip = FileZipPath;
            File outputDir = new File(FolderOutput);
            File sourceZip = new File(fileZip);
            if (!sourceZip.exists()) {
                LogUtils.warn("Skip unZipFile: source zip does not exist - " + FileZipPath);
                return;
            }

            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceZip));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(outputDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
            LogUtils.info("Unzipped " + FileZipPath + " successfully !!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) {
        try {
            File destFile = new File(destinationDir, zipEntry.getName());
            String destDirPath = destinationDir.getCanonicalPath();
            String destFilePath = destFile.getCanonicalPath();
            if (!destFilePath.startsWith(destDirPath + File.separator)) {
                throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
            }
            return destFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
