package dev.gmpark.bankbackend.mappers;

import dev.gmpark.bankbackend.entities.UserPinEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserPinMapper {
    // PIN 번호 등록 및 재설정 (ON DUPLICATE KEY UPDATE 활용)
    int insertUserPin(@Param("userId") Integer userId, @Param("pinHash") String pinHash);
    UserPinEntity getUserPin(@Param("userId") Integer userId);
    int deleteUserPin(@Param("userId") Integer userId);

    int update(@Param("userId") Integer userId, String encodedPin);
}
