package org.citylibrary.service;

import org.citylibrary.csvhelper.LibrarItemCsvReader;
import org.citylibrary.db.DataStore;
import org.citylibrary.enums.Status;
import org.citylibrary.exception.LibraryOperationException;
import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.LibraryItem;
import org.citylibrary.model.item.Loan;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

//TODO: create interface DataService
//TODO: rename CSVDataService
public class CSVDataService implements DataService{

    private final DataStore dataStore;

    //TODO: Review
    private final ReentrantReadWriteLock.ReadLock readLock = new ReentrantReadWriteLock().readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = new ReentrantReadWriteLock().writeLock();

    public CSVDataService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void clearDataStore() {
        //TODO: Review try catch finally
        try {
            writeLock.lock();
            dataStore.getLibraryItems().clear();
            dataStore.getLoans().clear();
        }finally {
            writeLock.unlock();
        }
    }

    @Override
    public void reloadDataStore() {

        try {
            writeLock.lock();
            this.clearDataStore();
            dataStore.getLibraryItems()
                    .addAll(LibrarItemCsvReader.getInstance().getLibraryItemsFromCsv());
        }finally {
            writeLock.unlock();
        }
    }

    @Override
    public List<LibraryItem> getCurrentInventory() {

        List<LibraryItem> currentInventoryList = new ArrayList<>();;
        try {
            readLock.lock();
            currentInventoryList =
                    dataStore.getLibraryItems().parallelStream().collect(Collectors.toList());
        }finally {
            readLock.unlock();
        }
        return  currentInventoryList;
    }

    @Override
    public List<LibraryItem> getCurrentLoanableInventory() {

        //TODO: review filter method
        List<LibraryItem> currentLoanableInventoryList = new ArrayList<>();
        try {
            readLock.lock();
            Predicates loanable = new Predicates(Status.AVAILABLE);
            currentLoanableInventoryList = getLibraryItems(loanable);
        }finally {
            readLock.unlock();
        }

        return  currentLoanableInventoryList;
    }



    @Override
    public List<LibraryItem> searchItemsByTitle(final String title) {
        Objects.requireNonNull(title, "Title cannot be null");

        List<LibraryItem> itemList = new ArrayList<>();
        try {
            readLock.lock();
            itemList = dataStore.getLibraryItems().parallelStream()
                    .filter(libraryItem -> libraryItem.getTitle().contains(title)).collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }

        return itemList;
    }

    @Override
    public List<LibraryItem> searchItemsByLibraryId(final int libraryId) {

        List<LibraryItem> itemList = new ArrayList<>();
        try{
        readLock.lock();
        itemList = dataStore.getLibraryItems().parallelStream()
                .filter(libraryItem -> libraryItem.getLibraryId() == libraryId).collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }

        return itemList;
    }

    @Override
    public boolean addLibraryItem(final LibraryItem item) {
        Objects.requireNonNull(item, "Item cannot be null");

        boolean added = false;
        try {
                writeLock.lock();
                if (!dataStore.getLibraryItems().contains(item)) {
                    added = dataStore.getLibraryItems().add(item);
                }
            }finally {
            writeLock.unlock();
        }

        return added;
    }

    @Override
    public boolean removeLibraryItem(final LibraryItem item) throws LibraryOperationException {
        Objects.requireNonNull(item, "Item cannot be null");

        boolean removed = false;

        if(!dataStore.getLibraryItems().contains(item)) {
            throw new LibraryOperationException("Item does not exist");
        }

        try {
            writeLock.lock();
            removed = dataStore.getLibraryItems().remove(item);
        } finally {
            writeLock.unlock();
        }

        return removed;
    }

    public boolean isBorrowed(final LibraryItem item){
        Objects.requireNonNull(item, "Item cannot be null");

        boolean isBorrowed;
        try {
            readLock.lock();
            isBorrowed = dataStore.getLoans().parallelStream()
                    .anyMatch(loan -> loan.getItem().equals(item));
        }finally {
            readLock.unlock();
        }
        return isBorrowed;
    }

    @Override
    public boolean addLoan(Person borrower, LibraryItem item, LocalDate issueDate, LocalDate dueDate) {
        Objects.requireNonNull(borrower, "Borrower cannot be null");
        Objects.requireNonNull(item, "Item cannot be null");
        Objects.requireNonNull(issueDate, "issueDate cannot be null");
        Objects.requireNonNull(dueDate, "dueDate cannot be null");

        boolean added;
        try {
            writeLock.lock();
            item.setItemStatus(Status.LOANED);
            Loan newLoan = new Loan(borrower, item, issueDate, dueDate);
            added = dataStore.getLoans().add(newLoan);
        } finally {
            writeLock.unlock();
        }

        return added;
    }

    @Override
    public List<Loan> getLoan() {

        List<Loan> loanList = new ArrayList<>();
        try {
            readLock.lock();
            loanList = dataStore.getLoans().parallelStream().collect(Collectors.toList());
        } finally {
            readLock.unlock();
        }

        return  loanList;
    }

    public boolean returnLoanedItem(LibraryItem item) {

        boolean success= false;
        try {
            writeLock.lock();
            Loan loanedItem = dataStore.getLoans().stream()
                    .filter(litem -> litem.getItem().equals(item)).findAny().orElse(null);

            if (loanedItem != null) {
                item.setItemStatus(Status.AVAILABLE);
                success = dataStore.getLoans().remove(loanedItem);
            }
        }finally {
            writeLock.unlock();
        }
        return success;
    }

    private List<LibraryItem> getLibraryItems(Predicates p) {
        return dataStore
                .getLibraryItems()
                .parallelStream()
                .filter(p::lonable)
                .collect(Collectors.toList());
    }

    private static class Predicates {
        private final Status status;
        public boolean lonable(LibraryItem item) {
            return item.getItemStatus().equals(status);
        }
        public Predicates(Status status) {
            this.status = status;
        }
    }
}
