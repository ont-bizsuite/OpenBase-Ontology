package com.ontology.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Result {
    @ApiModelProperty(name="action",value = "动作标志",required = true)
    public String action;
    @ApiModelProperty(name="error",value = "错误码",required = true)
    public int error;
    @ApiModelProperty(name="desc",value = "错误描述",required = true)
    public String desc;
    @ApiModelProperty(name="result",value = "返回结果",required = true)
    public Object result;
    @ApiModelProperty(name="version",value = "版本号",required = true)
    public String version;

    public Result() {
    }

    public Result(String action, int error, String desc, Object result) {
        this.action = action;
        this.error = error;
        this.desc = desc;
        this.result = result;
        this.version = "v1";
    }

}
