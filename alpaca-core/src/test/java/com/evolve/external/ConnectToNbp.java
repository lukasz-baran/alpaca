package com.evolve.external;

import com.evolve.alpaca.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

public class ConnectToNbp {

    private final String url = "https://ewib.nbp.pl/api/v1/zapytanie1/?nrRozliczeniowy=%s&format=json";

    ValidateNbpNumbersService validateWithNbp = new ValidateNbpNumbersService(url);

    @Test
    public void handleValidId() {
        assumeThat(TestUtils.isOnLocalEnv()).isTrue();
        Optional<BankDetails> nbpRecord = validateWithNbp.getBankDetails("10600076");
        assertThat(nbpRecord)
                .isNotEmpty();

        //System.out.println(nbpRecord.get());
    }


    @Test
    public void handleInvalidId() {
        assumeThat(TestUtils.isOnLocalEnv()).isTrue();
        Optional<BankDetails> nbpRecord = validateWithNbp.getBankDetails("20440500");
        assertThat(nbpRecord)
                .isEmpty();

    }
}
