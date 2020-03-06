package org.citylibrary.integration;

import org.assertj.core.api.Assertions;
import org.citylibrary.Library;
import org.citylibrary.db.CSVLibraryDataStore;
import org.citylibrary.db.DataStore;
import org.citylibrary.enums.ItemType;
import org.citylibrary.enums.Status;
import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.*;
import org.citylibrary.service.CSVDataService;
import org.citylibrary.service.DataService;
import org.citylibrary.service.LendingService;
import org.citylibrary.service.LibrarayItemLendingService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

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

        //Given
        dataService.reloadDataStore();

        //When
        List<LibraryItem> inventory = library.getCurrentInventory();

        //Then
        Assertions.assertThat(inventory)
                .isNotEmpty()
                .hasSize(12);
    }

    @Test
    public void canGetCurrentInventoryWhenLibraryHasNoItems() {

        //Given
        dataService.clearDataStore();

        //When
        List<LibraryItem> inventory = library.getCurrentInventory();

        //Then
        Assertions.assertThat(inventory)
                .isEmpty();
    }

    @Test
    public void canBorrowBook() {
        //Given
        LibraryItem borrowBook = library.findItem("The Pragmatic Programmer", ItemType.BOOK);

        //When
        boolean ret = library.borrowItem(borrower,borrowBook);

        //Then
        Assertions.assertThat(ret).isTrue();
        Assertions.assertThat(borrowBook)
                .extracting(LibraryItem::getItemStatus)
                .as(Status.LOANED.toString());
    }

    @Test
    public void canBorrowDvd() {
        //Given
        LibraryItem borrowDvd = library.findItem("Pi", ItemType.DVD);

        //When
        boolean ret = library.borrowItem(borrower,borrowDvd);

        //Then
        Assertions.assertThat(ret).isTrue();
        Assertions.assertThat(borrowDvd)
                .extracting(LibraryItem::getItemStatus)
                .as(Status.LOANED.toString());
    }

    @Test
    public void canBorrowVhs() {
        //Given
        LibraryItem borrowVhs = library.findItem("Hackers", ItemType.VHS);

        //When
        boolean ret = library.borrowItem(borrower,borrowVhs);

        //Then
        Assertions.assertThat(ret).isTrue();
        Assertions.assertThat(borrowVhs)
                .extracting(LibraryItem::getItemStatus)
                .as(Status.LOANED.toString());
    }

     @Test
    public void canReturnBorrowedItem() {

        //Given
         LibraryItem borrowBook = library.findItem("The Pragmatic Programmer", ItemType.BOOK);
         library.borrowItem(borrower,borrowBook);

         //When
         boolean ret = library.returnItem(borrowBook);

         //Then
         Assertions.assertThat(ret)
                 .isTrue();

         Assertions.assertThat(borrowBook)
                 .extracting(LibraryItem::getItemStatus)
                 .as(Status.AVAILABLE.toString());
    }

    @Test
    public void canNotReturnUnBorrowedItem() {

        //Given
        LibraryItem borrowBook = library.findItem("The Pragmatic Programmer", ItemType.BOOK);

        //When
        boolean ret = library.returnItem(borrowBook);

        //Then
        Assertions.assertThat(ret)
                .isFalse();

        Assertions.assertThat(borrowBook)
                .extracting(LibraryItem::getItemStatus)
                .as(Status.AVAILABLE.toString());
    }

    @Ignore
    @Test
    public void getOverDueItems() {
        LibraryItem borrowBook1 = library.getCurrentInventory().get(0);
        LibraryItem borrowBook2 = library.getCurrentInventory().get(1);

        library.borrowItem(borrower,borrowBook1);
        library.borrowItem(borrower,borrowBook2);

        // Make any one item overdue
        //dataService.getLoan().stream().findAny().ifPresent(item->item.setDueDate(LocalDate.now().plusDays(-3)));

        Assertions.assertThat(library.getOverDueItems())
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    public void cangetItemsBorrowedByGivenUser() {

        //Given
        dataService.reloadDataStore();
        LibraryItem borrowSoftwareBook = library.findItem("The Pragmatic Programmer", ItemType.BOOK);
        LibraryItem borrowJavaBook = library.findItem("Java Concurrency In Practice", ItemType.BOOK);
        LibraryItem borrowHackersVhs = library.findItem("Hackers", ItemType.VHS);

        library.borrowItem(borrower,borrowSoftwareBook);
        library.borrowItem(borrower,borrowJavaBook);
        library.borrowItem(borrower,borrowHackersVhs);

        //When
        List<Loan> loans = library.getItemBorrowedByUser(borrower);

        //Then
        Assertions.assertThat(loans)
                .isNotEmpty()
                .hasSize(3)
                .flatExtracting(Loan::getBorrower)
                .allMatch(b->b.equals(borrower));
    }

    @Test
    public void isBookAvailable() {
        //Given
        LibraryItem book = library.findItem("The Pragmatic Programmer", ItemType.BOOK);

        //When
        boolean available = library.isBookAvailable(book);

        //Then
        Assertions.assertThat(available)
                .isTrue();
    }
}