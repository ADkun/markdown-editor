package com.adkun.markdown.controller.advice;

import com.adkun.markdown.common.ErrorCode;
import com.adkun.markdown.common.MdProjException;
import com.adkun.markdown.common.ResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常处理
 * @author adkun
 */
@RestControllerAdvice
@Slf4j
public class ExceptionAdvice implements ErrorCode {

    @ExceptionHandler
    public ResponseModel handleException(Exception e) {
        Map<Object, Object> data = new HashMap<>();
        if (e instanceof MdProjException) {
            MdProjException mdExc = (MdProjException) e;
            data.put("code", mdExc.getCode());
            data.put("message", mdExc.getMessage());
        } else {
            data.put("code", UNDEFINED_ERROR);
            data.put("message", "发生未知错误！");
            log.error("发生未知错误！" + e.getMessage());
        }

        return new ResponseModel(ResponseModel.STATUS_FAILURE, data);
    }
}
