package dev.gmpark.bankbackend.mappers;

import dev.gmpark.bankbackend.entities.TransactionHistoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TransactionHistoryMapper {
    int insertTransaction(@Param("transaction") TransactionHistoryEntity transaction);
}
