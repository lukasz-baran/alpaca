package com.evolve.gui.admin.importDbf;

import lombok.Data;

import java.io.File;

@Data
public class DbfFiles {
    private File mainFile; // Z_B_KO.DBF
    private File planAccountsFile; // PLAN.DBF

    /**
     * @return {@code true} if both file paths are set up
     */
    public boolean isReady() {
        return mainFile != null || planAccountsFile != null;
    }

}
