package gov.uk.ets.commons.dschemas.testclasses;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person {

    private String name;
    private int age;
    private Long money;
    private List<String> children = new ArrayList<>();
    private Gender gender;
    private Set<Pet> pets = new HashSet<>();

    public static Person buildOne() {
        Person person = new Person();
        person.setName("Nik");
        person.setAge(32);
        person.setMoney(1000000L);
        person.setGender(Gender.MALE);
        person.setChildren(List.of("Helen", "George"));
        person.setPets(Set.of(
            new Pet("rex", true, 3),
            new Pet("rosie", false, 6))
        );
        return person;
    }

    public enum Gender {
        MALE, FEMALE
    }

    public static class Pet {
        String name;
        boolean woof;
        int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isWoof() {
            return woof;
        }

        public void setWoof(boolean woof) {
            this.woof = woof;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Pet(String name, boolean woof, int age) {
            this.name = name;
            this.woof = woof;
            this.age = age;
        }
    }
}
