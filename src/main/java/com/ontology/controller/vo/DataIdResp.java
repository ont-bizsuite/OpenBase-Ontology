package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DataIdResp {
    @ApiModelProperty(name="userId",value = "userId",required = true)
    @NotBlank
    private String userId;
    @ApiModelProperty(name="dataId",value = "dataId",required = true)
    @NotBlank
    private String dataId;
    @ApiModelProperty(name="version",value = "version")
    private String version;
    @ApiModelProperty(name="dataOntId",value = "dataOntId")
    @NotBlank
    private String dataOntId;
}
