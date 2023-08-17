package com.evolve.gui.admin.importDbf;

import lombok.Data;

import java.io.File;

@Data
public class DbfFiles {
    private File mainFile; // Z_B_KO.DBF
    private File planAccountsFile; // PLAN.DBF

    public boolean isReady() {
        return mainFile != null || planAccountsFile != null;
    }

}
