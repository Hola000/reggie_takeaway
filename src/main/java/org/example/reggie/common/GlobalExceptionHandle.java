package org.example.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandle {
    @ExceptionHandler
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String msg = ex.getMessage();
        log.info("error massage:{}",msg);
        if (msg.contains("Duplicate entry")) {
            String[] str = msg.split(" ");
            return R.error("user " + str[2] +" has already exists");
        }
        return R.error("unknown error");
    }
}
