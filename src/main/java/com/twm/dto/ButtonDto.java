package com.twm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ButtonDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("type_id")
    private Long typeId;

    @JsonProperty("question")
    private String question;

    @JsonProperty("answer")
    private String answer;
}
