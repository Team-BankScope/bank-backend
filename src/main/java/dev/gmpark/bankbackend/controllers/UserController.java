package dev.gmpark.bankbackend.controllers;

import dev.gmpark.bankbackend.entities.MemberEntity;
import dev.gmpark.bankbackend.entities.UserEntity;
import dev.gmpark.bankbackend.results.CommonResult;
import dev.gmpark.bankbackend.services.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Tag(name = "회원(User)", description = "회원 가입 및 로그인 관련 API")
@Controller
@RequestMapping(value = "/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입", description = "이름, 이메일, 비밀번호, 주민번호를 받아 회원을 등록합니다.")
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postUserRegister(@RequestBody UserEntity user) {
        CommonResult result = this.userService.register(user);
        Map<String, Object> response = new HashMap<>();
        response.put("result", result.name());
        return response;
    }
    @Operation(summary = "멤버 등록", description = "멤버 정보를 받아 등록합니다.")
    @RequestMapping(value = "/member", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postMemberRegister(@RequestBody MemberEntity member) {
        CommonResult result = this.userService.registerMember(member);
        Map<String, Object> response = new HashMap<>();
        response.put("result", result.name());
        return response;
    }

    @Operation(summary = "멤버 수정", description = "멤버 정보를 받아 수정합니다.")
    @RequestMapping(value = "/member", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> patchMember(@RequestBody MemberEntity member) {
        CommonResult result = this.userService.modifyMember(member);
        Map<String, Object> response = new HashMap<>();
        response.put("result", result.name());
        return response;
    }

    @Operation(summary = "멤버 목록 조회", description = "모든 멤버 정보를 조회합니다.")
    @RequestMapping(value = "/members", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<MemberEntity> getMembers() {
        return this.userService.getMembers();
    }

    @Operation(summary = "로그인", description = "이메일, 비밀번호, 주민번호를 받아 로그인합니다.")
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postLogin(@Param(value = "email") String email, @Param(value = "password") String password, @Param(value = "residentNumber") String residentNumber, HttpSession session) {
        UserEntity user = this.userService.login(email, password, residentNumber);
        Map<String, Object> response = new HashMap<>();
        if (user != null && "customer".equals(user.getUserType())) {
            response.put("result", CommonResult.SUCCESS.name());
            session.setAttribute("user", user);
        } else {
            response.put("result", CommonResult.FAILURE.name());
        }
        return response;
    }

    @Operation(summary = "관리자 로그인", description = "이메일, 비밀번호를 받아 관리자 로그인을 합니다.")
    @RequestMapping(value = "/login-admin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postAdminLogin(@Param(value = "email") String email, @Param(value = "password") String password, HttpSession session) {
        UserEntity user = this.userService.loginAdmin(email, password);
        Map<String, Object> response = new HashMap<>();
        if (user != null && "admin".equals(user.getUserType())) {
            response.put("result", CommonResult.SUCCESS.name());
            session.setAttribute("user", user);
        } else {
            response.put("result", CommonResult.FAILURE.name());
        }
        return response;
    }


    @Operation(summary = "세션 확인", description = "현재 로그인된 사용자의 정보를 반환합니다.")
    @RequestMapping(value = "/session", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getSession(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user != null) {
            response.put("result", "SUCCESS");
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("residentNumber", user.getResidentNumber());
        } else {
            response.put("result", "FAILURE");
        }
        return response;
    }
    @Operation(summary = "로그아웃", description = "세션을 만료시킵니다.")
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> postLogout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("result", "SUCCESS");
        return response;
    }
}