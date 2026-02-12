package dev.gmpark.bankbackend.services;


import dev.gmpark.bankbackend.entities.UserEntity;
import dev.gmpark.bankbackend.mappers.UserMapper;
import dev.gmpark.bankbackend.results.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import dev.gmpark.bankbackend.entities.UserEntity;
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
    public UserEntity login(String email, String password, String residentNumber) {
        return this.userMapper.selectUserByEmailPasswordAndResidentNumber(email, password, residentNumber);
    }

    public UserEntity loginAdmin(String email, String password) {
        return this.userMapper.selectUserByEmailAndPassword(email, password);
    }
}
