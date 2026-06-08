package BankSdNd.example.BsDnD.core.port.in;

import BankSdNd.example.BsDnD.core.domain.model.BankUser;
import java.util.List;

/**
 * Input port for person retrieval use cases.
 */
public interface GetPersonUseCase {
    List<BankUser> findAll();
}
