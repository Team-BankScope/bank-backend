package dev.gmpark.bankbackend.mappers;


import dev.gmpark.bankbackend.entities.TaskEntity;
import dev.gmpark.bankbackend.vos.TaskVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskMapper {
    int insert(@Param(value = "task")TaskEntity task);
    String selectLastTicketNumber(@Param("prefix") String prefix);
    int countWaitingTasks(@Param("taskType") String taskType);
    Integer selectAvailableMemberId(@Param("minLevel") int minLevel);
    List<TaskVo> selectTasksByEmail(@Param("userEmail") String userEmail);
    TaskVo selectLatestTaskByUserEmail(@Param("userEmail") String userEmail);
    List<TaskVo> selectTasksByMemberId(@Param("memberId") Integer memberId);
    String selectAverageTime();
    int countAvailableMembers();
    int countAllWaitingPerson();
    int updateTaskStatus(@Param("taskId") Long taskId, @Param("status") String status);
}
