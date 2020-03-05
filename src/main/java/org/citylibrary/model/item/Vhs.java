package org.citylibrary.model.item;

import org.citylibrary.enums.ItemType;
import org.citylibrary.enums.Status;

public class Vhs extends LibraryItem {
    public Vhs(final int libraryId, final int itemId,final  String title) {
        super(libraryId,itemId, ItemType.VHS, title);
    }

    @Override
    public boolean isLoanable() {
        return this.getItemStatus().equals(Status.AVAILABLE);
    }
}
