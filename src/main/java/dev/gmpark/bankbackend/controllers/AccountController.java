package dev.gmpark.bankbackend.controllers;


import dev.gmpark.bankbackend.entities.AccountEntity;
import dev.gmpark.bankbackend.entities.MemberEntity;
import dev.gmpark.bankbackend.entities.UserEntity;
import dev.gmpark.bankbackend.results.AccountResult;
import dev.gmpark.bankbackend.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@Tag(name = "계좌(Account)", description = "계좌 관련 api ")
@Controller
@RequestMapping(value = "/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "계좌 등록", description = "손님의 계좌를 등록합니다. (세션,계좌유형, 계좌별칭, 계좌비밀번호)")
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postAccount(HttpSession session, @RequestParam(value = "userId") Integer userId,
                                           @RequestParam(value = "accountType") String accountType,
                                           @RequestParam(value = "accountAlias") String accountAlias ,
                                           @RequestParam(value = "accountPassword") String accountPassword) {
        Map<String, Object> response = new HashMap<>();
/*        MemberEntity member = (MemberEntity) session.getAttribute("member");
        if(member == null) {
            response.put("result", AccountResult.FAILURE.name());
            return response;
        }*/
        Pair<AccountResult, AccountEntity> result = this.accountService.createAccount(userId, accountType, accountAlias, accountPassword);
        response.put("result", result.getLeft().name());
        if (result.getLeft() == AccountResult.SUCCESS) {
            response.put("account", result.getRight());
        }
        return response;
    }
    @Operation(summary = "통장비밀번호 일치조회", description = "손님의 통장비밀번호의 일치여부를 조회합니다.")
    @RequestMapping(value = "/account-password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postAccountPassword(HttpSession session, String accountPassword) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        AccountResult result = this.accountService.checkAccountPassword(user.getId(), accountPassword);
        response.put("result", result);
        return response;
    }

}
