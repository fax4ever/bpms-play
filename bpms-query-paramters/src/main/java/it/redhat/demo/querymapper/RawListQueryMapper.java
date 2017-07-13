package it.redhat.demo.querymapper;

import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.jbpm.kie.services.impl.query.mapper.AbstractQueryMapper;
import org.jbpm.services.api.query.QueryResultMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 13/07/2017.
 */
public class RawListQueryMapper extends AbstractQueryMapper<List<Object>> implements QueryResultMapper<List<List<Object>>> {

    private static final long serialVersionUID = 5935133069234696714L;

    /**
     * Dedicated for ServiceLoader to create instance, use <code>get()</code> method instead
     */
    public RawListQueryMapper() {
    }

    /**
     * Default access to get instance of the mapper
     * @return
     */
    public static RawListQueryMapper get() {
        return new RawListQueryMapper();
    }

    @Override
    public List<List<Object>> map(Object result) {
        if (result instanceof DataSet) {
            DataSet dataSetResult = (DataSet) result;
            List<List<Object>> mappedResult = new ArrayList<List<Object>>();

            if (dataSetResult != null) {

                for (int i = 0; i < dataSetResult.getRowCount(); i++) {
                    List<Object> row = buildInstance(dataSetResult, i);
                    mappedResult.add(row);

                }
            }

            return mappedResult;
        }

        throw new IllegalArgumentException("Unsupported result for mapping " + result);
    }

    protected List<Object> buildInstance(DataSet dataSetResult, int index) {
        List<Object> row = new ArrayList<Object>();

        for (DataColumn column : dataSetResult.getColumns()) {
            row.add(dataSetResult.getColumnById(column.getId()).getValues().get(index));
        }

        return row;
    }

    @Override
    public String getName() {
        return "RawList7";
    }

    @Override
    public Class<?> getType() {
        return List.class;
    }

    @Override
    public QueryResultMapper<List<List<Object>>> forColumnMapping(Map<String, String> columnMapping) {
        return new RawListQueryMapper();
    }

}

