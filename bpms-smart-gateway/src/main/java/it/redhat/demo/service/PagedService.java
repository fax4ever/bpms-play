package it.redhat.demo.service;

import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by fabio.ercoli@redhat.com on 18/07/2017.
 */

@ApplicationScoped
public class PagedService {

    @Inject
    private Logger log;

    public List<Long> extractPage(Integer pageSize, Integer page, boolean asc, List<Long> allids) {
        Stream<Long> distinct = allids.stream().distinct();
        List<Long> ids = (asc) ?
                distinct.sorted().collect(Collectors.toList()) :
                distinct.sorted(Collections.reverseOrder()).collect(Collectors.toList());

        int total = ids.size();
        int offset = page * pageSize;
        int limit = (page + 1) * pageSize;

        if (offset >= total) {
            return new ArrayList<>();
        }

        List<Long> longs = ids.subList(offset, Math.min(limit, total));

        log.debug("chosen page: {}", ids);

        return longs;
    }

}
