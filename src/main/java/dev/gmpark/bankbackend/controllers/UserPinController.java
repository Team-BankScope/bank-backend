package dev.gmpark.bankbackend.controllers;


import dev.gmpark.bankbackend.entities.UserEntity;
import dev.gmpark.bankbackend.results.CommonResult;
import dev.gmpark.bankbackend.services.UserPinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "디지털 핀번호", description ="웹사이트 핀번호 관리 API")
@Controller
@RequestMapping(value = "/api/pin")
@RequiredArgsConstructor
public class UserPinController {
    private final UserPinService userPinService;
    // 핀번호 등록 api SMS인증후에 가능

    // 핀번호 생성
    @Operation(summary = "핀번호 생성", description = "유저의 핀번호를 생성합니다.")
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postUserPin(HttpSession session, @RequestParam("pin") String pin) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return Map.of("result", CommonResult.FAILURE_SESSION.name(), "message", "로그인이 필요합니다.");
        }
        CommonResult result = this.userPinService.createUserPin(user, pin);
        response.put("result", result.name());
        if (result == CommonResult.FAILURE) {
            response.put("message", "인증이 완료되지 않았거나 유효하지 않은 핀 번호입니다.");
        }
        
        return response;
    }
    @Operation(summary = "핀번호 재설정", description = "유저의 핀번호를 재설정 합니다.")
    @RequestMapping(value = "/", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> patchUserPin(HttpSession session, @RequestParam("pin") String pin) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return Map.of("result", CommonResult.FAILURE_SESSION.name(), "message", "로그인이 필요합니다.");

        }
        CommonResult result = this.userPinService.updateUserPin(user, pin);
        if ( result.name() == CommonResult.SUCCESS.name()) {
            response.put("result", result.name());
        }
        return response;
    }
    // 핀번호 삭제
    @Operation(summary = "핀번호 삭제", description = "유저가 등록한 핀번호를 삭제합니다.")
    @RequestMapping(value = "/", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> deleteUserPin(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return Map.of("result", CommonResult.FAILURE_SESSION.name(), "message", "로그인이 필요합니다.");
        }
        CommonResult serviceResult = this.userPinService.deleteUserPin(user);
        response.put("result", serviceResult.name());
        return response;
    }
    @Operation(summary = "핀번호 일치조회", description = "유저가 핀번호 등록한 핀번호를 일치조회 여부를 확인합니다.")
    @RequestMapping(value = "/confirm", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> confirmUserPin(HttpSession session, @RequestParam("pin") String pin) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            return Map.of("result", CommonResult.FAILURE_SESSION.name(), "message", "로그인이 필요합니다.");
        }
        CommonResult result = this.userPinService.checkUserPin(user, pin);
        if ( result.equals(CommonResult.SUCCESS) ) {
            response.put("result", CommonResult.SUCCESS.name());
        }else{
            response.put("result", CommonResult.FAILURE.name());
        }
        return response;
    }

}
