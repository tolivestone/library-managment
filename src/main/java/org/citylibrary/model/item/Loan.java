package org.citylibrary.model.item;

import org.citylibrary.model.actor.Borrower;
import org.citylibrary.model.actor.Person;

import java.time.LocalDate;
import java.util.Date;

public class Loan {
    private Person borrower;
    private LibraryItem item;
    private LocalDate issueDate;
    private LocalDate dueDate;


    public Loan(final Person borrower, final  LibraryItem item, final LocalDate issueDate, final LocalDate dueDate) {
        this.borrower = borrower;
        this.item = item;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public Person getBorrower() {
        return borrower;
    }

    public void setBorrower(Borrower borrower) {
        this.borrower = borrower;
    }

    public LibraryItem getItem() {
        return item;
    }

    public void setItem(LibraryItem item) {
        this.item = item;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Loan loan = (Loan) o;

        if (borrower != null ? !borrower.equals(loan.borrower) : loan.borrower != null) return false;
        if (item != null ? !item.equals(loan.item) : loan.item != null) return false;
        if (issueDate != null ? !issueDate.equals(loan.issueDate) : loan.issueDate != null) return false;
        return dueDate != null ? dueDate.equals(loan.dueDate) : loan.dueDate == null;
    }

    @Override
    public int hashCode() {
        int result = borrower != null ? borrower.hashCode() : 0;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + (issueDate != null ? issueDate.hashCode() : 0);
        result = 31 * result + (dueDate != null ? dueDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Loan[" +
                "\nborrower=" + borrower +
                ",\n item=" + item +
                ",\n issueDate=" + issueDate +
                "\n  dueDate=" + dueDate +
                "\n]";
    }
}
