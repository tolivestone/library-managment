package org.citylibrary.service;

import org.citylibrary.model.actor.Person;
import org.citylibrary.model.item.LibraryItem;

import java.time.LocalDate;

public interface LendingService {
    boolean borrowItem(Person borrower, LibraryItem item, LocalDate issueDate, LocalDate dueDate);
    boolean returnItem(LibraryItem item);
}
