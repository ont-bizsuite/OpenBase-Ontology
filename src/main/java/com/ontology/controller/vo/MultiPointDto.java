package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MultiPointDto {
    @ApiModelProperty(name="userId",value = "userId",required = true)
    @NotBlank
    private String userId;
    @ApiModelProperty(name="dataId",value = "dataId list",required = true)
    @NotEmpty
    private List<DataVersionDto> dataIds;
    @ApiModelProperty(name="amount",value = "amount",required = true)
    @NotNull
    private Long amount;
}
