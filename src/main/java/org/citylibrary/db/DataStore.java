package org.citylibrary.db;

import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.LibraryItem;
import org.citylibrary.model.item.Loan;

import java.util.List;

public interface DataStore {
    List<LibraryItem> getLibraryItems();
    List<Person> getCustomers();
    List<Loan> getLoans();
}
