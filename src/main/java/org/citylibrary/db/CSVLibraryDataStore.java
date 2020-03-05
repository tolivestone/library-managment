package org.citylibrary.db;

import org.citylibrary.Library;
import org.citylibrary.csvhelper.LibrarItemCsvReader;
import org.citylibrary.model.actor.Borrower;
import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.LibraryItem;
import org.citylibrary.model.item.Loan;

import java.util.ArrayList;
import java.util.List;

public class CSVLibraryDataStore implements DataStore {

    private static DataStore instance;

    private final List<LibraryItem> libraryItems;
    private final List<Person> borrowers;
    private final List<Loan> loans;


    private CSVLibraryDataStore() {
        libraryItems = new ArrayList<>();
        borrowers = new ArrayList<>();
        loans = new ArrayList<>();

        //Pre-populating data store with items and users

        libraryItems
                .addAll(
                        LibrarItemCsvReader.getInstance()
                                .getLibraryItemsFromCsv());

        borrowers.addAll(List.of(
                new Borrower(1,"Borrower-1","Borrower-1-LastName"),
                new Borrower(2,"Borrower-2","Borrower-2-LastName"),
                new Borrower(3,"Borrower-3","Borrower-3-LastName")
        ));
    }


    public List<LibraryItem> getLibraryItems() {
        return libraryItems;
    }

    public List<Person> getBorrowers() {
        return borrowers;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    // This will ensure only one copy of data source is available to application
    // Singleton static factory method with double checking for thread saftey
    public static DataStore getInstance() {
        if(instance == null) {
            synchronized (CSVLibraryDataStore.class) {
                if(instance == null) {
                    instance = new CSVLibraryDataStore();
                    return instance;
                }
            }
        }
        return instance;
    }
}
