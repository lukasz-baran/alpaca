package com.evolve.importing.importDbf.deducers;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractSmartDeducer<T> implements SmartDeducer<T> {

    protected final IssuesLogger.ImportIssues issues;

}
