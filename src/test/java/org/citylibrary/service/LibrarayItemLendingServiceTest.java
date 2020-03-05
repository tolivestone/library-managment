package org.citylibrary.service;

import org.assertj.core.api.Assertions;
import org.citylibrary.db.DataStore;
import org.citylibrary.enums.Status;
import org.citylibrary.model.actor.Borrower;
import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.Book;
import org.citylibrary.model.item.LibraryItem;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class LibrarayItemLendingServiceTest {

    @Test
    @Ignore
    public void borrowItem_happyPath_1() {
        DataStore mockDataStore = mock(DataStore.class);
        CSVDataService mockCSVDataService = new CSVDataService(mockDataStore);
        LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        LibraryItem book = new Book(1,1,"Test Book");
        Person borrower1 = new Borrower(1,"Borrower-1", "Borrower Last name");
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(7);

        when(mockCSVDataService.addLoan(borrower1, book, today, dueDate)).thenReturn(true);

        Assertions
                .assertThat(librarayItemLendingService.borrowItem(borrower1, book, today, dueDate))
                .isEqualTo(true);

        verify(mockCSVDataService, atMost(1))
                .addLoan(borrower1, book, today, dueDate);
    }


    @Test
    public void borrowItem_happyPath() {
        CSVDataService mockCSVDataService = mock(CSVDataService.class);
        LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        LibraryItem book = new Book(1,1,"Test Book");
        Person borrower1 = new Borrower(1,"Borrower-1", "Borrower Last name");
        LocalDate today = LocalDate.now();
        LocalDate dueDate = today.plusDays(7);

        when(mockCSVDataService.addLoan(borrower1, book, today, dueDate)).thenReturn(true);

        Assertions
                .assertThat(librarayItemLendingService.borrowItem(borrower1, book, today, dueDate))
                .isEqualTo(true);

        verify(mockCSVDataService, atMost(1))
                .addLoan(borrower1, book, today, dueDate);
    }

    @Test
    public void borrowItem_withNullParameters() {
        CSVDataService mockCSVDataService = mock(CSVDataService.class);
        LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        Assertions
                .assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(
                        ()-> librarayItemLendingService
                                .borrowItem(null, null, null, null));

        verify(mockCSVDataService, atMost(1))
                .addLoan(null, null, null, null);
    }

    @Test
    public void returnItem_happyPath() {

        CSVDataService mockCSVDataService = mock(CSVDataService.class);
        LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        LibraryItem book = new Book(1,1,"Test Book");
        book.setItemStatus(Status.LOANED);

        when(mockCSVDataService.returnLoanedItem(book)).thenReturn(true);

        Assertions.assertThat(librarayItemLendingService.returnItem(book))
                .isEqualTo(true);

        verify(mockCSVDataService, atMost(1)).returnLoanedItem(book);
    }

    @Test
    public void returnItem_withNonBorrowedItem() {

        CSVDataService mockCSVDataService = mock(CSVDataService.class);
        LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        LibraryItem book = new Book(1,1,"Test Book");

        when(mockCSVDataService.returnLoanedItem(book)).thenReturn(false);

        Assertions.assertThat(librarayItemLendingService.returnItem(book))
                .isEqualTo(false);

        verify(mockCSVDataService, never()).returnLoanedItem(book);
    }

    @Test
    public void returnItem_withNullParameters() {

        CSVDataService mockCSVDataService = mock(CSVDataService.class);
        LibrarayItemLendingService librarayItemLendingService = new LibrarayItemLendingService(mockCSVDataService);

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> librarayItemLendingService.returnItem(null));

        verify(mockCSVDataService, never()).isBorrowed(null);
        verify(mockCSVDataService, never()).returnLoanedItem(null);
    }
}