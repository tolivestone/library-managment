package org.citylibrary;

import org.citylibrary.enums.ItemType;
import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.LibraryItem;
import org.citylibrary.model.item.Loan;
import org.citylibrary.service.DataService;
import org.citylibrary.service.LendingService;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Library {

    //TODO: review singleton
    // Singletone Library Instance

    private final DataService dataService;
    private final LendingService lendingService;

    private static final int LOAN_PERIOD = 7;

    public Library(DataService dataService, LendingService lendingService) {
        this.dataService = dataService;
        this.lendingService = lendingService;
    }

    public boolean borrowItem(Person customer, LibraryItem item)  {
        Objects.requireNonNull(customer, "Customer cannot be null");
        Objects.requireNonNull(item, "Item cannot be null");

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(LOAN_PERIOD);

        return lendingService.borrowItem(customer,item,issueDate,dueDate);
    }

    public boolean returnItem(LibraryItem item) {
        Objects.requireNonNull(item, "Item cannot be null");
        return lendingService.returnItem(item);
    }

    public List<Loan> getOverDueItems() {
        return dataService
                .getLoan()
                .parallelStream()
                .filter(item->item.getDueDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public List<Loan> getItemBorrowedByUser(Person customer) {
        Objects.requireNonNull(customer,"Customer cannot be null");
        return dataService
                .getLoan()
                .parallelStream()
                .filter(item -> item.getCustomer().equals(customer))
                .collect(Collectors.toList());
    }

    public List<LibraryItem> getCurrentInventory() {
        return dataService
                .getCurrentInventory();
    }

    public boolean isBookAvailable(LibraryItem libraryItem) {
        Objects.requireNonNull(libraryItem, "Library Item cannot be null");

        return dataService
                .getCurrentLoanableInventory()
                .parallelStream()
                .anyMatch(item->item.equals(libraryItem) && item.getType() == ItemType.BOOK);
    }

    public LibraryItem findItemByTitleAndType(String title, ItemType itemType) {
        return
                dataService.getCurrentInventory()
                .parallelStream()
                .filter(item-> item.getTitle().contains(title) && item.getType().equals(itemType))
                .findFirst().get();
    }
}
