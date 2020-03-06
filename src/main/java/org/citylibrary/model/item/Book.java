package org.citylibrary.model.item;

import org.citylibrary.enums.ItemType;
import org.citylibrary.enums.Status;

public final class Book extends LibraryItem {

    protected Book(final int libraryId, final int itemId, final String title, final String description) {
        super(libraryId,itemId, ItemType.BOOK, title,description);
    }

    @Override
    public boolean isLoanable() {
        return this.getItemStatus().equals(Status.AVAILABLE);
    }
}
