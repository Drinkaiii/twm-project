package com.twm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecordDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("question")
    private String question;

    @JsonProperty("response")
    private String response;

}
