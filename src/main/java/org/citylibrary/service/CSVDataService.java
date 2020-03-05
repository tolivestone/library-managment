package org.citylibrary.service;

import org.citylibrary.csvhelper.LibrarItemCsvReader;
import org.citylibrary.db.DataStore;
import org.citylibrary.enums.Status;
import org.citylibrary.exception.LibraryOperationException;
import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.LibraryItem;
import org.citylibrary.model.item.Loan;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

//TODO: create interface DataService
//TODO: rename CSVDataService
public class CSVDataService implements DataService{

    private DataStore dataStore;

    //TODO: Review
    private ReentrantReadWriteLock.ReadLock readLock = new ReentrantReadWriteLock().readLock();
    private ReentrantReadWriteLock.WriteLock writeLock = new ReentrantReadWriteLock().writeLock();

    public CSVDataService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void clearDataStore() {
        //TODO: Review try catch finally
        writeLock.lock();
            dataStore
                    .getLibraryItems()
                    .clear();
        writeLock.unlock();
    }

    @Override
    public void reloadDataStore() {

        writeLock.lock();
        this.clearDataStore();
        dataStore
                .getLibraryItems()
                .addAll(LibrarItemCsvReader.getInstance().getLibraryItemsFromCsv());
        writeLock.unlock();
    }

    @Override
    public List<LibraryItem> getCurrentInventory() {

        readLock.lock();

        List<LibraryItem> currentInventoryList
                = dataStore
                    .getLibraryItems()
                    .parallelStream()
                    .collect(Collectors.toList());

        readLock.unlock();

        return  currentInventoryList;

    }

    @Override
    public List<LibraryItem> getCurrentLoanableInventory() {

        //TODO: review filter method
        readLock.lock();
        Predicates loanable = new Predicates(Status.AVAILABLE);
        List<LibraryItem> currentLoanableInventoryList = getLibraryItems(loanable);
        readLock.unlock();

        return  currentLoanableInventoryList;

    }



    @Override
    public List<LibraryItem> searchItemsByTitle(final String title) {
        Objects.requireNonNull(title, "Title cannot be null");

        readLock.lock();

        List<LibraryItem> itemList
                = dataStore
                    .getLibraryItems()
                    .parallelStream()
                    .filter(libraryItem -> libraryItem.getTitle().contains(title))
                    .collect(Collectors.toList());

        readLock.unlock();

        return itemList;
    }

    @Override
    public List<LibraryItem> searchItemsByLibraryId(final int libraryId) {
        Objects.requireNonNull(libraryId, "library id cannot be null");

        readLock.lock();

        List<LibraryItem> itemList = dataStore
                .getLibraryItems()
                .parallelStream()
                .filter(libraryItem -> libraryItem.getLibraryId() == libraryId)
                .collect(Collectors.toList());

        readLock.unlock();

        return itemList;
    }

    @Override
    public boolean addItem(final LibraryItem item) {
        Objects.requireNonNull(item, "Item cannot be null");

        boolean added = false;

        writeLock.lock();
            if(!dataStore.getLibraryItems().contains(item)) {
                 added =  dataStore.getLibraryItems().add(item);
            }
        writeLock.unlock();

        return added;
    }

    @Override
    public boolean removeItem(final LibraryItem item) throws LibraryOperationException {
        Objects.requireNonNull(item, "Item cannot be null");

        boolean removed = false;

        if(!dataStore.getLibraryItems().contains(item)) {
            throw new LibraryOperationException("Item does not exist");
        }

        writeLock.lock();
                removed = dataStore.getLibraryItems().remove(item);
        writeLock.unlock();

        return removed;
    }

    public boolean isBorrowed(final LibraryItem item){
        Objects.requireNonNull(item, "Item cannot be null");

        readLock.lock();
            boolean isBorrowed = dataStore
                                    .getLoans()
                                    .parallelStream()
                                    .anyMatch(loan -> loan.getItem().equals(item));
        readLock.unlock();
        return isBorrowed;
    }

    @Override
    public boolean addLoan(Person borrower, LibraryItem item, LocalDate issueDate, LocalDate dueDate) {
        Objects.requireNonNull(borrower, "Borrower cannot be null");
        Objects.requireNonNull(item, "Item cannot be null");
        Objects.requireNonNull(issueDate, "issueDate cannot be null");
        Objects.requireNonNull(dueDate, "dueDate cannot be null");

        boolean added = false;
        writeLock.lock();
            item.setItemStatus(Status.LOANED);
            Loan newLoan = new Loan(borrower,item,issueDate,dueDate);
            added = dataStore.getLoans().add(newLoan);
        writeLock.unlock();

        return added;
    }

    @Override
    public List<Loan> getLoan() {

        readLock.lock();

        List<Loan> loanList = dataStore
                .getLoans()
                .parallelStream()
                .collect(Collectors.toList());

        readLock.unlock();

        return  loanList;
    }

    public boolean returnLoanedItem(LibraryItem item) {

        writeLock.lock();
        Loan loanedItem = dataStore.getLoans().stream()
                .filter(litem -> litem.getItem().equals(item))
                .findAny()
                .orElse(null);

        if(loanedItem != null) {
            dataStore.getLoans().remove(loanedItem);
            item.setItemStatus(Status.AVAILABLE);
            return true;
        }
        writeLock.unlock();
        return false;

    }

    private List<LibraryItem> getLibraryItems(Predicates p) {
        return dataStore
                .getLibraryItems()
                .parallelStream()
                .filter(p::lonable)
                .collect(Collectors.toList());
    }

    private static class Predicates {
        private  Status status;
        public boolean lonable(LibraryItem item) {
            return item.getItemStatus().equals(status);
        }
        public Predicates(Status status) {
            this.status = status;
        }
    }
}
