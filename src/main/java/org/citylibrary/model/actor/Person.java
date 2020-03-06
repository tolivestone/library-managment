package org.citylibrary.model.actor;

// Base class for library actors customer, librarian, clerk, admin etc

abstract public class Person {
    private final int id;               // unique identity
    private final String firstName;
    private final String lastName;

    protected Person(final int id, final String firstName,final String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    protected int getId() {
        return id;
    }

    protected String getFirstName() {
        return firstName;
    }

    protected String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
