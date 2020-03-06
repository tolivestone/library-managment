package org.citylibrary.service;

import org.citylibrary.exception.LibraryOperationException;
import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.LibraryItem;
import org.citylibrary.model.item.Loan;

import java.time.LocalDate;
import java.util.List;

public interface DataService {
    void clearDataStore();
    void reloadDataStore();
    boolean addLibraryItem(final LibraryItem item);
    boolean removeLibraryItem(final LibraryItem item) throws LibraryOperationException;
    boolean addLoan(Person customer, LibraryItem item, LocalDate issueDate, LocalDate dueDate);
    boolean returnLoanedItem(LibraryItem item);
    List<LibraryItem> getCurrentInventory();
    List<LibraryItem>getCurrentLoanableInventory();
    List<LibraryItem> searchItemsByTitle(final String title);
    List<LibraryItem> searchItemsByLibraryId(final int libraryId);
    List<Loan> getLoan();
}
