package com.example.chatting_study.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CustomControllerAdvice {


    @ExceptionHandler(CustomJWTException.class)
    protected ResponseEntity<?> handleJWTException(CustomJWTException e) {

        String msg = e.getMessage();
        log.error("CustomJWTException: {}", msg);

        return ResponseEntity.ok().body(getErrorMessage(msg));
    }

    @ExceptionHandler(NotAccessChatRoom.class)
    public ResponseEntity<?> NotAccessChattingRoom(NotAccessChatRoom e) {
        String msg = e.getMessage();
        log.error("NotAccessChattingRoom: {}", msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorMessage(msg));
    }

    private static Map<String, String> getErrorMessage(String msg) {
        return Map.of("errMsg", msg);
    }
}
