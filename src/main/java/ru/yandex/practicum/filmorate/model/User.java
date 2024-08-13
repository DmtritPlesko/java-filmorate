package ru.yandex.practicum.filmorate.model;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Data
@Builder
@NonNull
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @EqualsAndHashCode.Include
    Long id;

    String email;

    String login;

    String name;

    int years;

    String password;

    LocalDate birthday;

//    Set<Long> friends;

//    Status friend;

}
