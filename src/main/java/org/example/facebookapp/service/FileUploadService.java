package org.example.facebookapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileService fileService;

    @Value("${dw.path}")
    private String dwPath;

    public void uploadFile(MultipartFile[] files) {
        try {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String fileOriginalName = file.getOriginalFilename();
                    if (fileOriginalName != null && !fileOriginalName.isEmpty()) {
                        String fileName = fileService.getUniqueFileName(fileOriginalName);
                        file.transferTo(new File(dwPath + fileName));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}