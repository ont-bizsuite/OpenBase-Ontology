package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.service.*;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;


@Api(tags = "honor point api")
@RestController
@RequestMapping("/api/v1/honor-point")
@CrossOrigin
public class HonorPointController {
    @Autowired
    private HonorPointService honorPointService;

    @ApiOperation(value = "queryPoint", notes = "queryPoint", httpMethod = "GET")
    @GetMapping
    public Result queryPoint(String userId) throws Exception {
        String action = "queryPoint";
        Long point = honorPointService.queryPoint(action, userId);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), point);
    }

    @ApiOperation(value = "distributePoint", notes = "distributePoint", httpMethod = "POST")
    @PostMapping
    public Result distribute(@Valid @RequestBody PointDto dto) throws Exception {
        String action = "distributePoint";
        String dataId = dto.getDataId();
        String version = dto.getVersion() == null ? "" : dto.getVersion();
        Long amount = dto.getAmount();
        String userId = dto.getUserId();
        String hash = honorPointService.distribute(action, userId, dataId, version, amount);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ErrorInfo.SUCCESS.descEN());
    }

    @ApiOperation(value = "distribute Multi Point", notes = "distribute Multi Point", httpMethod = "POST")
    @PostMapping
    public Result distributeMulti(@Valid @RequestBody MultiPointDto dto) throws Exception {
        String action = "distributeMultiPoint";
        List<DataVersionDto> dataIds = dto.getDataIds();
        Long amount = dto.getAmount();
        String userId = dto.getUserId();
        String hash = honorPointService.distributeMulti(action, userId, dataIds, amount);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ErrorInfo.SUCCESS.descEN());
    }

}
