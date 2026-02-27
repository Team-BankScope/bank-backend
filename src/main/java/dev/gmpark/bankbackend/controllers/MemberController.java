package dev.gmpark.bankbackend.controllers;


import dev.gmpark.bankbackend.entities.MemberEntity;
import dev.gmpark.bankbackend.results.CommonResult;
import dev.gmpark.bankbackend.results.TaskResult;
import dev.gmpark.bankbackend.services.TaskService;
import dev.gmpark.bankbackend.vos.TaskVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "멤버(Member)", description = "행원 워크스페이스 관련 api ")
@Controller
@RequestMapping(value = "/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final TaskService taskService;

    @Operation(summary = "행원 업무 조회", description = "로그인한 행원에게 할당된 업무 목록을 조회합니다.")
    @RequestMapping(value = "/task", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TaskVo> getTasks (HttpSession session) {
        MemberEntity member = (MemberEntity) session.getAttribute("member");
        if (member == null) {
            return null;
        }
        return this.taskService.getTasksByMemberId(member.getId().intValue());
    }

    @Operation(summary = "업무 상태 변경", description = "특정 업무의 상태를 변경합니다. (WAITING -> IN_PROGRESS -> COMPLETED)")
    @RequestMapping(value = "/task/{taskId}/status", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> updateTaskStatus(@PathVariable Long taskId, @RequestParam String status, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        if (session.getAttribute("member") == null) {
            response.put("result", TaskResult.FAILURE_SESSION.name());
            return response;
        }
        TaskResult result = this.taskService.updateTaskStatus(taskId, status);
        response.put("result", result.name());
        return response;
    }
}