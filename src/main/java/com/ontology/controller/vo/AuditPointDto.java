package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AuditPointDto {
    @ApiModelProperty(name="userIds",value = "auditor userId List",required = true)
    @NotEmpty
    private List<String> userIds;
    @ApiModelProperty(name="dataId",value = "dataId",required = true)
    @NotBlank
    private String dataId;
    @ApiModelProperty(name="version",value = "version",required = true)
    private String version;
    @ApiModelProperty(name="amount",value = "amount",required = true)
    @NotNull
    private Long amount;
}
