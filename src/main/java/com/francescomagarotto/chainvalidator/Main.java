package com.francescomagarotto.chainvalidator;


public class Main {
    public static void main(String[] args) {
        Pippo pippo = new Pippo();
        pippo.setApricot("apircot");
        pippo.setName("nameeee");
        pippo.setSurname("aaaaaa");
        ValidatorChain<Pippo> chain = ValidatorChain.of(pippo)
                .chain((String s) -> true, Pippo::getName)
                .chain((String s) -> s.length() == 7, new NameTransformer())
                .chain((String s) -> s.length() == 2, Pippo::getSurname, "Surname length should be 7")
                .buildChain();
        System.out.println(chain.validate());
    }
}