package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class DataIdGroupControllerDto {
    @ApiModelProperty(name="userIdList",value = "userIdList",required = true)
    @NotEmpty
    private List<String> userIdList;
    @ApiModelProperty(name="dataIdList",value = "dataIdList",required = true)
    @NotEmpty
    private List<String> dataIdList;
    @ApiModelProperty(name="version",value = "version")
    private String version;
}
