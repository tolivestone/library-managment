package org.citylibrary.application;

import org.citylibrary.Library;
import org.citylibrary.db.DataStore;
import org.citylibrary.db.CSVLibraryDataStore;
import org.citylibrary.enums.Status;
import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.LibraryItem;
import org.citylibrary.model.item.Loanable;
import org.citylibrary.service.CSVDataService;
import org.citylibrary.service.DataService;
import org.citylibrary.service.LendingService;
import org.citylibrary.service.LibrarayItemLendingService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class LibraryApplication
{
    private Library library;
    DataStore dataStore;
    DataService CSVDataService;
    LendingService librarayItemLendingService;

    public static void main( String[] args )
    {

        LibraryApplication libraryApplication = new LibraryApplication();
        libraryApplication.inititalizeLibrary();

        libraryApplication.printCurrentInventory();

        // Borrower-1 borrows 2 items
        // Borrower- 2 borrows 1 item
        libraryApplication.borrowItems();

        //Prints items borrowed by Borrower-1 and Borrower-2
        libraryApplication.printItemsBorrowedByUsers();

        // Prints currently loanable items excluding items borrowed by Borrower-1 and Borrower-2
        libraryApplication.printCurrentLoanableInventory();

        //Borrower-1 returns an item
        libraryApplication.returnItems();

        libraryApplication.printOverDueItems();

    }

    private void returnItems() {
        System.out.println();
        System.out.println("**************************** BORROWER-1 RETURNING AN ITEM *************************************");
        System.out.println();

        LibraryItem book = library.getCurrentInventory().get(5);
        System.out.println(book);
        library.returnItem(book);
    }

    private void printCurrentLoanableInventory() {
        System.out.println();
        System.out.println("**************************** CURRENT LOANABLE INVENTORY AFTER FEW LOAN *************************************");
        System.out.println();

        library.getCurrentInventory().stream().filter(Loanable::isLoanable).forEach(System.out::println);
    }

    private void printItemsBorrowedByUsers() {
        System.out.println();
        System.out.println("**************************** ITEMS BORROWED BY A BORROWER-1 *************************************");
        System.out.println();

        Person borrower1 = dataStore.getBorrowers().get(0);
        library.getItemBorrowedByUser(borrower1).forEach(loan-> System.out.println(loan.getItem()));


        System.out.println();
        System.out.println("**************************** ITEMS BORROWED BY A BORROWER-2 *************************************");
        System.out.println();

        Person borrower2 = dataStore.getBorrowers().get(1);
        library.getItemBorrowedByUser(borrower2).forEach(loan-> System.out.println(loan.getItem()));
    }

    private void borrowItems() {

        System.out.println();
        System.out.println("**************************** BORROWER-1 BORROWS ITEMS *************************************");
        System.out.println();

        Person borrower1 = dataStore.getBorrowers().get(0);
        LibraryItem item = CSVDataService.searchItemsByLibraryId(1).get(0);
        LibraryItem item3 = CSVDataService.searchItemsByLibraryId(5).get(0);

        System.out.println(item);
        System.out.println(item3);

        library.borrowItem(borrower1,item);
        library.borrowItem(borrower1,item3);


        System.out.println();
        System.out.println("**************************** BORROWER-2 BORROWS A ITEM *************************************");
        System.out.println();

        Person borrower2 = dataStore.getBorrowers().get(1);
        LibraryItem item2 = CSVDataService.searchItemsByLibraryId(3).get(0);

        System.out.println(item2);

        library.borrowItem(borrower2,item2);

    }

    private void printCurrentInventory() {
        System.out.println("**************************** CURRENT INVENTORY *************************************");
        System.out.println();

        //library.getCurrentInventory().parallelStream().forEach(System.out::println);
        Map<Integer, List<LibraryItem>> mp = library.getCurrentInventory().stream().collect(Collectors.groupingBy((LibraryItem::getItemId)));

        mp.forEach((key, value) -> {
            System.out.println("************************************************************************************");
            System.out.println("Item Id:" + key);
            System.out.println("Item Type:" + value.get(0).getType());
            System.out.println("Item Title:" + value.get(0).getTitle());
            System.out.println("Copies Available:" + value.stream().filter(t -> t.getItemStatus().equals(Status.AVAILABLE)).count());
            System.out.println("Currently Loaned:" + value.stream().filter(t -> t.getItemStatus().equals(Status.LOANED)).count());
            value.forEach(System.out::println);

        });

    }

    private void inititalizeLibrary() {
        dataStore = CSVLibraryDataStore.getInstance();
        CSVDataService = new CSVDataService(dataStore);
        librarayItemLendingService = new LibrarayItemLendingService(CSVDataService);
        library = new Library(CSVDataService, librarayItemLendingService);
    }


    private void printOverDueItems() {
        System.out.println();
        System.out.println("**************************** PRINT OVERDUE ITEMS *************************************");
        System.out.println();

        library.getOverDueItems().forEach(System.out::println);
    }

}
