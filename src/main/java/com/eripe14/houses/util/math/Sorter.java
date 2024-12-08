package com.eripe14.houses.util.math;

import com.eripe14.houses.house.House;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Sorter {

    private Sorter() {
    }

    public static List<House> sortAlphanumeric(Collection<House> list) {
        List<House> sortedList = new ArrayList<>(list);
        sortedList.sort((o1, o2) -> o1.getHouseId().compareToIgnoreCase(o2.getHouseId()));
        return sortedList;
    }

}
