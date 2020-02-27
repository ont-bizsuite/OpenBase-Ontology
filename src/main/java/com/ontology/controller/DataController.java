package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.controller.vo.DownloadDto;
import com.ontology.service.DataService;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api(tags = "dataset api")
@RestController
@RequestMapping("/api/v1/data")
@CrossOrigin
public class DataController {
    @Autowired
    private DataService dataService;

    @ApiOperation(value = "download", notes = "download", httpMethod = "POST")
    @PostMapping("/download")
    public Result download(@Valid @RequestBody DownloadDto dto) throws Exception {
        String action = "download";
        String dataId = dto.getDataId();
        Long amount = dto.getAmount();
        String userId = dto.getUserId();
        String url = dataService.download(action, userId, dataId, amount);
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), url);
    }

}
