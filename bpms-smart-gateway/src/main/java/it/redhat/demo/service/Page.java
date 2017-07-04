package it.redhat.demo.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabio.ercoli@redhat.com on 04/07/2017.
 */
public class Page<T> {

    private int total;
    private List<T> items;

    public Page(int total) {
        this.total = total;
        this.items = new ArrayList<>();
    }

    public Page(int total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public int getTotal() {
        return total;
    }

    public List<T> getItems() {
        return items;
    }

}
