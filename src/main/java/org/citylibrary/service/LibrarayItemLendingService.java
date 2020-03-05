package org.citylibrary.service;

import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.LibraryItem;

import java.time.LocalDate;
import java.util.Objects;

public class LibrarayItemLendingService implements LendingService{

    private DataService dataService;

    public LibrarayItemLendingService(DataService dataService){
        this.dataService = dataService;
    }

    @Override
    public boolean borrowItem(Person borrower, LibraryItem item, LocalDate issueDate, LocalDate dueDate) {
        if(borrower == null || item == null || issueDate == null || dueDate == null)
            throw new IllegalArgumentException("One or more arguments are null");

        if(dataService.addLoan(borrower, item, issueDate, dueDate))
            return true;
        return  false;
    }

    @Override
    public boolean returnItem(LibraryItem item) {
        if(item == null) throw new IllegalArgumentException("Item cannot be null");

        if(!item.isLoanable()) {
           if(dataService.returnLoanedItem(item))
                return true;
        }
        return  false;
    }

}
