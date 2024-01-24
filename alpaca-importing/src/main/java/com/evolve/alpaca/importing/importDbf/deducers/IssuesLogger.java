package com.evolve.alpaca.importing.importDbf.deducers;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class IssuesLogger {
    private final Map<String, ImportIssues> issues = new HashMap<>();

    public ImportIssues forPersonId(String id) {
        final ImportIssues importIssues = new ImportIssues();
        issues.put(id, importIssues);
        return importIssues;
    }

    @Override
    public String toString() {
        StringBuilder issuesFound = new StringBuilder("Issues found: ")
                .append(Strings.LINE_SEPARATOR);

        issues.forEach((id, issues)  -> {
            if (!issues.isEmpty()) {
                issuesFound.append(id).append(" -> ").append(issues).append(Strings.LINE_SEPARATOR);
            }
        });

        return issuesFound.toString();
    }

    @ToString
    public static class ImportIssues {
        List<String> issues = new ArrayList<>();

        public boolean isEmpty() {
            return issues.isEmpty();
        }

        public void store(String issue) {
            issues.add(issue);
        }
    }

}
