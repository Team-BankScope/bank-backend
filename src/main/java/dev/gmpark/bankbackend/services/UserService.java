package dev.gmpark.bankbackend.services;


import dev.gmpark.bankbackend.entities.MemberEntity;
import dev.gmpark.bankbackend.entities.UserEntity;
import dev.gmpark.bankbackend.enums.Gender;
import dev.gmpark.bankbackend.mappers.UserMapper;
import dev.gmpark.bankbackend.results.CommonResult;
import dev.gmpark.bankbackend.results.KioskResult;
import dev.gmpark.bankbackend.utils.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public CommonResult register(UserEntity user ) {
        if( user.getName() == null || user.getEmail() == null || user.getPassword() == null || user.getResidentNumber() == null) {
            return CommonResult.FAILURE;
        }

        // 이메일 중복 체크 추가
        if (this.userMapper.selectUserByEmail(user.getEmail()) != null) {
            return CommonResult.FAILURE; 
        }
        
        // 평문 주민번호에서 성별, 나이 추출
        setGenderAndAgeFromResidentNumber(user);
        
        // 비밀번호는 단방향 해시(BCrypt)
        user.setPassword(encoder.encode(user.getPassword()));
        // 주민등록번호는 양방향 암호화(AES)
        user.setResidentNumber(AESUtil.encrypt(user.getResidentNumber()));
        
        int result = this.userMapper.insertUser(user);
        if(result > 0) {
            return CommonResult.SUCCESS;
        } else  {
            return CommonResult.FAILURE;
        }
    }
    
    public KioskResult seminRegister(UserEntity user) {
        if( user.getResidentNumber() == null || user.getName() == null) {
            return KioskResult.FAILURE;
        }
        // 1. 이미 존재하는 주민번호인지 확인 (중복 가입 방지)
        String encryptedResidentNumber = AESUtil.encrypt(user.getResidentNumber());
        UserEntity existingUser = this.userMapper.selectUserByResidentNumber(encryptedResidentNumber);

        if (existingUser != null) {
            // 키오스크(비회원)의 경우, 이미 가입된 내역이 있다면
            // 에러를 띄우지 말고 바로 성공 처리하여 다음 단계로 넘어가게 합니다.
            return KioskResult.FAILURE_EXISTING_RESIDENT_NUMBER;
        }

        // 2. 평문 주민번호에서 성별, 나이 추출
        setGenderAndAgeFromResidentNumber(user);
        
        // 3. 주민등록번호 양방향 암호화
        user.setResidentNumber(encryptedResidentNumber);
        
        int result = this.userMapper.insertUnregisteredUser(user);
        if(result > 0) {
            return KioskResult.SUCCESS;
        }
        else {
            return KioskResult.FAILURE;
        }
    }

    private void setGenderAndAgeFromResidentNumber(UserEntity user) {
        String residentNumber = user.getResidentNumber();
        if (residentNumber == null || residentNumber.length() < 7) {
            return;
        }
        // 1. 하이픈 제거
        residentNumber = residentNumber.replace("-", "");
        
        if (residentNumber.length() != 13) {
            return; 
        }
        // 2. 성별 추출 (7번째 자리)
        char genderCode = residentNumber.charAt(6);
        if (genderCode == '1' || genderCode == '3' || genderCode == '5') {
            user.setGender(Gender.MALE);
        } else if (genderCode == '2' || genderCode == '4' || genderCode == '6') {
            user.setGender(Gender.FEMALE);
        }

        // 3. 나이 추출
        String birthYearPrefix;
        if (genderCode == '1' || genderCode == '2' || genderCode == '5' || genderCode == '6') {
            birthYearPrefix = "19";
        } else {
            birthYearPrefix = "20";
        }
        
        String birthYearStr = birthYearPrefix + residentNumber.substring(0, 2);
        int birthYear = Integer.parseInt(birthYearStr);
        int currentYear = LocalDate.now().getYear();
        
        // 현재 연도 - 출생 연도 (만 나이가 아닌 연 나이 기준)
        int age = currentYear - birthYear;
        user.setAge(String.valueOf(age));
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
    public CommonResult deleteUser( Long id) {
        int result = this.userMapper.deleteMember(id);
        if( result > 0) {
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
        // 1. 이메일로 유저를 먼저 찾는다.
        UserEntity user = this.userMapper.selectUserByEmail(email);
        
        // 2. 유저가 존재하지 않으면 null 반환
        if (user == null) {
            return null;
        }
        
        // 3. 비밀번호는 BCrypt 매칭, 주민번호는 AES 암호화 후 문자열 비교
        String encryptedResidentNumber = AESUtil.encrypt(residentNumber);
        if (encoder.matches(password, user.getPassword()) && encryptedResidentNumber.equals(user.getResidentNumber())) {
            return user; 
        }
        
        return null;
    }

    public UserEntity loginKiosk(String residentNumber ) {
        // 평문 주민번호를 암호화
        String encryptedResidentNumber = AESUtil.encrypt(residentNumber);
        // 암호화된 문자열로 DB에서 조회
        return this.userMapper.selectUserByResidentNumber(encryptedResidentNumber);
    }

    public UserEntity loginAdmin(String email, String password) {
       UserEntity admin = this.userMapper.selectUserByEmail(email);
        if (admin == null || !"admin".equals(admin.getUserType())) {
            return null;
        }
        if(BCrypt.checkpw(password,admin.getPassword())) {
            return  admin;
        }
        return null;
    }

    public MemberEntity loginMember(String email, String password) {
        return this.userMapper.selectMemberByEmailAndPassword(email, password);
    }
}
