package dev.gmpark.bankbackend.mappers;

import dev.gmpark.bankbackend.entities.AccountEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountMapper {
    int insertAccount(@Param("account") AccountEntity account);
    AccountEntity selectAccountById(@Param("id") Long id);
    AccountEntity selectAccountByAccountNumber(@Param("accountNumber") String accountNumber);
    List<AccountEntity> selectAccountsByUserId(@Param("userId") Integer userId);
    int countByAccountNumber(@Param("accountNumber") String accountNumber);
    int updateBalance(@Param("accountId") Long accountId, @Param("balance") Long balance);
}
