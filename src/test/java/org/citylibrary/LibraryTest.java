package org.citylibrary;

import org.assertj.core.api.Assertions;
import org.citylibrary.enums.ItemType;
import org.citylibrary.model.actor.Borrower;
import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.Book;
import org.citylibrary.model.item.Dvd;
import org.citylibrary.model.item.LibraryItem;
import org.citylibrary.model.item.Loan;
import org.citylibrary.service.CSVDataService;
import org.citylibrary.service.DataService;
import org.citylibrary.service.LendingService;
import org.citylibrary.service.LibrarayItemLendingService;
import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class LibraryTest {

    DataService mockDataService;
    LendingService mockLendingService;
    Library library;
    List<LibraryItem> items;
    List<Loan> loans;
    Borrower borrower;

    @Before
    public void setUp() {

        //Mocking dependent services
        mockDataService = mock(CSVDataService.class);
        mockLendingService = mock(LibrarayItemLendingService.class);
        library = new Library(mockDataService,mockLendingService);

        items = List.of(
                new LibraryItem.LibraryItemBuilder(1,1, ItemType.BOOK, "Introduction to Algorithms").build(),
                new LibraryItem.LibraryItemBuilder(2,1, ItemType.BOOK, "Introduction to Algorithms").build(),
                new LibraryItem.LibraryItemBuilder(3,1, ItemType.BOOK, "Introduction to Algorithms").build(),
                new LibraryItem.LibraryItemBuilder(4,2, ItemType.DVD, "Pi").build(),
                new LibraryItem.LibraryItemBuilder(5,3, ItemType.DVD, "Frozen").build()
        );

        borrower = new Borrower(1,"Borrower1", "B Last Name");

        loans = new ArrayList<>();
        loans.addAll(List.of(
                new Loan(borrower,items.get(0),LocalDate.now(),LocalDate.now().plusDays(-2)),
                new Loan(borrower,items.get(1),LocalDate.now(),LocalDate.now().plusDays(-3)),
                new Loan(borrower,items.get(2),LocalDate.now(),LocalDate.now().plusDays(7))
        ));


    }

    @Test
    public void getCurrentInventory() {

        when(mockDataService.getCurrentInventory()).thenReturn(items);

        Assertions.assertThat(library.getCurrentInventory())
                .isNotEmpty()
                .hasSize(5);
        verify(mockDataService, atMost(1)).getCurrentInventory();
    }

    @Test
    public void borrowItem() {
        LibraryItem item = items.get(0);
        Person borrower = new Borrower(1,"Borrower 1","Borrower's last name");
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(7);
        when(mockLendingService.borrowItem(borrower,item, today, dueDate)).thenReturn(true);

        Assertions.assertThat(library.borrowItem(borrower,item))
                .isTrue();
        verify(mockLendingService, atMost(1))
                .borrowItem(borrower,item, today, dueDate);
    }

    @Test
    public void returnItem() {
        LibraryItem item = items.get(0);

        when(mockLendingService.returnItem(item)).thenReturn(true);

        Assertions.assertThat(library.returnItem(item))
                .isTrue();
        verify(mockLendingService, atMost(1))
                .returnItem(item);
    }

    @Test
    public void getOverDueItems() {

        when(mockDataService.getLoan()).thenReturn(loans);

        Assertions.assertThat(library.getOverDueItems())
                .isNotEmpty()
                .hasSize(2);
        verify(mockDataService, atMost(1))
                .getLoan();
    }

    @Test
    public void getItemBorrowedByUser() {
        when(mockDataService.getLoan()).thenReturn(loans);

        Assertions.assertThat(library.getItemBorrowedByUser(borrower))
                .isNotEmpty()
                .hasSize(3);
        verify(mockDataService, atMost(1))
                .getLoan();
    }

    @Test
    public void isBookAvailable() {
        when(mockDataService.getCurrentLoanableInventory()).thenReturn(items);

        LibraryItem availableBook = items.get(0);

        Assertions.assertThat(library.isBookAvailable(availableBook))
                .isTrue();
        verify(mockDataService, atMost(1))
                .getCurrentLoanableInventory();

    }

}