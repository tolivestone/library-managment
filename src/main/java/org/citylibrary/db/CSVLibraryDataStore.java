package org.citylibrary.db;

import org.citylibrary.csvhelper.LibrarItemCsvReader;
import org.citylibrary.model.actor.Customer;
import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.LibraryItem;
import org.citylibrary.model.item.Loan;

import java.util.ArrayList;
import java.util.List;

public class CSVLibraryDataStore implements DataStore {

    private static DataStore instance;

    private final List<LibraryItem> libraryItems;
    private final List<Person> customers;
    private final List<Loan> loans;


    private CSVLibraryDataStore() {
        libraryItems = new ArrayList<>();
        customers = new ArrayList<>();
        loans = new ArrayList<>();

        //Pre-populating data store with items and users

        libraryItems
                .addAll(
                        LibrarItemCsvReader.getInstance()
                                .getLibraryItemsFromCsv());

        customers.addAll(List.of(
                new Customer(1,"Customer-1","Customer-1-LastName"),
                new Customer(2,"Customer-2","Customer-2-LastName"),
                new Customer(3,"Customer-3","Customer-3-LastName")
        ));
    }


    public List<LibraryItem> getLibraryItems() {
        return libraryItems;
    }

    public List<Person> getCustomers() {
        return customers;
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
