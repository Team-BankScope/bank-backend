package dev.gmpark.bankbackend.mappers;


import dev.gmpark.bankbackend.entities.MemberEntity;
import dev.gmpark.bankbackend.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    int insertUser (@Param(value = "user") UserEntity user);
    int insertMember(@Param(value = "member") MemberEntity member);
    int updateMember(@Param(value = "member") MemberEntity member);
    List<MemberEntity> selectMembers();
    UserEntity selectUserByEmailPasswordAndResidentNumber(@Param(value = "email") String email, @Param(value = "password") String password, @Param(value = "residentNumber") String residentNumber);
    UserEntity selectUserByEmailAndPassword(@Param(value = "email") String email, @Param(value = "password") String password);
}
