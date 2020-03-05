package org.citylibrary.model.item;

import org.citylibrary.enums.ItemType;
import org.citylibrary.enums.Status;

public class Dvd extends LibraryItem {

    public Dvd(final int libraryId, final int itemId,final String title) {
        super(libraryId,itemId, ItemType.DVD, title);
    }

    @Override
    public boolean isLoanable() {
        return this.getItemStatus().equals(Status.AVAILABLE);
    }
}
