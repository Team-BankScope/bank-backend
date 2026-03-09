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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskMapper taskMapper;


    // TODO: ai 설정 및 스케줄링 알고리즘 추가예정
    @Transactional
    public TaskResult createTask(TaskRequestDto requestDto, Integer userId) {
        // TODO : DTO랑 userId null검사
        if (requestDto == null || userId == null) {
            return TaskResult.FAILURE;
        }

        // TODO: 유저의 id를 통해서 그 유저의 특정 업무를 조회하고 단 하나라도
        //  IN_PROGRESS상태인 업무가 있을때 FAILURE_TASK_IN_PROGRESS를 return;
        List<TaskVo> userTasks = taskMapper.selectTasksByUserId(userId);
        if (userTasks != null && userTasks.stream()
                .anyMatch(task -> "IN_PROGRESS".equals(task.getStatus()))) {
            return TaskResult.FAILURE_TASK_IN_PROGRESS;
        }

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
        if ( waitingCount == 0) {
            waitingCount = 1;
        }
        int availableMemberCount = taskMapper.countAvailableMembersByLevel(minLevel);
        if (availableMemberCount == 0) availableMemberCount = 1; // 0으로 나누기 방지

        int expectedWaitingTime = (waitingCount * processingTime) / availableMemberCount;

        // 4. 직원 배정 및 기존 업무 통합 로직
        Integer memberId;
        List<TaskEntity> waitingTasks = taskMapper.selectWaitingTasksByUserId(userId);

        if (!waitingTasks.isEmpty()) {
            // 기존 대기 업무가 있는 경우
            int maxMinLevel = minLevel;
            List<Long> taskIdsToUpdate = new ArrayList<>();

            for (TaskEntity task : waitingTasks) {
                taskIdsToUpdate.add(task.getTaskId());
                int taskMinLevel = getMinLevelByAssignedLevel(task.getAssignedLevel());
                if (taskMinLevel > maxMinLevel) {
                    maxMinLevel = taskMinLevel;
                }
            }

            // 가장 높은 레벨을 처리할 수 있는 직원 배정
            memberId = taskMapper.selectAvailableMemberId(maxMinLevel);

            // 기존 업무들의 담당 직원 업데이트
            if (memberId != null && !taskIdsToUpdate.isEmpty()) {
                taskMapper.updateMemberIdForTasks(taskIdsToUpdate, memberId);
            }
        } else {
            // 기존 대기 업무가 없는 경우
            memberId = taskMapper.selectAvailableMemberId(minLevel);
        }
        
        // 5. 순번 (ranking)
        int ranking = waitingCount + 1;

        // 6. 엔티티 생성 및 저장
        TaskEntity task = TaskEntity.builder()
                .userId(userId)
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

    private int getMinLevelByAssignedLevel(String assignedLevel) {
        if ("LEVEL_1".equals(assignedLevel)) return 1;
        if ("LEVEL_2".equals(assignedLevel)) return 3;
        if ("LEVEL_3".equals(assignedLevel)) return 5;
        return 1; // 기본값
    }

    public List<TaskVo> getTask(Integer userId) {
        return taskMapper.selectTasksByUserId(userId);
    }
    
    public TaskVo getLatestTask(Integer userId) {
        return taskMapper.selectLatestTaskByUserId(userId);
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
        // TODO: memberId가 null값이 경우 예외처리 try-catch
        try {
            if (memberId == null) {
                throw new IllegalArgumentException("memberId cannot be null");
            }
            return taskMapper.selectTasksByMemberId(memberId);
        } catch (IllegalArgumentException e) {
            // 로그 기록
            System.err.println("Invalid memberId: " + e.getMessage());
            return new ArrayList<>(); // 빈 리스트 반환
        }
    }
    public TaskResult updateTaskStatus(Long taskId, String status) {
        // TODO : taskId, status가 null값이 경우 실패반환
        if (taskId == null || status == null) {
            return TaskResult.FAILURE;
        }
        int result = taskMapper.updateTaskStatus(taskId, status);
        return result > 0 ? TaskResult.SUCCESS : TaskResult.FAILURE;
    } 
}