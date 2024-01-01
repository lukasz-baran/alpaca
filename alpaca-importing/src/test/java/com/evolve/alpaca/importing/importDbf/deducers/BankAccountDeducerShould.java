package com.evolve.alpaca.importing.importDbf.deducers;

import com.evolve.domain.BankAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BankAccountDeducerShould {

    @Mock
    IssuesLogger.ImportIssues importIssues;

    @InjectMocks
    BankAccountDeducer deducer;

    @Test
    void decodeBankNumber() {
        final Optional<BankAccount> result = deducer.deduceFrom(List.of("78",
                 "BPH III/O RZESZÓW",
                 "",
                 "10600076 0000 3000",
                 "0251 6937"));

        assertThat(result).hasValue(BankAccount.of("78106000760000300002516937"));
    }


}