package io.github.xitadoo.sshop.util;

import io.github.xitadoo.sshop.models.History;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StringTranslator {

    //Value will never be none because i wont push them to database :D
    //Stored in the following format
    //SpawnerType,DateBought,amountBought@


    public static String historyTranslated(List<History> historyList) {
        return historyList.stream()
                .map(history ->
                        String.format("%s,%s,%d",
                                String.valueOf(history.getSpawnerType()),
                                history.getDateBought(),
                                history.getAmountBought()))
                .collect(Collectors.joining("@"));
    }

    public static List<History> retrievedHistory(String string) {
        String[] historyStrings = string.split("@");

        List<History> historyList = new ArrayList<>();
        for (String historyString : historyStrings) {
            String[] parts = historyString.split(",");
            if (parts.length == 3) {
                EntityType spawnerType = EntityType.valueOf(parts[0]);
                long dateBought = Long.parseLong(parts[1]);
                int amountBought = Integer.parseInt(parts[2]);
                historyList.add(new History(spawnerType, dateBought, amountBought));
            }
        }
        return historyList;
    }
}
