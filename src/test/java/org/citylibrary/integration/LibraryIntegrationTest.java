package org.citylibrary.integration;

import org.assertj.core.api.Assertions;
import org.citylibrary.Library;
import org.citylibrary.db.CSVLibraryDataStore;
import org.citylibrary.db.DataStore;
import org.citylibrary.enums.Status;
import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.*;
import org.citylibrary.service.CSVDataService;
import org.citylibrary.service.DataService;
import org.citylibrary.service.LendingService;
import org.citylibrary.service.LibrarayItemLendingService;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

public class LibraryIntegrationTest {

    DataService dataService;
    LendingService lendingService;
    Library library;
    Person borrower;
    DataStore dataStore;

    @Before
    public void setUp() {
        dataStore = CSVLibraryDataStore.getInstance();
        dataService = new CSVDataService(dataStore);
        dataService.reloadDataStore();
        lendingService = new LibrarayItemLendingService(dataService);
        library = new Library(dataService,lendingService);
        borrower = dataStore.getBorrowers().get(0);
    }

    @Test
    public void getCurrentInventory() {

        Assertions.assertThat(library.getCurrentInventory())
                .isNotEmpty()
                .hasSize(12);
    }

   @Test
    public void borrowItem() {

        LibraryItem borrowBook = library.getCurrentInventory().get(0);

        Assertions.assertThat(library.borrowItem(borrower,borrowBook))
                .isTrue();

        Assertions.assertThat(borrowBook)
                .extracting(LibraryItem::getItemStatus)
                .as(Status.LOANED.toString());
    }

     @Test
    public void returnItem() {

         LibraryItem borrowBookAndReturnBook = library.getCurrentInventory().get(0);

         library.borrowItem(borrower,borrowBookAndReturnBook);

         Assertions.assertThat(library.returnItem(borrowBookAndReturnBook))
                 .isTrue();

         Assertions.assertThat(borrowBookAndReturnBook)
                 .extracting(LibraryItem::getItemStatus)
                 .as(Status.AVAILABLE.toString());
    }

    @Test
    public void getOverDueItems() {
        LibraryItem borrowBook1 = library.getCurrentInventory().get(0);
        LibraryItem borrowBook2 = library.getCurrentInventory().get(1);

        library.borrowItem(borrower,borrowBook1);
        library.borrowItem(borrower,borrowBook2);

        // Make any one item overdue
        dataService.getLoan().stream().findAny().ifPresent(item->item.setDueDate(LocalDate.now().plusDays(-3)));

        Assertions.assertThat(library.getOverDueItems())
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    public void getItemBorrowedByUser() {
        LibraryItem borrowBook1 = library.getCurrentInventory().get(0);
        LibraryItem borrowBook2 = library.getCurrentInventory().get(1);
        LibraryItem borrowBook3 = library.getCurrentInventory().get(3);

        library.borrowItem(borrower,borrowBook1);
        library.borrowItem(borrower,borrowBook2);
        library.borrowItem(borrower,borrowBook3);

        Assertions.assertThat(library.getItemBorrowedByUser(borrower))
                .isNotEmpty()
                .hasSize(3);

    }

    @Test
    public void isBookAvailable() {
        LibraryItem book = library.getCurrentInventory().get(3);
        Assertions.assertThat(library.isBookAvailable(book))
                .isTrue();
    }
}