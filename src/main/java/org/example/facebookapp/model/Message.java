package org.example.facebookapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class Message {
    @JsonProperty("sender_name")
    private String senderName;
    @JsonProperty("timestamp_ms")
    private Object timestampMs;
    private String content;
}
