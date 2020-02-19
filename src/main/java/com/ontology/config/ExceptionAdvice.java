package com.ontology.config;

import com.github.ontio.network.exception.RestfulException;
import com.ontology.bean.Result;
import com.ontology.exception.OntIdException;
import com.ontology.utils.ErrorInfo;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestControllerAdvice
public class ExceptionAdvice {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Result validException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, Object> result = this.getValidError(fieldErrors);
        return new Result(HttpStatus.BAD_REQUEST.getReasonPhrase(), ErrorInfo.PARAM_ERROR.code(), result.get("errorMsg").toString(), "");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result validException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, Object> result = this.getValidError(fieldErrors);
        return new Result(HttpStatus.BAD_REQUEST.getReasonPhrase(), ErrorInfo.PARAM_ERROR.code(), result.get("errorMsg").toString(), "");
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(OntIdException.class)
    public Result handle(OntIdException e, HttpServletRequest httpServletRequest) {
        return new Result(e.getAction(), e.getErrCode(), e.getErrDesEN(), "");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result handle(NoHandlerFoundException e) {
        return new Result(HttpStatus.NOT_FOUND.getReasonPhrase(), ErrorInfo.PARAM_ERROR.code(), e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result globalException(HttpServletRequest request, Throwable ex) {
        logger.error("error...", ex);
        return new Result(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ErrorInfo.PARAM_ERROR.code(), ex.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(RestfulException.class)
    public Result RestfulException (HttpServletRequest request, Throwable ex) {
        logger.error("error...", ex);
        return new Result("sendrawtransaction", ErrorInfo.PARAM_ERROR.code(), ex.getMessage(), null);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

    private Map<String, Object> getValidError(List<FieldError> fieldErrors) {
        Map<String, Object> result = new HashMap<String, Object>(16);
        List<String> errorList = new ArrayList<String>();
        StringBuffer errorMsg = new StringBuffer("校验异常(ValidException):");
        for (FieldError error : fieldErrors) {
            errorList.add(error.getField() + "-" + error.getDefaultMessage());
            errorMsg.append(error.getField() + "-" + error.getDefaultMessage() + ".");
        }
        result.put("errorList", errorList);
        result.put("errorMsg", errorMsg);
        return result;
    }
}
