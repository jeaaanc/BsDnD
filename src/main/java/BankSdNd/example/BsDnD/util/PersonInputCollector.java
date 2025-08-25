package BankSdNd.example.BsDnD.util;

import BankSdNd.example.BsDnD.dto.PersonDto;

import java.math.BigDecimal;
import java.util.Scanner;

public class PersonInputCollector {

    public static PersonDto collectUserInput(Scanner scanner){
        String name = InputUtils.readString(scanner,"Nome: ");
        String lastName = InputUtils.readString(scanner, "Sobrenome: ");
        String cpf = InputUtils.readString(scanner, "CPF: ");
        String phoneNumber = InputUtils.readString(scanner, "Celular: ");
        BigDecimal income = InputUtils.readBigDecimal(scanner, "Renda: ");

        String rawPassword = InputUtils.readString(scanner, "Senha: ");
        String confirmedRawPassword = InputUtils.readString(scanner, "Confirme a senha: ");


        return new PersonDto(name, lastName, cpf, phoneNumber, income, rawPassword, confirmedRawPassword);
    }
}
