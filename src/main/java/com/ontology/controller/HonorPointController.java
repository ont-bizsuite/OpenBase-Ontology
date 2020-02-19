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


@Api(tags = "honor point api")
@RestController
@RequestMapping("/api/v1/honor-point")
@CrossOrigin
public class HonorPointController {
    @Autowired
    private HonorPointService honorPointService;

    @ApiOperation(value = "register", notes = "register", httpMethod = "GET")
    @GetMapping
    public Result queryPoint(String userId) throws Exception {
        String action = "queryPoint";
        Long point = honorPointService.queryPoint(action, userId);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), point);
    }

    @ApiOperation(value = "register", notes = "register", httpMethod = "POST")
    @PostMapping
    public Result distribute(@Valid @RequestBody PointDto req) throws Exception {
        String action = "distributePoint";
        String dataId = req.getDataId();
        String version = req.getVersion() == null ? "" : req.getVersion();
        Long amount = req.getAmount();
        String userId = req.getUserId();
        String hash = honorPointService.distribute(action, userId, dataId, version, amount);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ErrorInfo.SUCCESS.descEN());
    }

}
