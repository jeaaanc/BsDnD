package BankSdNd.example.BsDnD.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String accountNumber,
        BigDecimal balance,
        UserUpdateDtos.UserResponse holder
){}
