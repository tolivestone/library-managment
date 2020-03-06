package org.citylibrary.model.item;

import org.citylibrary.enums.ItemType;
import org.citylibrary.enums.Status;

public final class Vhs extends LibraryItem {
    protected Vhs(final int libraryId, final int itemId,final  String title, final String description) {
        super(libraryId,itemId, ItemType.VHS, title,description);
    }

    @Override
    public boolean isLoanable() {
        return this.getItemStatus().equals(Status.AVAILABLE);
    }
}
