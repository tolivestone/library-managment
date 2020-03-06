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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

//TODO: create interface DataService
public class CSVDataService implements DataService{

    private final DataStore dataStore;

    //TODO: Review
    
    private final ReadWriteLock invtentoryReadWriteLock = new ReentrantReadWriteLock();
    private final ReadWriteLock loanReadWriteLock = new ReentrantReadWriteLock();


    public CSVDataService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void clearDataStore() {
        try {
            invtentoryReadWriteLock.writeLock().lock();
            dataStore.getLibraryItems().clear();
            dataStore.getLoans().clear();
        }finally {
            invtentoryReadWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void reloadDataStore() {

        try {
            invtentoryReadWriteLock.writeLock().lock();
            this.clearDataStore();
            dataStore.getLibraryItems()
                    .addAll(LibrarItemCsvReader.getInstance().getLibraryItemsFromCsv());
        }finally {
            invtentoryReadWriteLock.writeLock().unlock();
        }
    }

    @Override
    public List<LibraryItem> getCurrentInventory() {

        List<LibraryItem> currentInventoryList = new ArrayList<>();;
        try {
            invtentoryReadWriteLock.readLock().lock();
            currentInventoryList =
                    dataStore.getLibraryItems().parallelStream().collect(Collectors.toList());
        }finally {
            invtentoryReadWriteLock.readLock().unlock();
        }
        return  currentInventoryList;
    }

    @Override
    public List<LibraryItem> getCurrentLoanableInventory() {

        List<LibraryItem> currentLoanableInventoryList = new ArrayList<>();
        try {
            invtentoryReadWriteLock.readLock().lock();
            Predicates loanable = new Predicates(Status.AVAILABLE);
            currentLoanableInventoryList = getLibraryItems(loanable);
        }finally {
            invtentoryReadWriteLock.readLock().unlock();
        }

        return  currentLoanableInventoryList;
    }



    @Override
    public List<LibraryItem> searchItemsByTitle(final String title) {
        Objects.requireNonNull(title, "Title cannot be null");

        List<LibraryItem> itemList = new ArrayList<>();
        try {
            invtentoryReadWriteLock.readLock().lock();
            itemList = dataStore.getLibraryItems().parallelStream()
                    .filter(libraryItem -> libraryItem.getTitle().contains(title)).collect(Collectors.toList());
        } finally {
            invtentoryReadWriteLock.readLock().unlock();
        }

        return itemList;
    }

    @Override
    public List<LibraryItem> searchItemsByLibraryId(final int libraryId) {

        List<LibraryItem> itemList = new ArrayList<>();
        try{
            invtentoryReadWriteLock.readLock().lock();
        itemList = dataStore.getLibraryItems().parallelStream()
                .filter(libraryItem -> libraryItem.getLibraryId() == libraryId).collect(Collectors.toList());
        } finally {
            invtentoryReadWriteLock.readLock().unlock();
        }

        return itemList;
    }

    @Override
    public boolean addLibraryItem(final LibraryItem item) {
        Objects.requireNonNull(item, "Item cannot be null");

        boolean added = false;
        try {
            invtentoryReadWriteLock.writeLock().lock();
                if (!dataStore.getLibraryItems().contains(item)) {
                    added = dataStore.getLibraryItems().add(item);
                }
            }finally {
            invtentoryReadWriteLock.writeLock().unlock();
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
            invtentoryReadWriteLock.writeLock().lock();
            removed = dataStore.getLibraryItems().remove(item);
        } finally {
            invtentoryReadWriteLock.writeLock().unlock();
        }

        return removed;
    }

    public boolean isBorrowed(final LibraryItem item){
        Objects.requireNonNull(item, "Item cannot be null");

        boolean isBorrowed;
        try {
            loanReadWriteLock.readLock().lock();
            isBorrowed = dataStore.getLoans().parallelStream()
                    .anyMatch(loan -> loan.getItem().equals(item));
        }finally {
            loanReadWriteLock.readLock().unlock();
        }
        return isBorrowed;
    }

    @Override
    public boolean addLoan(Person customer, LibraryItem item, LocalDate issueDate, LocalDate dueDate) {
        Objects.requireNonNull(customer, "Customer cannot be null");
        Objects.requireNonNull(item, "Item cannot be null");
        Objects.requireNonNull(issueDate, "issueDate cannot be null");
        Objects.requireNonNull(dueDate, "dueDate cannot be null");

        boolean added;
        try {
            loanReadWriteLock.writeLock().lock();invtentoryReadWriteLock.writeLock().lock();
            item.setItemStatus(Status.LOANED);
            Loan newLoan = new Loan(customer, item, issueDate, dueDate);
            added = dataStore.getLoans().add(newLoan);
        } finally {
            loanReadWriteLock.writeLock().unlock();
        }

        return added;
    }

    @Override
    public List<Loan> getLoan() {

        List<Loan> loanList = new ArrayList<>();
        try {
            loanReadWriteLock.readLock().lock();
            loanList = dataStore.getLoans().parallelStream().collect(Collectors.toList());
        } finally {
            loanReadWriteLock.readLock().unlock();
        }

        return  loanList;
    }

    public boolean returnLoanedItem(LibraryItem item) {

        boolean success= false;
        try {
            loanReadWriteLock.writeLock().lock();
            Loan loanedItem = dataStore.getLoans().stream()
                    .filter(litem -> litem.getItem().equals(item)).findAny().orElse(null);

            if (loanedItem != null) {
                item.setItemStatus(Status.AVAILABLE);
                success = dataStore.getLoans().remove(loanedItem);
            }
        }finally {
            loanReadWriteLock.writeLock().unlock();
        }
        return success;
    }

    private List<LibraryItem> getLibraryItems(Predicates p) {
        return dataStore.getLibraryItems()
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
