package dev.gmpark.bankbackend.controllers;


import dev.gmpark.bankbackend.entities.TransactionHistoryEntity;
import dev.gmpark.bankbackend.results.TransactionResult;
import dev.gmpark.bankbackend.services.TransactionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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


@Tag(name = "거래내역(TransactionHistory)", description = "거래내역 관련 API")
@Controller
@RequestMapping(value = "/api/transaction")
@RequiredArgsConstructor
public class TransactionHistoryController {
    private final TransactionHistoryService transactionHistoryService;

    @Operation(summary = "입금", description = "계좌번호와 금액을 받아 입금을 처리합니다.")
    @RequestMapping(value = "/deposit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postDeposit(@RequestParam(value = "accountNumber") String accountNumber,
                                           @RequestParam(value = "amount") Long amount,
                                           @RequestParam(value = "description", required = false, defaultValue = "입금") String description,
                                           @RequestParam(value = "taskId", required = false) Long taskId) {
        Map<String, Object> response = new HashMap<>();
        Pair<TransactionResult, TransactionHistoryEntity> result = this.transactionHistoryService.deposit(accountNumber, amount, description, taskId);
        response.put("result", result.getLeft().name());
        if (result.getLeft() == TransactionResult.SUCCESS) {
            response.put("transaction", result.getRight());
        }
        return response;
    }

    @Operation(summary = "출금", description = "계좌번호, 비밀번호, 금액을 받아 출금을 처리합니다.")
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST, produces =  MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postWithdraw(@RequestParam(value = "accountNumber") String accountNumber,
                                            @RequestParam(value = "accountPassword") String accountPassword,
                                            @RequestParam(value = "amount") Long amount,
                                            @RequestParam(value = "description", required = false, defaultValue = "출금") String description,
                                            @RequestParam(value = "taskId", required = false) Long taskId) {
        Map<String, Object> response = new HashMap<>();
        Pair<TransactionResult, TransactionHistoryEntity> result = this.transactionHistoryService.withdraw(accountNumber, accountPassword, amount, description, taskId);
        response.put("result", result.getLeft().name());
        if (result.getLeft() == TransactionResult.SUCCESS) {
            response.put("transaction", result.getRight());
        }
        return response;
    }

    @Operation(summary = "이체하기", description = "출금계좌, 비밀번호, 입금계좌, 금액을 받아 이체를 처리합니다.")
    @RequestMapping(value = "/transfer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postTransfer(@RequestParam(value = "fromAccountNumber") String fromAccountNumber,
                                            @RequestParam(value = "accountPassword") String accountPassword,
                                            @RequestParam(value = "toAccountNumber") String toAccountNumber,
                                            @RequestParam(value = "amount") Long amount,
                                            @RequestParam(value = "description", required = false, defaultValue = "이체") String description,
                                            @RequestParam(value = "taskId", required = false) Long taskId) {
        Map<String, Object> response = new HashMap<>();
        Pair<TransactionResult, TransactionHistoryEntity> result = this.transactionHistoryService.transfer(fromAccountNumber, accountPassword, toAccountNumber, amount, description, taskId);
        response.put("result", result.getLeft() != null ? result.getLeft().name() : "FAILURE");
        if (result.getLeft() == TransactionResult.SUCCESS) {
            response.put("transaction", result.getRight());
        }
        return response;
    }
    // 이체하기 웹사이트용 핀번호로 검증 (간편인증)

}
