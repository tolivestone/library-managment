package org.citylibrary.csvhelper;

import org.citylibrary.model.item.LibraryItem;
import org.assertj.core.api.Assertions;
import org.citylibrary.model.item.Loanable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class LibrarItemCsvReaderTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getLibraryItemsFromCsv() {
        List<LibraryItem> libraryItemList = LibrarItemCsvReader.getInstance().getLibraryItemsFromCsv();

        Assertions.assertThat(libraryItemList)
                .isNotEmpty()
                .hasSize(12);
    }
}