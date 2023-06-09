package com.evolve.services;

import com.evolve.domain.Unit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.repository.Cursor;
import org.dizitart.no2.repository.ObjectRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitsService {
    private final Nitrite nitrite;

    public Map<String, Unit> fetchMap() {
        return fetchList().stream().collect(Collectors.toMap(Unit::getId, unit -> unit));
    }

    public List<Unit> fetchList() {
        final ObjectRepository<Unit> unitRepo = nitrite.getRepository(Unit.class);

        final List<Unit> units = new ArrayList<>();
        Cursor<Unit> unitCursor = unitRepo.find();
        for (Unit document : unitCursor) {
            units.add(document);
        }

        return units;
    }

}
