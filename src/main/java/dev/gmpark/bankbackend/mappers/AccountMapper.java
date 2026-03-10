package dev.gmpark.bankbackend.mappers;

import dev.gmpark.bankbackend.entities.AccountEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountMapper {
    int insertAccount(@Param("account") AccountEntity account);
    AccountEntity selectAccountById(@Param("id") Integer id);
    int countByAccountNumber(@Param("accountNumber") String accountNumber);
}
