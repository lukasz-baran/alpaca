package com.evolve.services;

import com.evolve.domain.Unit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.repository.Cursor;
import org.dizitart.no2.repository.ObjectRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnitsPopulateService implements InitializingBean {

    private final Nitrite nitrite;

    private static final List<Unit> UNITS = List.of(
            Unit.of("01 – Szpital wojewódzki nr 1 im. Fryderyka Chopina"),
            Unit.of("02 – Szpital wojewódzki nr 2"),
            Unit.of("03 – SP ZOZ nr 1"),
            Unit.of("04 – ZOZ nr 2"),
            Unit.of("05 – Szpital MSW"),
            Unit.of("06 – Jednostka Wojskowa 4909"),
            Unit.of("07 – Obwód lecznictwa kolejowego Rz-W"),
            Unit.of("08 – Spec. Zesp. Gruź. I chorób płuc"),
            Unit.of("09 – Małe jednostki"),
            Unit.of("10 – Rzeszów – Sami"),
            Unit.of("11 – ZOZ Brzozów"),
            Unit.of("12 – ZOZ Dębica"),
            Unit.of("13 – ZOZ Gorlice"),
            Unit.of("14 – ZOZ Jarosław"),
            Unit.of("15 – Szpital Psychiatryczny Jarosław"),
            Unit.of("16 – ZOZ Jasło"),
            Unit.of("17 – ZOZ Kolbuszowa"),
            Unit.of("18 – Szpital Wojewódzki Krosno"),
            Unit.of("19 – ZOZ Krosno"),
            Unit.of("20 – ZOZ Lesko"),
            Unit.of("21 – ZOZ Leżajsk"),
            Unit.of("22 – ZOZ Łańcut"),
            Unit.of("23 – ZOZ Mielec"),
            Unit.of("24 – ZOZ Nisko"),
            Unit.of("25 – ZOZ Nowa Dęba"),
            Unit.of("26 – Wojewódzki Szp. Zesp. W Przemyślu"),
            Unit.of("27 – ZOZ Przemyśl"),
            Unit.of("28 – ZOZ Przeworsk"),
            Unit.of("29 – ZOZ Ropczyce"),
            Unit.of("30 – ZOZ Sandomierz"),
            Unit.of("31 – ZOZ Sanok"),
            Unit.of("32 – ZOZ Stalowa Wola"),
            Unit.of("33 – Przem. Spec. ZOZ Stalowa Wola"),
            Unit.of("34 – ZOZ Strzyżów"),
            Unit.of("35 – Szpital Tarnobrzeg"),
            Unit.of("36 – ZOZ Ustrzyki Dolne"),
            Unit.of("37 – Bar.San, Bystre, Brzesko"),
            Unit.of("38 – ?"),
            Unit.of("39 – Górno, Gorzów WLK"),
            Unit.of("40 – Iwonicz Zdrój – uzdrowisko Iw. San. Gór, UG"),
            Unit.of("41 – ?"),
            Unit.of("42 – Kraków- C.M. Pr, ZOZ-3, P. R. Szp. Ok"),
            Unit.of("43 – Krosno Sp.In. Krępna, Krzeszów"),
            Unit.of("44 – Ter. St. San, Leżajsk, Sp. In, Łańcut"),
            Unit.of("45 – ?"),
            Unit.of("46 – ZOZ Opatów"),
            Unit.of("47 – ZOZ Poddęb. Przem, Olk, P.R. ST San"),
            Unit.of("48 – ?"),
            Unit.of("49 – Szpotal Woj Tarnów"),
            Unit.of("50 – Warszawa- Żoliborz"),
            Unit.of("51 – Brzozów-sami"),
            Unit.of("52 – Dębica sami"),
            Unit.of("53 – Gdynia, Gorlice, Gorzkowice- sami"),
            Unit.of("54 – Jarosław, Jasło-sami"),
            Unit.of("55 – Kraków, Kolbuszowa, Krosno, Sami"),
            Unit.of("56 – Leżajsk, Leżaj, Lub, Łańcut – sami"),
            Unit.of("57 – Mielec – sami"),
            Unit.of("58 – Nowa Dęba, Nisko – sami"),
            Unit.of("59 – Przemyśl, Pruchnik – sami"),
            Unit.of("60 – Ropczyce, Rudnik, sami"),
            Unit.of("61 – Sanok, Sandomierz, Stalowa Wola – sami"),
            Unit.of("62 – Tarnobrzeg -sami"),
            Unit.of("63 – Ustrzyki dolne - sami"),
            Unit.of("64 – Wrocław, Warszawa, sami"),
            Unit.of("65 – ?"),
            Unit.of("66 – Różne miejscowości sami"),
            Unit.of("67 – ?"),
            Unit.of("68 – ?"),
            Unit.of("69 – ?"),
            Unit.of("70 – ZOZ Lubaczów"),
            Unit.of("71 – Uzdrowisko Rymanów Zdrój"),
            Unit.of("95 – zwolnieni"),
            Unit.of("96 – inni"),
            Unit.of("99 – zmarli"));


    @Override
    public void afterPropertiesSet() {
        log.info("db ready {}", nitrite.getConfig());

        final ObjectRepository<Unit> unitRepo = nitrite.getRepository(Unit.class);
//        unitRepo.remove(Filter.ALL);
//        UNITS.forEach(unitRepo::insert);

        Cursor<Unit> unitCursor = unitRepo.find();
        for (Unit document : unitCursor) {
            log.info("document: {}", document);
        }
    }
}
