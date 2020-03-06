package org.citylibrary.model.item;

import org.citylibrary.enums.ItemType;
import org.citylibrary.enums.Status;

import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class LibraryItem implements Loanable {

    private final int libraryId;            //required unique library id
    private final int itemId;               //required
    private final ItemType type;            //required
    private final String title;             //required
    private volatile Status itemStatus;     //required

    private final String description;       //optional

    protected LibraryItem(final int libraryId, final int itemId,final  ItemType type,final String title, String description) {
        this.libraryId = libraryId;
        this.itemId = itemId;
        this.type = type;
        this.title = title;
        this.itemStatus = Status.AVAILABLE;
        this.description = description;
    }

    public int getLibraryId() {
        return libraryId;
    }

    public int getItemId() {
        return itemId;
    }

    public ItemType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Status getItemStatus() {
        return itemStatus;
    }

    public String getDescription() {
        return description;
    }

    public final void setItemStatus(final Status itemStatus) {
            this.itemStatus = itemStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryItem that = (LibraryItem) o;
        return libraryId == that.libraryId;
    }

    @Override
    public int hashCode() {
        return 31 * libraryId;
    }

    @Override
    public String toString() {
        return "[" +
                "libraryId=" + libraryId +
                ", itemId=" + itemId +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", itemStatus=" + itemStatus +
                ", description='" + description + '\'' +
                ']';
    }

    //Builder for library items, This will abstract item creation and make sure no item is created with invalid data
    public static class LibraryItemBuilder {

        private final int libraryId;
        private final int itemId;
        private final ItemType type;
        private final String title;
        private String description = "";

        public LibraryItemBuilder(int libraryId, int itemId, ItemType type, String title) {
            if(libraryId <=0 || itemId <=0 || type == null || title == null || title.isEmpty())
                        throw new IllegalArgumentException("One or more argurment are not set or valid");

            this.libraryId = libraryId;
            this.itemId = itemId;
            this.type = type;
            this.title = title;
        }

        public LibraryItemBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public LibraryItem build() {
            LibraryItem item = null;
            switch (this.type) {
                case BOOK:
                    item = new Book(this.libraryId, this.itemId, this.title,description);
                    break;
                case DVD:
                    item =  new Dvd(this.libraryId, this.itemId, this.title,description);
                    break;
                case VHS:
                    item =  new Vhs(this.libraryId, this.itemId, this.title,description);
                    break;
                case CD:
                    item =  new CompactDisc(this.libraryId, this.itemId, this.title,description);
                    break;
            }
            return item;
        }
    }
}
