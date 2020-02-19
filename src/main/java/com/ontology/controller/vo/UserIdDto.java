package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserIdDto {
    @ApiModelProperty(name="userId",value = "userId",required = true)
    @NotBlank
    private String userId;
}
