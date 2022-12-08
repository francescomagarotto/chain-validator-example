package com.francescomagarotto.chainvalidator;


import com.francescomagarotto.chainvalidator.validators.ValidatorChain;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Person person = new Person();
        person.setName("Sam");
        person.setSurname("Winston");
        person.setDogs(List.of(new Person.Dog("Artur"), new Person.Dog("Philip")));
        ValidatorChain<Person> chain = ValidatorChain.of(person)
                .link(Person::getName, (String s) -> true)
                .link(new NameTransformer(), (String s) -> s.length() == 7)
                .link((Person p) -> p.getDogs().get(0), (Person.Dog dog) -> dog.getName().equals("Artur"))
                .link(Person::getSurname, (String s) -> s.length() == 2,  "Surname length should be 7")
                .bond();
        System.out.println(chain.validate());
    }
}