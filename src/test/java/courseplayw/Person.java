package courseplayw;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Person {

    String name;
    int age;

    Person(String name, int age) {

        this.name = name;
        this.age = age;
    }

    public static void printMaxAge(List<Person> people) {
        people.stream()
                .collect(Collectors.groupingBy(person -> person.name,
                        Collectors.mapping(person -> person.age,
                                Collectors.maxBy(Integer :: compareTo)
                        )
                ))
                .forEach((name, age) -> {
                            System.out.println(name + ": " + age);
                        }
                        );
    }

    public static void main(String[] args) {
        List<Person> persons = Arrays.asList(
            new Person("Маша", 25),
            new Person("Максим", 0),
            new Person("Максим", 36),
            new Person("Максим", 46),
            new Person("Никита", 56)
        );
        printMaxAge(persons);
    }





}
