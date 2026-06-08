package BankSdNd.example.BsDnD.adapter.in.web.dto;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        BigDecimal balance,
        UserUpdateDtos.UserResponse holder
){}
