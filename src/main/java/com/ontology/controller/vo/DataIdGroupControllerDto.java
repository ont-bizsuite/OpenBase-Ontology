package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DataIdGroupControllerDto {
    @ApiModelProperty(name = "userIdList", value = "userIdList", required = true)
    @NotEmpty
    private List<String> userIdList;
    @ApiModelProperty(name = "dataIdList", value = "dataIdList", required = true)
    @NotEmpty
    private List<String> dataIdList;
    @ApiModelProperty(name = "version", value = "version")
    private String version;
    @ApiModelProperty(name = "isDataset", value = "isDataset:0-false;1-true")
    @NotNull
    @Range(min = 0, max = 1)
    private Integer isDataset;
}
