package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.controller.vo.DataIdDto;
import com.ontology.controller.vo.DataIdGroupControllerDto;
import com.ontology.controller.vo.DataIdResp;
import com.ontology.controller.vo.UserIdDto;
import com.ontology.service.OntIdService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Slf4j
@Api(tags = "OntId api")
@RestController
@RequestMapping("/api/v1/ont-id")
@CrossOrigin
public class OntIdController {

    @Autowired
    private OntIdService ontIdService;

    @ApiOperation(value = "register user ont id", notes = "register user ont id", httpMethod = "POST")
    @PostMapping("/user")
    public Result registerUserOntId(@Valid @RequestBody UserIdDto dto) throws Exception {
        String action = "registerUserOntId";
        String userId = dto.getUserId();
        String userOntId = ontIdService.registerUserOntId(action, userId);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), userOntId);
    }

    @ApiOperation(value = "register data ont id", notes = "register data ont id", httpMethod = "POST")
    @PostMapping("/data")
    public Result registerDataOntId(@Valid @RequestBody DataIdDto dto) throws Exception {
        String action = "registerDataOntId";
        String userId = dto.getUserId();
        String dataId = dto.getDataId();
        String version = dto.getNewVersion() == null ? "" : dto.getNewVersion();
        String dataOntId = ontIdService.registerDataOntId(action, userId, dataId, version);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), dataOntId);
    }

    @ApiOperation(value = "register multi data ont id", notes = "register multi data ont id", httpMethod = "POST")
    @PostMapping("/data/multi")
    public Result registerMultiDataOntId(@Valid @RequestBody List<DataIdDto> list) throws Exception {
        String action = "registerMultiDataOntId";
        List<DataIdResp> dataOntId = ontIdService.registerMultiDataOntId(action, list);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), dataOntId);
    }

    @ApiOperation(value = "register multi data ont id with group controller", notes = "register multi data ont id with group controller", httpMethod = "POST")
    @PostMapping("/data/multi/group")
    public Result registerMultiDataOntId(@Valid @RequestBody DataIdGroupControllerDto dto) throws Exception {
        String action = "registerMultiDataOntIdWithGroupController";
        @NotEmpty List<String> userIdList = dto.getUserIdList();
        @NotEmpty List<String> dataIdList = dto.getDataIdList();
        String version = dto.getVersion() == null ? "" : dto.getVersion();
        List<DataIdResp> dataOntId = ontIdService.registerMultiDataOntIdWithGroupController(action, userIdList, dataIdList, version);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), dataOntId);
    }

    @ApiOperation(value = "update data ont id", notes = "update data ont id", httpMethod = "PUT")
    @PutMapping("/data")
    public Result updateDataOntId(@Valid @RequestBody DataIdDto dto) throws Exception {
        String action = "updateDataOntId";
        String userId = dto.getUserId();
        String dataId = dto.getDataId();
        String newVersion = dto.getNewVersion();
        String oldVersion = dto.getOldVersion() == null ? "" : dto.getOldVersion();
        String dataOntId = ontIdService.updateDataOntId(action, userId, dataId, newVersion, oldVersion);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), dataOntId);
    }
}
