package org.citylibrary.model.item;

import org.citylibrary.enums.ItemType;
import org.citylibrary.enums.Status;

public class CompactDisc extends LibraryItem {

    protected CompactDisc(final int libraryId, final int itemId, final String title, final String description) {
        super(libraryId,itemId, ItemType.BOOK, title,description);
    }

    @Override
    public boolean isLoanable() {
        return this.getItemStatus().equals(Status.AVAILABLE);
    }
}
