package BankSdNd.example.BsDnD.adapter.in.web;

import BankSdNd.example.BsDnD.adapter.in.web.dto.TransferRequest;
import BankSdNd.example.BsDnD.core.port.in.TransferMoneyUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
public class TransferRestController {

    private final TransferMoneyUseCase transferMoneyUseCase;

    public TransferRestController(TransferMoneyUseCase transferMoneyUseCase) {
        this.transferMoneyUseCase = transferMoneyUseCase;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> transfer(
            @RequestBody @Valid TransferRequest request
            ) {
        transferMoneyUseCase.transfer(
                request.sourceAccountNumber(),
                request.destinationAccountNumber(),
                request.amount(),
                request.password()
        );

        return ResponseEntity.ok(Map.of("message", "Transfer completed successfully!"));
    }
}
