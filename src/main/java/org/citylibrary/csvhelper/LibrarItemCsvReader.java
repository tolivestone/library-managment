package org.citylibrary.csvhelper;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.citylibrary.enums.ItemType;
import org.citylibrary.model.item.LibraryItem;
import org.citylibrary.model.item.Loanable;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class LibrarItemCsvReader {

    private static LibrarItemCsvReader instance;

    public static LibrarItemCsvReader getInstance() {

        if(instance == null) {
            synchronized (LibrarItemCsvReader.class) {
                if(instance == null) {
                    instance = new LibrarItemCsvReader();
                }
            }
        }
        return instance;
    }

    public List<LibraryItem> getLibraryItemsFromCsv(){

        String csvFile = "C:\\Users\\Keya\\IdeaProjects\\library-managment\\library-managment\\src\\main\\resources\\library.csv";

        CSVReader csvReader = null;
        List<LibraryItem> libraryItems = new ArrayList<>();

        try{
            csvReader = new CSVReaderBuilder(new FileReader(csvFile))
                    .withSkipLines(1)
                    .build();

            List<String[]> records = csvReader.readAll();
            for(String[] record: records) {

                switch (record[2].toUpperCase()) {
                    case "BOOK":
                        LibraryItem book = new LibraryItem.LibraryItemBuilder()
                                .withLibraryId(Integer.parseInt(record[0]))
                                .withItemId(Integer.parseInt(record[1]))
                                .withTitle(record[3])
                                .withType(ItemType.BOOK)
                                .build();

                        libraryItems.add(book);
                        break;

                    case "DVD":
                        LibraryItem dvd = new LibraryItem.LibraryItemBuilder()
                                .withLibraryId(Integer.parseInt(record[0]))
                                .withItemId(Integer.parseInt(record[1]))
                                .withTitle(record[3])
                                .withType(ItemType.DVD)
                                .build();

                        libraryItems.add(dvd);
                        break;

                    case "VHS":
                        LibraryItem vhs = new LibraryItem.LibraryItemBuilder()
                                .withLibraryId(Integer.parseInt(record[0]))
                                .withItemId(Integer.parseInt(record[1]))
                                .withTitle(record[3])
                                .withType(ItemType.VHS)
                                .build();

                        libraryItems.add(vhs);
                        break;
                }
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
        return libraryItems;
    }



}
