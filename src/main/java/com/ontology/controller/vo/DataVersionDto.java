package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DataVersionDto {
    @ApiModelProperty(name="dataId",value = "dataId",required = true)
    @NotBlank
    private String dataId;
    @ApiModelProperty(name="version",value = "version",required = true)
    private String version;
}
