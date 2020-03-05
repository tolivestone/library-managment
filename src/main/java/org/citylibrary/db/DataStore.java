package org.citylibrary.db;

import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.LibraryItem;
import org.citylibrary.model.item.Loan;
import org.citylibrary.model.item.Loanable;

import java.util.List;

public interface DataStore {
    List<LibraryItem> getLibraryItems();
    List<Person> getBorrowers();
    List<Loan> getLoans();
}