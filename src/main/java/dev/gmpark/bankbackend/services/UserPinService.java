package dev.gmpark.bankbackend.services;


import dev.gmpark.bankbackend.entities.UserEntity;
import dev.gmpark.bankbackend.entities.UserPinEntity;
import dev.gmpark.bankbackend.mappers.UserPinMapper;
import dev.gmpark.bankbackend.results.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPinService {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final SmsService smsService;
    private final UserPinMapper userPinMapper;

    @Transactional
    public CommonResult createUserPin(UserEntity user, String pin) {
        if (user == null || user.getPhone() == null) {
            return CommonResult.FAILURE;
        }


        if (pin == null || !pin.matches("\\d{6}")) { // PIN은 6자리 숫자인지 정규식으로 검증
            return CommonResult.FAILURE;
        }
        // 1. SMS 인증 완료 여부 확인
        boolean isAuthComplete = smsService.isAuthenticationComplete(user.getPhone());
        if (!isAuthComplete) {
            return CommonResult.FAILURE; // 또는 CommonResult.FAILURE_UNAUTHORIZED
        }

        // 2. PIN 번호 암호화 및 DB 저장
        String encodedPin = encoder.encode(pin);
        int result = userPinMapper.insertUserPin(user.getId(), encodedPin);

        if (result == 0) {
            return CommonResult.FAILURE; // DB 저장 실패
        }
        // 3. 보안을 위해 사용된 인증 정보 즉시 폐기
        smsService.cleanupAuthInfo(user.getPhone());

        return CommonResult.SUCCESS;
    }
    @Transactional
    public CommonResult deleteUserPin(UserEntity user) {
        if( user == null || user.getPhone() == null ) {
            return CommonResult.FAILURE;
        }
        int result = this.userPinMapper.deleteUserPin(user.getId());
        if ( result > 0) {
            return CommonResult.SUCCESS;

        } else {
           return   CommonResult.FAILURE;
        }

    }
    public CommonResult updateUserPin(UserEntity user, String pin) {
        if (user == null || user.getPhone() == null) {
            return CommonResult.FAILURE;
        }
        if (pin == null || !pin.matches("\\d{6}")) {
            return CommonResult.FAILURE;
        }
        String encodedPin = encoder.encode(pin);
        int result = this.userPinMapper.update(user.getId(), encodedPin);
        if ( result > 0 ) {
            return CommonResult.SUCCESS;
        } else {
            return CommonResult.FAILURE;
        }
    }
    @Transactional
    public CommonResult checkUserPin(UserEntity user, String pin) {
        if (user == null || user.getPhone() == null) {
            return CommonResult.FAILURE;
        }
        if (pin == null || !pin.matches("\\d{6}")) {
            return CommonResult.FAILURE;

        }
        UserPinEntity dbUserPin = this.userPinMapper.getUserPin(user.getId());
        if (BCrypt.checkpw(pin, dbUserPin.getPinHash())) {
            return CommonResult.SUCCESS;
        }
        return CommonResult.FAILURE;
    }

}
