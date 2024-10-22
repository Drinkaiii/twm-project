package com.twm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonalityDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("description")
    private String description;

}
