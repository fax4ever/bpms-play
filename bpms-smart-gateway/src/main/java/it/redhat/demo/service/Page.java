package it.redhat.demo.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabio.ercoli@redhat.com on 04/07/2017.
 */
public class Page<T> {

    private int total;
    private int page;
    private int pageSize;
    private List<T> items = new ArrayList<>();

    public Page(int total, int page, int pageSize) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    public Page(int total, int page, int pageSize, List<T> items) {
        this(total, page, pageSize);
        this.items = items;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotal() {
        return total;
    }

    public List<T> getItems() {
        return items;
    }

}
