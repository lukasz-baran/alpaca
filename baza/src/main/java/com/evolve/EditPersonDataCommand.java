package com.evolve;

import java.time.LocalDate;

public record EditPersonDataCommand(String id,
                                    String firstName,
                                    String lastName,
                                    String secondName,
                                    String email,
                                    LocalDate dob) {

}
