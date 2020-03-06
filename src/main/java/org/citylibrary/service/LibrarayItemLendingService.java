package org.citylibrary.service;

import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.LibraryItem;

import java.time.LocalDate;

public class LibrarayItemLendingService implements LendingService{

    private final DataService dataService;

    public LibrarayItemLendingService(DataService dataService){
        this.dataService = dataService;
    }

    @Override
    public boolean borrowItem(Person customer, LibraryItem item, LocalDate issueDate, LocalDate dueDate) {
        if(customer == null || item == null || issueDate == null || dueDate == null)
            throw new IllegalArgumentException("One or more arguments are null");

        return dataService.addLoan(customer, item, issueDate, dueDate);
    }

    @Override
    public boolean returnItem(LibraryItem item) {
        if(item == null) throw new IllegalArgumentException("Item cannot be null");

        if(!item.isLoanable()) {
            return dataService.returnLoanedItem(item);
        }
        return  false;
    }

}
