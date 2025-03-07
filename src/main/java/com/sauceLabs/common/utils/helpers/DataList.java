package com.sauceLabs.common.utils.helpers;

import java.util.ArrayList;
import java.util.List;

public class DataList {
    private List<String> list = new ArrayList<>();

    public void addItem(String value) {
        list.add(value);
    }

    public void removeItem(String value) {
        list.remove(value);
    }

    public String getItem(int valueIndx) {
        return list.get(valueIndx);
    }

    public List<String> getList() {
        return list;
    }
}
