package org.citylibrary;

import org.citylibrary.db.DataStore;
import org.citylibrary.db.CSVLibraryDataStore;
import org.citylibrary.enums.Status;
import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.LibraryItem;
import org.citylibrary.service.CSVDataService;
import org.citylibrary.service.DataService;
import org.citylibrary.service.LendingService;
import org.citylibrary.service.LibrarayItemLendingService;

import javax.crypto.spec.PSource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        DataStore dataStore = CSVLibraryDataStore.getInstance();
        DataService CSVDataService = new CSVDataService(dataStore);
        LendingService librarayItemLendingService = new LibrarayItemLendingService(CSVDataService);

        Library library = new Library(CSVDataService, librarayItemLendingService);

        System.out.println("**************************** CURRENT INVENTORY *************************************");
        System.out.println();

        library.getCurrentInventory().parallelStream().forEach(System.out::println);
        Map<Integer, List<LibraryItem>> mp = library.getCurrentInventory().stream().collect(Collectors.groupingBy((LibraryItem::getItemId)));

        mp.entrySet().forEach(e-> {
            System.out.println("************************************************************************************");
            System.out.println("Item Id:" + e.getKey());
            System.out.println("Item Type:" + e.getValue().get(0).getType());
            System.out.println("Item Title:" + e.getValue().get(0).getTitle());
            System.out.println("Copies Available:" + e.getValue().stream().filter(t->t.getItemStatus().equals(Status.AVAILABLE)).collect(Collectors.counting()));
            System.out.println("Currently Loaned:" + e.getValue().stream().filter(t->t.getItemStatus().equals(Status.LOANED)).collect(Collectors.counting()));
            e.getValue().stream().forEach(System.out::println);

        });


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


        System.out.println();
        System.out.println("**************************** LOANED ITEMS *************************************");
        System.out.println();

        printCurrentlyLoanedItems(library);

        System.out.println();
        System.out.println("**************************** ITEMS BORROWED BY A BORROWER-1 *************************************");
        System.out.println();

        library.getItemBorrowedByUser(borrower1).forEach(loan-> System.out.println(loan.getItem()));


        System.out.println();
        System.out.println("**************************** ITEMS BORROWED BY A BORROWER-2 *************************************");
        System.out.println();

        library.getItemBorrowedByUser(borrower2).forEach(loan-> System.out.println(loan.getItem()));


        System.out.println();
        System.out.println("**************************** CURRENT LOANABLE INVENTORY AFTER FEW LOAN *************************************");
        System.out.println();

        printCurrentLoanableInventory(library);

        System.out.println();
        System.out.println("**************************** BORROWER-1 RETURNING AN ITEM *************************************");
        System.out.println();

        System.out.println(item3);
        library.returnItem(item3);



        System.out.println();
        System.out.println("**************************** ITEMS BORROWED BY A BORROWER-1 AFTER RETURNING AN ITEM *************************************");
        System.out.println();

        library.getItemBorrowedByUser(borrower1).forEach(loan-> System.out.println(loan.getItem()));


        System.out.println();
        System.out.println("**************************** PRINT OVERDUE ITEMS *************************************");
        System.out.println();

        printOverDueItems(library);
    }

    private static void printCurrentlyLoanedItems(Library library) {
        library.getCurrentInventory().stream().filter(item->!item.isLoanable()).forEach(System.out::println);
    }

    private static void printCurrentLoanableInventory(Library library) {
        library.getCurrentInventory().stream().filter(item->item.isLoanable()).forEach(System.out::println);
    }

    private static void printOverDueItems(Library library) {
        library.getOverDueItems().forEach(System.out::println);
    }

}
