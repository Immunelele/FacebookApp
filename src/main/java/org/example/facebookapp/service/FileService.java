package org.example.facebookapp.service;

import org.example.facebookapp.model.FileInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FileService {

    @Value("${dw.path}")
    private String dwPath;

    public List<File> findFilesBySuffix(String suffix) {
        return findFilesBySuffix(new File(dwPath), suffix);
    }

    private List<File> findFilesBySuffix(File root, String suffix) {
        List<File> filesList = new ArrayList<>();

        if (root.isDirectory()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    filesList.addAll(findFilesBySuffix(file, suffix));
                }
            }
        } else if (root.getName().endsWith(suffix)) {
            filesList.add(root);
        }
        return  filesList;
    }

    public List<FileInfo> createFileInfoList() {
        List<File> files = findFilesBySuffix(".json");
        List<FileInfo> filesInfoList = new ArrayList<>();
        for (File file : files) {
            String name = file.getName();
            String url = "/download/" + name;
            filesInfoList.add(new FileInfo(name, url));
        }
        return filesInfoList;
    }

    public void fileDelete(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return;
        }
        file.delete();
    }

    public String getUniqueFileName(String originalName) {
        String baseName = originalName.substring(0, originalName.lastIndexOf('.'));
        String extension = originalName.substring(originalName.lastIndexOf('.'));
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return baseName + "_" + timestamp + extension;
    }

    public String convertLongToDayOfTheWeek(long timestampLongVal) {
        Date date = new Date(timestampLongVal);
        DateFormat format = new SimpleDateFormat("EEEE");
        return format.format(date);
    }

    public String convertLongToDayOfTheMonth(long timestampLongVal) {
        Date date = new Date(timestampLongVal);
        DateFormat format = new SimpleDateFormat("dd");
        return format.format(date);
    }

    public String convertLongToMonth(long timestampLongVal) {
        Date date = new Date(timestampLongVal);
        DateFormat format = new SimpleDateFormat("MMMM");
        return format.format(date);
    }

    public String convertLongToYear(long timestampLongVal) {
        Date date = new Date(timestampLongVal);
        DateFormat format = new SimpleDateFormat("yyyy");
        return format.format(date);
    }
}