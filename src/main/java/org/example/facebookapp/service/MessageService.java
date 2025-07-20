package org.example.facebookapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.facebookapp.model.Message;
import org.example.facebookapp.model.Participant;
import org.example.facebookapp.model.Root;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final FileService fileService;
    private final ObjectMapper objectMapper;

    public List<Message> getMessageFromFile(File file) {
        List<Message> messages = new ArrayList<>();
        try {
            String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            Root root = objectMapper.readValue(json, Root.class);

            if (root.getMessages() != null) {
                for (Message message : root.getMessages()) {
                    message.setContent(fixEncoding(message.getContent()));
                    message.setSenderName(fixEncoding(message.getSenderName()));
                    messages.add(message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas wczytywania pliku: " + file.getName(), e);
        }
        return messages;
    }

    public List<Participant> getParticipantsFromFile() {
        List<Participant> participants = new ArrayList<>();
        List<File> fileList = fileService.findFilesBySuffix(".json");

        for (File file : fileList) {
            try {
                String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                Root root = objectMapper.readValue(json, Root.class);

                if (root.getParticipants() != null) {
                    for (Participant participant : root.getParticipants()) {
                        participant.setName(fixEncoding(participant.getName()));
                        participants.add(participant);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Błąd podczas wczytywania pliku: " + file.getName(), e);
            }
        }
        return participants;
    }

    public Map<String, Integer> getParticipantsSizeFromFile() {
        Map<String, Integer> participantsSizeByFile = new LinkedHashMap<>();
        List<File> fileList = fileService.findFilesBySuffix(".json");

        for (File file : fileList) {
            try {
                String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                Root root = objectMapper.readValue(json, Root.class);

                if (root.getParticipants() != null) {
                    for (Participant participant : root.getParticipants()) {
                        participant.setName(fixEncoding(participant.getName()));
                        participantsSizeByFile.merge(file.getName(), 1, Integer::sum);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Błąd podczas wczytywania pliku: " + file.getName(), e);
            }
        }
        return participantsSizeByFile;
    }

    private String fixEncoding(String input) {
        if (input == null) return null;
        try {
            return new String(input.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return input;
        }
    }

    public Map<String, Long> getParticipantsMessageCount(File file) {
        Map<String, Long> participantsMessageCount = new HashMap<>();
        List<Message> messages = getMessageFromFile(file);

        for (Message message : messages) {
            participantsMessageCount.merge(message.getSenderName(), 1L, Long::sum);
        }
        return participantsMessageCount;
    }

    public Map<String, Long> getDayOfTheWeekCount(List<Message> messages) {
        Map<String, Long> dayOfTheWeekCountMap = new HashMap<>();
        for (Message message : messages) {
            String dateInString = fileService.convertLongToDayOfTheWeek((Long) message.getTimestampMs());
            dayOfTheWeekCountMap.put(dateInString, dayOfTheWeekCountMap.getOrDefault(dateInString, 0L) + 1);
        }
        return dayOfTheWeekCountMap;
    }

    public Map<String, Long> getDayOfTheMonthCount(List<Message> messages) {
        Map<String, Long> dayOfTheMonthCountMap = new TreeMap<>();
        for (Message message : messages) {
            String dateInString = fileService.convertLongToDayOfTheMonth((Long) message.getTimestampMs());
            dayOfTheMonthCountMap.put(dateInString, dayOfTheMonthCountMap.getOrDefault(dateInString, 0L) + 1);
        }
        return dayOfTheMonthCountMap;
    }

    public Map<String, Long> getYearCount(List<Message> messages) {
        Map<String, Long> yearCountMap = new TreeMap<>();
        for (Message message : messages) {
            String dateInString = fileService.convertLongToYear((Long) message.getTimestampMs());
            yearCountMap.put(dateInString, yearCountMap.getOrDefault(dateInString, 0L) + 1);
        }
        return yearCountMap;
    }

    public Map<String, Long> getMonthCount(List<Message> messages) {
        Map<String, Long> monthCountMap = new HashMap<>();
        for (Message message : messages) {
            String dateInString = fileService.convertLongToMonth((Long) message.getTimestampMs());
            monthCountMap.put(dateInString, monthCountMap.getOrDefault(dateInString, 0L) + 1);
        }
        return monthCountMap;
    }

    public int getWordsCount(List<Message> messages) {
        if (StringUtils.isBlank("true")) {
            System.out.println("Lista wiadomości jest pusta");
        }

        int totalWords = 0;
        for (Message message : messages) {
            if (message.getContent() != null) {
                String text = fixEncoding(message.getContent().trim());
                if (!text.isEmpty()) {
                    totalWords += text.split("\\s+").length;
                }
            }
        }
        return totalWords;
    }

    public String correctEncoding(String readString) {
        if (StringUtils.isBlank(readString)) {
            return "";
        }
        return readString
                .replaceAll("\\u00c4\u0085", "ą").replaceAll("\\u00c4\u0084", "Ą")
                .replaceAll("\\u00c4\u0087", "ć").replaceAll("\\u00c4\u0086", "Ć")
                .replaceAll("\\u00c4\u0099", "ę").replaceAll("\\u00c4\u0098", "Ę")
                .replaceAll("\\u00c5\u0082", "ł").replaceAll("\\u00c5\u0081", "Ł")
                .replaceAll("\\u00c5\u0084", "ń").replaceAll("\\u00c5\u0083", "Ń")
                .replaceAll("\\u00c3\u00b3", "ó").replaceAll("\\u00c3\u0093", "Ó")
                .replaceAll("\\u00c5\u009b", "ś").replaceAll("\\u00c5\u009a", "Ś")
                .replaceAll("\\u00c5\u00bc", "ż").replaceAll("\\u00c5\u00b9", "Ż")
                .replaceAll("\\u00c5\u00ba", "ź").replaceAll("\\u00c5\u00bb", "Ź")
                .replaceAll("\\u00f0\\u009f\\u0098\\u0080", "\uD83D\uDE00")
                .replaceAll("\\u00f0\\u009f\\u0098\\u0081", "\uD83D\uDE01")
                .replaceAll("\\u00f0\\u009f\\u0098\\u0082", "\uD83D\uDE02")
                .replaceAll("\\u00f0\\u009f\\u00a4\\u00a3", "\uD83E\uDD23")
                .replaceAll("\\u00f0\\u009f\\u0098\\u0083", "\uD83D\uDE03")
                .replaceAll("\\u00f0\\u009f\\u0098\\u0084", "\uD83D\uDE04")
                .replaceAll("\\u00f0\\u009f\\u0098\\u0085", "\uD83D\uDE05")
                .replaceAll("\\u00f0\\u009f\\u0098\\u0086", "\uD83D\uDE06")
                .replaceAll("\\u00f0\\u009f\\u0098\\u0089", "\uD83D\uDE09")
                .replaceAll("\\u00f0\\u009f\\u0098\\u008a", "\uD83D\uDE0A")
                .replaceAll("\\u00f0\\u009f\\u0098\\u008e", "\uD83D\uDE0E")
                .replaceAll("\\u00f0\\u009f\\u0098\\u008d", "\uD83D\uDE0D")
                .replaceAll("\\u00f0\\u009f\\u0098\\u0098", "\uD83D\uDE18")
                .replaceAll("\\u00f0\\u009f\\u0098\\u009c", "\uD83D\uDE1C")
                .replaceAll("\\u00f0\\u009f\\u0098\\u00ad", "\uD83D\uDE2D")
                .replaceAll("\\u00f0\\u009f\\u0098\\u00a1", "\uD83D\uDE21")
                .replaceAll("\\u00f0\\u009f\\u00a4\\u0094", "\uD83E\uDD14")
                .replaceAll("\\u00f0\\u009f\\u00a4\\u0097", "\uD83E\uDD17")
                .replaceAll("\\u00f0\\u009f\\u00a4\\u00a9", "\uD83E\uDD29")
                .replaceAll("\\u00f0\\u009f\\u00a4\\u00af", "\uD83E\uDD2F")
                .replaceAll("\\u00f0\\u009f\\u00a5\\u00b3", "\uD83E\uDD2F")
                .replaceAll("\\u00f0\\u009f\\u00a5\\u00ba", "\uD83E\uDD7A")
                .replaceAll("\\u00f0\\u009f\\u00a4\\u00ad", "\uD83E\uDD2D")
                .replaceAll("\\u00f0\\u009f\\u00a4\\u00ab", "\uD83E\uDD2B")
                .replaceAll("\\u00f0\\u009f\\u0091\\u008d", "\uD83D\uDC4D")
                .replaceAll("\\u00f0\\u009f\\u00a4\\u00a1", "\uD83E\uDD21")
                .replaceAll("\\u00f0\\u009f\\u0092\\u0080", "\uD83D\uDC80")
                .replaceAll("\\u00e2\\u0098\\u00a0", "☠")
                .replaceAll("\\u00f0\\u009f\\u0091\\u00bb", "\uD83D\uDC7B")
                .replaceAll("\\u00f0\\u009f\\u0094\\u00a5", "\uD83D\uDD25")
                .replaceAll("\\u00f0\\u009f\\u008e\\u0083", "\uD83C\uDF83")
                .replaceAll("\\u00e2\\u009d\\u00a4", "❤\uFE0F")
                .replaceAll("\\u00f0\\u009f\\u0092\\u0094", "\uD83D\uDC94")
                .replaceAll("\\u00e2\\u009b\\u0080", "⭐")
                .replaceAll("\\u00f0\\u009f\\u0092\\u00af", "\uD83D\uDCAF")
                .replaceAll("\\u00e2\\u009a\\u00bd", "⚽");
    }

    public Map<String, Long> getEmojiCount(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            System.out.println("Lista wiadomości jest pusta");
            return null;
        }

        Pattern pattern = Pattern.compile("[\\p{So}\\p{Cs}]");

        Map<String, Long> emojiCountMap = new HashMap<>();
        for (Message message : messages) {
            if (message.getContent() != null) {
                String text = correctEncoding(message.getContent().trim());
                if (StringUtils.isNotBlank(text)) {
                    Matcher matcher = pattern.matcher(text);
                    while (matcher.find()) {
                        emojiCountMap.put(matcher.group(), emojiCountMap.getOrDefault(matcher.group(), 0L) + 1);
                    }
                }
            }
        }
        return emojiCountMap;
    }

    public int getTotalMessageCount(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            System.out.println("Lista wiadomości jest pusta");
            return 0;
        }
        return messages.size();
    }
}