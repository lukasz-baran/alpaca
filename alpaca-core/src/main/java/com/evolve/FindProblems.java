package com.evolve;

import java.util.List;

public interface FindProblems {

    List<String> findRegistryNumbersIssues();

    List<String> findMissingDates();

    List<String> findInvalidAddresses();
}
