package com.francescomagarotto.chainvalidator;

import java.util.LinkedList;
import java.util.List;

public class Person {
    public String name;
    public String surname;
    public Integer age;
    public Boolean developer;

    public List<ProgrammingSkill> programmingSkills;

    public Person() {
        this.programmingSkills = new LinkedList<>();
    }

    public static class ProgrammingSkill {
        public String skillName;
        public int level;

        public ProgrammingSkill(String skillName, int level) {
            this.skillName = skillName;
            this.level = level;
        }
    }
}