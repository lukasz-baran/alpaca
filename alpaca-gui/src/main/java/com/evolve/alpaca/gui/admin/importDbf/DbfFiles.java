package com.evolve.alpaca.gui.admin.importDbf;

import lombok.Data;

import java.io.File;

@Data
public class DbfFiles {
    private File mainFile; // Z_B_KO.DBF
    private File planAccountsFile; // PLAN.DBF

    private File docFile; // PLAN KONT.doc

    /**
     * @return {@code true} if both file paths are set up
     */
    public boolean isReady() {
        return mainFile != null || planAccountsFile != null && docFile != null;
    }

}
