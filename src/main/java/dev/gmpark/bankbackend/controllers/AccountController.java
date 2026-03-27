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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Tag(name = "계좌(Account)", description = "계좌 관련 api ")
@Controller
@RequestMapping(value = "/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "계좌 등록", description = "손님의 일반 입출금 계좌를 등록합니다. (세션,계좌유형, 계좌별칭, 계좌비밀번호)")
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postAccount(HttpSession session, @RequestParam(value = "userId") Integer userId,
                                           @RequestParam(value = "accountType") String accountType,
                                           @RequestParam(value = "accountAlias") String accountAlias ,
                                           @RequestParam(value = "accountPassword") String accountPassword) {
        Map<String, Object> response = new HashMap<>();

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
    public Map<String, Object> postAccountPassword(HttpSession session, @RequestParam(value = "accountId") Long accountId, @RequestParam(value = "accountPassword") String accountPassword) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user == null) {
            response.put("result", AccountResult.FAILURE.name());
            return response;
        }
        AccountResult result = this.accountService.checkAccountPassword(accountId, accountPassword);
        response.put("result", result.name());
        return response;
    }
    @Operation(summary = "내 계좌 목록 및 잔액 조회", description = "로그인한 사용자의 계좌 목록과 잔액을 조회합니다.")
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getMyAccounts(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        UserEntity user = (UserEntity) session.getAttribute("user");
        
        if (user == null) {
            response.put("result", AccountResult.FAILURE.name());
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        List<AccountEntity> accounts = this.accountService.getMyAccounts(user.getId());
        response.put("result", AccountResult.SUCCESS.name());
        response.put("accounts", accounts);
        
        return response;
    }

    @Operation(summary = "특정 유저의 계좌 목록 조회 (행원용)", description = "유저 ID를 통해 해당 유저의 모든 계좌 목록을 조회합니다.")
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getUserAccounts(@PathVariable("userId") Integer userId) {
        Map<String, Object> response = new HashMap<>();
        
        List<AccountEntity> accounts = this.accountService.getMyAccounts(userId);
        response.put("result", AccountResult.SUCCESS.name());
        response.put("accounts", accounts);
        
        return response;
    }
    
    @Operation(summary = "특정 계좌 잔고 조회", description = "계좌번호로 특정 계좌의 정보 및 잔고를 조회합니다.")
    @RequestMapping(value = "/balance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getAccountBalance(@RequestParam(value = "accountNumber") String accountNumber) {
        Map<String, Object> response = new HashMap<>();
        
        AccountEntity account = this.accountService.getAccountByAccountNumber(accountNumber);
        if (account != null) {
            response.put("result", AccountResult.SUCCESS.name());
            response.put("balance", account.getBalance());
            response.put("accountAlias", account.getAccountAlias());
        } else {
            response.put("result", AccountResult.FAILURE.name());
            response.put("message", "계좌를 찾을 수 없습니다.");
        }
        
        return response;
    }
    // 예금 적금 컨트롤하는 api
    @Operation(summary = "예금 개좌 계설", description = "손님의 예금계좌를 개설합니다.")
    @RequestMapping(value = "/deposit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postDepositAccount() {
        this.accountService.createDepositAccount();
        return null;
    }

    @Operation(summary = "예금 개좌 계설", description = "손님의 적금계좌를 개설합니다.")
    @RequestMapping(value = "/savings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postSavingsAccount() {
        this.accountService.createSavingsAccount();
        return null;
    }

    // 법인 계좌 개설 api


}
