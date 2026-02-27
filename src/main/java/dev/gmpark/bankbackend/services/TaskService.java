package dev.gmpark.bankbackend.services;

import dev.gmpark.bankbackend.dtos.TaskRequestDto;
import dev.gmpark.bankbackend.entities.TaskEntity;
import dev.gmpark.bankbackend.enums.TaskStatus;
import dev.gmpark.bankbackend.mappers.TaskMapper;
import dev.gmpark.bankbackend.results.TaskResult;
import dev.gmpark.bankbackend.vos.TaskVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskMapper taskMapper;


    // TODO: ai 설정 및 스케줄링 알고리즘 추가예정
    @Transactional
    public TaskResult createTask(TaskRequestDto requestDto, String userEmail) {
        String taskType = requestDto.getTaskType();
        String prefix;
        String assignedLevel;
        int processingTime;
        int minLevel;

        // 1. 업무 유형별 설정
        if ("빠른 업무".equals(taskType)) {
            prefix = "A";
            assignedLevel = "LEVEL_1";
            processingTime = 5;
            minLevel = 1;
        } else if ("상담 업무".equals(taskType)) {
            prefix = "B";
            assignedLevel = "LEVEL_2";
            processingTime = 10;
            minLevel = 3;
        } else { // 기업 • 특수
            prefix = "C";
            assignedLevel = "LEVEL_3";
            processingTime = 25;
            minLevel = 5;
        }

        // 2. 대기표 번호 생성 (A-001)
        String lastTicket = taskMapper.selectLastTicketNumber(prefix);
        int nextNum = 1;
        if (lastTicket != null) {
            String numPart = lastTicket.split("-")[1];
            nextNum = Integer.parseInt(numPart) + 1;
        }
        String ticketNumber = String.format("%s-%03d", prefix, nextNum);

        // 3. 대기 인원 및 예상 대기 시간 계산
        int waitingCount = taskMapper.countWaitingTasks(taskType);
        int expectedWaitingTime = waitingCount * processingTime;

        // 4. 직원 배정
        Integer memberId = taskMapper.selectAvailableMemberId(minLevel);
        
        // 5. 순번 (ranking)
        int ranking = waitingCount + 1;

        // 6. 엔티티 생성 및 저장
        TaskEntity task = TaskEntity.builder()
                .userEmail(userEmail)
                .ticketNumber(ticketNumber)
                .taskType(taskType)
                .taskDetailType(requestDto.getTaskDetailType())
                .assignedLevel(assignedLevel)
                .expectedWaitingTime(expectedWaitingTime)
                .status(TaskStatus.WAITING.name())
                .memberId(memberId)
                .ranking(ranking)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        int result = taskMapper.insert(task);
        return result > 0 ? TaskResult.SUCCESS : TaskResult.FAILURE;
    }
    public List<TaskVo> getTask(String userEmail ) {
        return taskMapper.selectTasksByEmail(userEmail);
    }
    
    public TaskVo getLatestTask(String userEmail) {
        return taskMapper.selectLatestTaskByUserEmail(userEmail);
    }

    public String getAverageTime() {
        return taskMapper.selectAverageTime();
    }

    
    public int getAvailableMemberCount() {
        return taskMapper.countAvailableMembers();
    }
    public int getTotalWaitingPerson() {
        return taskMapper.countAllWaitingPerson();

    }
    public List<TaskVo> getTasksByMemberId(Integer memberId) {
        return taskMapper.selectTasksByMemberId(memberId);
    }
    public TaskResult updateTaskStatus(Long taskId, String status) {
        int result = taskMapper.updateTaskStatus(taskId, status);
        return result > 0 ? TaskResult.SUCCESS : TaskResult.FAILURE;
    } 
}