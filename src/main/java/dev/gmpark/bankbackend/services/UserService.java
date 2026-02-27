package dev.gmpark.bankbackend.services;


import dev.gmpark.bankbackend.entities.MemberEntity;
import dev.gmpark.bankbackend.entities.UserEntity;
import dev.gmpark.bankbackend.mappers.UserMapper;
import dev.gmpark.bankbackend.results.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    public CommonResult register(UserEntity user ) {
        if( user.getName() == null || user.getEmail() == null || user.getPassword() == null || user.getResidentNumber() == null) {
            return CommonResult.FAILURE;
        }

        int result = this.userMapper.insertUser(user);
        if(result > 0) {
            return CommonResult.SUCCESS;
        } else  {
            return CommonResult.FAILURE;
        }
    }

    public CommonResult registerMember(MemberEntity member) {
        if (member.getName() == null || member.getEmail() == null || member.getPassword() == null||
            member.getLevel() == null || member.getAuth() == null || member.getTeam() == null ) {
            return CommonResult.FAILURE;
        }
        int result = this.userMapper.insertMember(member);
        if (result > 0) {
            return CommonResult.SUCCESS;
        } else {
            return CommonResult.FAILURE;
        }
    }

    public CommonResult modifyMember(MemberEntity member) {
        if (member.getEmail() == null) {
            return CommonResult.FAILURE;
        }
        int result = this.userMapper.updateMember(member);
        if (result > 0) {
            return CommonResult.SUCCESS;
        } else {
            return CommonResult.FAILURE;
        }
    }

    public List<MemberEntity> getMembers() {
        return this.userMapper.selectMembers();
    }

    public UserEntity login(String email, String password, String residentNumber) {
        return this.userMapper.selectUserByEmailPasswordAndResidentNumber(email, password, residentNumber);
    }
    public UserEntity loginKiosk(String residentNumber ) {
        return this.userMapper.selectUserByResidentNumber(residentNumber);
    }

    public UserEntity loginAdmin(String email, String password) {
        return this.userMapper.selectUserByEmailAndPassword(email, password);
    }

    public MemberEntity loginMember(String email, String password) {
        return this.userMapper.selectMemberByEmailAndPassword(email, password);
    }
}
