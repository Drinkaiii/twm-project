package com.twm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateButtonDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("type")
    private Long type;

    @JsonProperty("question")
    private String question;

    @JsonProperty("answer")
    private String answer;

}
