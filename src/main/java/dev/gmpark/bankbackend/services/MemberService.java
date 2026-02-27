package dev.gmpark.bankbackend.services;

import dev.gmpark.bankbackend.mappers.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final TaskMapper taskMapper;

}
