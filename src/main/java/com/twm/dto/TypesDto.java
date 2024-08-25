package com.twm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TypesDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("type_name")
    private String typeName;
}
