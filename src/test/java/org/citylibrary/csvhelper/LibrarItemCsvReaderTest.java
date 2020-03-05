package org.citylibrary.csvhelper;

import org.citylibrary.model.item.LibraryItem;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class LibrarItemCsvReaderTest {
    @Test
    public void getLibraryItemsFromCsv() {
        List<LibraryItem> libraryItemList = LibrarItemCsvReader.getInstance().getLibraryItemsFromCsv();

        Assertions.assertThat(libraryItemList)
                .isNotEmpty()
                .hasSize(12);
    }
}