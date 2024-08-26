package com.twm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReturnQuestionDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("question")
    private String question;

}

