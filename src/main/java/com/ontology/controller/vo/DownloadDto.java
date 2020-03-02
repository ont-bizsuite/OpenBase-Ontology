package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DownloadDto {
    @ApiModelProperty(name="userId",value = "userId",required = true)
    private String userId;
    @ApiModelProperty(name="dataId",value = "dataId",required = true)
    @NotBlank
    private String dataId;
    @ApiModelProperty(name="honor point amount",value = "荣誉值数量",required = true)
    @NotNull
    private Long amount;
}
