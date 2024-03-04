package com.evolve.supporting;
import org.h2.tools.Script;

import java.sql.SQLException;

public class BackupDatabaseApp {

    public static void main(String[] args) throws SQLException {
        final String[] arguments = new String[]{
                "-url", "jdbc:h2:file:c:/alpaca/h2/alpaca",
                "-script", "alpaca.zip",
                "-options",  "compression", "zip"
        };

        new Script().runTool(arguments);

    }
}
