package org.citylibrary.model.item;

import org.citylibrary.enums.ItemType;
import org.citylibrary.enums.Status;

public class Book extends LibraryItem {

    public Book(final int libraryId, final int itemId, final String title) {
        super(libraryId,itemId, ItemType.BOOK, title);
    }

    @Override
    public boolean isLoanable() {
        return this.getItemStatus().equals(Status.AVAILABLE);
    }
}
