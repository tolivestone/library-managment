package org.citylibrary.model.item;

import org.citylibrary.enums.ItemType;
import org.citylibrary.enums.Status;

//TODO: check getters and setters
public abstract class LibraryItem implements Loanable {

    private final int libraryId;
    private final int itemId;
    private final ItemType type;
    private final String title;
    private Status itemStatus;

    public LibraryItem(final int libraryId, final int itemId,final  ItemType type,final String title) {
        this.libraryId = libraryId;
        this.itemId = itemId;
        this.type = type;
        this.title = title;
        this.itemStatus = Status.AVAILABLE;
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

    public void setItemStatus(final Status itemStatus) {
        this.itemStatus = itemStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LibraryItem that = (LibraryItem) o;

        if (libraryId != that.libraryId) return false;
        if (itemId != that.itemId) return false;
        if (type != that.type) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return itemStatus == that.itemStatus;
    }

    @Override
    public int hashCode() {
        int result = libraryId;
        result = 31 * result + itemId;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (itemStatus != null ? itemStatus.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "[" +
                "libraryId=" + libraryId +
                ", itemId=" + itemId +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", itemStatus=" + itemStatus +
                '}';
    }

    //Builder for library items, This will abstract item creation and sure no item is created with invalid data
    public static class LibraryItemBuilder {

        private int libraryId;
        private int itemId;
        private ItemType type;
        private String title;

        public LibraryItemBuilder withLibraryId(final int libraryId) {
            this.libraryId = libraryId;
            return this;
        }

        public LibraryItemBuilder withItemId(final int ItemId) {
            this.itemId = ItemId;
            return this;
        }

        public LibraryItemBuilder withType(final ItemType type) {
            this.type = type;
            return this;
        }

        public LibraryItemBuilder withTitle(final String title) {
            this.title = title;
            return this;
        }

        public LibraryItem build() {
            if(libraryId <=0 || itemId <=0 || type == null || title == null || title.isEmpty())
                throw new IllegalArgumentException("One or more argurment are not valid");

            LibraryItem item = null;
            switch (this.type) {
                case BOOK:
                    item = new Book(this.libraryId, this.itemId, this.title);
                    break;
                case DVD:
                    item =  new Dvd(this.libraryId, this.itemId, this.title);
                    break;
                case VHS:
                    item =  new Vhs(this.libraryId, this.itemId, this.title);
                    break;
            }
            return item;
        }
    }
}
