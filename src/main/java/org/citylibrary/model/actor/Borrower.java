package org.citylibrary.model.actor;

public class Borrower extends Person {

    public Borrower(final int id, final String firstName, final String lastName) {
        super(id, firstName, lastName);
    }

    @Override
    public String toString() {
        return "[" +
                "id=" + this.getId() +
                ", firstName='" + this.getFirstName() + '\'' +
                ", lastName='" + this.getLastName() + '\'' +
                ']';
    }
}
