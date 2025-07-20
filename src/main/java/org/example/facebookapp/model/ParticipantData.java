package org.example.facebookapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class ParticipantData {
    private Long messageCount;
    private String avatarBase64;
}