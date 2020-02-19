package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PointDto {
    @ApiModelProperty(name="userId",value = "userId",required = true)
    @NotBlank
    private String userId;
    @ApiModelProperty(name="dataId",value = "dataId",required = true)
    @NotBlank
    private String dataId;
    @ApiModelProperty(name="version",value = "version",required = true)
    private String version;
    @ApiModelProperty(name="amount",value = "amount",required = true)
    @NotNull
    private Long amount;
}
