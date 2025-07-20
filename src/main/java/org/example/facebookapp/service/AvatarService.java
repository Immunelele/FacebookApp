package org.example.facebookapp.service;

import lombok.RequiredArgsConstructor;
import org.example.facebookapp.model.Participant;
import org.example.facebookapp.model.ParticipantData;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private final RestTemplate restTemplate;
    private final MessageService messageService;

    public Map<Participant, byte[]> getAvatars() {
        List<Participant> participantsList = messageService.getParticipantsFromFile();
        Map<Participant, byte[]> avatarMap = new HashMap<>();

        for (Participant participant : participantsList) {
            try {
                String encodedName = UriUtils.encode(participant.getName(), StandardCharsets.UTF_8);
                String url = "https://eu.ui-avatars.com/api/" + "?name=" + encodedName + "&background=random&size=128";

                ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, null, byte[].class);
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    avatarMap.put(participant, response.getBody());
                }
            } catch (Exception e) {
                System.err.println("Error getting avatar for " + participant.getName() + "Exception message" + e.getMessage());
            }
        }
        return avatarMap;
    }

    public Map<String, ParticipantData> getParticipantsData(File file) {
        Map<String, Long> participantsMessageCount = messageService.getParticipantsMessageCount(file);
        Map<Participant, byte[]> avatarMap = getAvatars();
        Map<String, ParticipantData> resultMap = new HashMap<>();

        for (Map.Entry<String, Long> entry : participantsMessageCount.entrySet()) {
            String participantName = entry.getKey();
            Long count = entry.getValue();
            String avatarBase64 = null;

            for (Map.Entry<Participant, byte[]> avatarEntry : avatarMap.entrySet()) {
                if (avatarEntry.getKey().getName().equals(participantName)) {
                    byte[] avatarBytes = avatarEntry.getValue();
                    if (avatarBytes != null) {
                        avatarBase64 = Base64.getEncoder().encodeToString(avatarBytes);
                    }
                    break;
                }
            }
            resultMap.put(participantName, new ParticipantData(count, avatarBase64));
        }
        return resultMap;
    }
}