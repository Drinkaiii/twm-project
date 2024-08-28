package com.twm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReturnCategoryDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("category")
    private String category;

}