package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DataIdDto {
    @ApiModelProperty(name="userId",value = "userId",required = true)
    @NotBlank
    private String userId;
    @ApiModelProperty(name="dataId",value = "dataId",required = true)
    @NotBlank
    private String dataId;
    @ApiModelProperty(name="newVersion",value = "newVersion",required = true)
    @NotBlank
    private String newVersion;

    @ApiModelProperty(name="oldVersion",value = "oldVersion")
    private String oldVersion;
}
