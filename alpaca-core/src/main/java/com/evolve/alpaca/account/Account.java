package com.evolve.alpaca.account;

import com.evolve.domain.Person;
import lombok.*;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Entity;
import java.util.Arrays;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account {

    @javax.persistence.Id
    private String accountId;
    private String accountName; // Usually it contains person's name with some additional message


    /**
     * Reference to {@link Person}, based on {@link Account#accountId} <br/>
     * Maybe {@code null} when account does not belong to a person
     */
    private String personId;
    private String unitNumber;
    private AccountType accountType;

    public static String getUnitNumber(String accountingNumber) {
        return StringUtils.substring(accountingNumber, 3, 5);
    }

    @RequiredArgsConstructor
    @Getter
    public enum AccountType {
        FEES("200", "składki"),
        LOANS("201", "pożyczki"),
        PAYDAY_LOANS("203", "chwilówki"),
        DEATH_BENEFITS("807", "odprawy pośmiertne");

        private final String code;
        private final String description;

        public static AccountType of(String givenCode) {
            return Arrays.stream(AccountType.values())
                    .filter(type -> type.code.equals(givenCode))
                    .findFirst()
                    .orElse(null);
        }


        public static AccountType ofAccountingNumber(String accountingNumber) {
            return Account.AccountType.of(StringUtils.substring(accountingNumber, 0, 3));
        }

        @Override
        public String toString() {
            return this.description;
        }
    }

}
