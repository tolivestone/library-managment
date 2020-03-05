package org.citylibrary.service;

import org.assertj.core.api.Assertions;
import org.citylibrary.db.DataStore;
import org.citylibrary.db.CSVLibraryDataStore;
import org.citylibrary.enums.Status;
import org.citylibrary.exception.LibraryOperationException;
import org.citylibrary.model.item.Book;
import org.citylibrary.model.item.Dvd;
import org.citylibrary.model.item.LibraryItem;
import org.citylibrary.model.item.Vhs;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CSVDataServiceTest {


    DataStore dataStore;
    CSVDataService CSVDataService;

    @Before
    public void setUp() throws Exception {
        dataStore = CSVLibraryDataStore.getInstance();
        CSVDataService = new CSVDataService(dataStore);
        CSVDataService.clearDataStore();

    }


    @Test
    public void getCurrentInventory() {

        List<LibraryItem> items = new ArrayList<>();
        items.add(new Book(1,1,"Introduction to Algorithms"));
        items.add(new Book(2,1,"Introduction to Algorithms"));
        items.add(new Book(3,1,"Introduction to Algorithms"));
        items.add(new Dvd(4,2,"Test DVD"));
        items.add(new Dvd(5,3,"Test DVD"));

        items.parallelStream().forEach(item-> CSVDataService.addItem(item));

        //Checking inventory
        Assertions.assertThat(CSVDataService.getCurrentInventory())
                .isNotEmpty()
                .hasSize(5)
                .doesNotContainNull()
                .allMatch(d->d.getItemStatus() == Status.AVAILABLE);

        //Checking inventory with loaned items
        items.get(0).setItemStatus(Status.LOANED);
        items.get(4).setItemStatus(Status.LOANED);

        Assertions.assertThat(CSVDataService.getCurrentInventory())
                .isNotEmpty()
                .hasSize(5)
                .doesNotContainNull()
                .anyMatch(d->d.getItemStatus() == Status.LOANED);
    }

    @Test
    public void getCurrentLoanableInventory() {

        List<LibraryItem> items = new ArrayList<>();
        items.add(new Book(1,1,"Introduction to Algorithms"));
        items.add(new Book(2,1,"Introduction to Algorithms"));
        items.add(new Book(3,1,"Introduction to Algorithms"));
        items.add(new Dvd(4,2,"Test DVD"));
        items.add(new Dvd(5,3,"Test DVD"));

        items.parallelStream().forEach(item-> CSVDataService.addItem(item));

        //Checking inventory with loaned items
        items.get(0).setItemStatus(Status.LOANED);
        items.get(4).setItemStatus(Status.LOANED);

        Assertions.assertThat(CSVDataService.getCurrentLoanableInventory())
                .isNotEmpty()
                .hasSize(3)
                .extracting("itemStatus")
                .doesNotContain(Status.LOANED)
                .allMatch(d->d.equals(Status.AVAILABLE));
    }

    @Test
    public void searchItemsByTitle_withMatchingItems() {

        List<LibraryItem> items = new ArrayList<>();
        items.add(new Book(1,1,"Introduction to Algorithms"));
        items.add(new Book(2,1,"Introduction to Algorithms"));
        items.add(new Book(3,1,"Introduction to Algorithms"));
        items.add(new Dvd(4,2,"Test DVD"));
        items.add(new Dvd(5,3,"Test DVD"));

        items.parallelStream().forEach(item-> CSVDataService.addItem(item));

        Assertions.assertThat(CSVDataService.searchItemsByTitle("Introduction to Algorithms"))
                .isNotEmpty()
                .hasSize(3)
                .flatExtracting(LibraryItem::getTitle)
                .allMatch(d->d.equals("Introduction to Algorithms"));
    }

    @Test
    public void searchItemsByTitle_withNoMatchingItems() {

        List<LibraryItem> items = new ArrayList<>();
        items.add(new Book(1,1,"Introduction to Algorithms"));
        items.add(new Book(2,1,"Introduction to Algorithms"));
        items.add(new Book(3,1,"Introduction to Algorithms"));
        items.add(new Dvd(4,2,"Test DVD"));
        items.add(new Dvd(5,3,"Test DVD"));

        items.parallelStream().forEach(item-> CSVDataService.addItem(item));

        Assertions.assertThat(CSVDataService.searchItemsByTitle("Fake title"))
                .isEmpty();
    }

    @Test
    public void addItem_happyPath() {

        Assertions.assertThat(CSVDataService.searchItemsByLibraryId(1))
                .isEmpty();

        LibraryItem vhs = new Vhs(1,2,"WarGames");

        boolean isAdded = CSVDataService.addItem(vhs);

        Assertions.assertThat(isAdded)
                .isEqualTo(true);

        Assertions.assertThat(CSVDataService.searchItemsByLibraryId(1))
                .isNotEmpty()
                .hasSize(1)
                .contains(vhs)
                .flatExtracting(LibraryItem::getTitle)
                .allMatch(d-> d.equals("WarGames"));
    }

    @Test
    public void add_withNullParameter() {

        Assertions.assertThat(CSVDataService.searchItemsByLibraryId(1))
                .isEmpty();

        Assertions.assertThatNullPointerException()
                .isThrownBy(()-> CSVDataService.addItem(null))
                .withMessage("Item cannot be null");
    }

    @Test
    public void removeItem_happyPath() throws LibraryOperationException {

        LibraryItem vhs = new Vhs(1,2,"WarGames");
        CSVDataService.addItem(vhs);

        Assertions.assertThat(CSVDataService.searchItemsByLibraryId(1))
                .isNotEmpty()
                .hasSize(1)
                .contains(vhs)
                .flatExtracting(LibraryItem::getTitle)
                .allMatch(d-> d.equals("WarGames"));

        boolean isRemoved = CSVDataService.removeItem(vhs);

        Assertions.assertThat(isRemoved)
                .isEqualTo(true);

        Assertions.assertThat(CSVDataService.searchItemsByLibraryId(1))
                .isEmpty();
    }

    @Test
    public void remove_withNullParameter() {

        Assertions.assertThatNullPointerException()
                .isThrownBy(()-> CSVDataService.removeItem(null))
                .withMessage("Item cannot be null");
    }

    @Test
    public void remove_nonExistingLibraryItem() {

        Assertions.assertThat(CSVDataService.getCurrentInventory())
                .isEmpty();

        LibraryItem vhs = new Vhs(1,2,"WarGames");

        Assertions.assertThatExceptionOfType(LibraryOperationException.class)
                .isThrownBy(()-> CSVDataService.removeItem(vhs))
                .withMessage("Item does not exist");
    }


    @Test
    public void isBorrowed() {
    }
}