package com.evolve.alpaca.importing.importDbf.account;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

class ImportAccountDbfShould {

    @Test
    void importAccounts() throws IOException {
        // given
        final Resource accountsFile = new DefaultResourceLoader().getResource("PLAN.DBF");
        assumeThat(accountsFile.exists())
                .isTrue();

        // when
        final List<DbfAccount> kontaDbf = new ImportAccountDbf().performImport(accountsFile.getURL());

        // then
        assertThat(kontaDbf)
                .isNotEmpty();
//        for (DbfAccount dbfAccount : kontaDbf) {
//            System.out.println(dbfAccount);
//        }
    }

}