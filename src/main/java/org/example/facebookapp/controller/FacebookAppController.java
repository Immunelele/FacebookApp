package org.example.facebookapp.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.facebookapp.exception.ResourceNotFoundException;
import org.example.facebookapp.model.FileInfo;
import org.example.facebookapp.model.Message;
import org.example.facebookapp.model.ParticipantData;
import org.example.facebookapp.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/starter")
public class FacebookAppController {
    private final FileService fileService;
    private final MessageService messageService;
    private final FileUploadService fileUploadService;
    private final AvatarService avatarService;
    private final PdfService pdfService;

    @Value("${dw.path}")
    private String dwPath;

    @GetMapping()
    public String starter() {
        return "starter";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping("/upload")
    public String filesUpload(@RequestParam("files") MultipartFile[] files) {
        fileUploadService.uploadFile(files);
        return "upload" ;
    }

    @GetMapping("/files")
    public String getFilesList(ModelMap model) {
        List<FileInfo> filesInfoList = fileService.createFileInfoList();
        Map<String, Integer> participantsByFileMap = messageService.getParticipantsSizeFromFile();
        model.addAttribute("files", filesInfoList);
        model.addAttribute("participants", participantsByFileMap);
        return "files";
    }

    @GetMapping("/files/messages/{fileName}")
    public String getFilesMessages(@PathVariable String fileName, ModelMap model) {
        File file = new File(dwPath + fileName);
        if (!file.exists()) {
            throw new ResourceNotFoundException("Nie znaleziono pliku " + fileName);
        }

        List<Message> messageList = messageService.getMessageFromFile(file);
        int wordsCount = messageService.getWordsCount(messageList);
        int totalMessageCount = messageService.getTotalMessageCount(messageList);

        model.addAttribute("messages", messageList);
        model.addAttribute("wordsCount", wordsCount);
        model.addAttribute("totalMessageCount", totalMessageCount);
        return "messages";
    }

    @GetMapping("/files/messages/{fileName}/execute")
    public String dataSelection(@PathVariable String fileName, ModelMap model, @RequestParam String dataType) {
        File file = new File(dwPath + fileName);
        if (!file.exists()) {
            throw new ResourceNotFoundException("Nie znaleziono pliku " + fileName);
        }
        List<Message> messageList = messageService.getMessageFromFile(file);
        Map<String, Long> dataMap;

        switch (dataType) {
            case "participants":
                Map<String, ParticipantData> participantDataMap = avatarService.getParticipantsData(file);
                model.addAttribute("participantDataMap", participantDataMap);
                return "messages_execute_avatars";
            case "dWeek":
                dataMap = messageService.getDayOfTheWeekCount(messageList);
                break;
            case "dMonth":
                dataMap = messageService.getDayOfTheMonthCount(messageList);
                break;
            case "year":
                dataMap = messageService.getYearCount(messageList);
                break;
            case "months":
                dataMap = messageService.getMonthCount(messageList);
                break;
            case "emotes":
                dataMap = messageService.getEmojiCount(messageList);
                break;
            default:
                return "redirect:/starter/files";
        }
        model.addAttribute("dataMap", dataMap);
        model.addAttribute("dataType", dataType);
        return "messages_execute";
    }

    @GetMapping("/files/pdf/{fileName}")
    public String pdf(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        File file = new File(dwPath + fileName);
        if (!file.exists()) {
            throw new ResourceNotFoundException("Nie znaleziono pliku " + fileName);
        }
        pdfService.createPDF(response, file, fileName);
        return "redirect:/starter/files";
    }


    @GetMapping("/files/delete/{fileName}")
    public String deleteFile(@PathVariable String fileName) {
        File file = new File(dwPath + fileName);
        if (!file.exists()) {
            throw new ResourceNotFoundException("Nie znaleziono pliku " + fileName);
        }
        fileService.fileDelete(file);
        return "redirect:/starter/files";
    }
}

