package core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XmlConfigTable {
    private ImmutableMap<String, List<DataConfigItem>> dataTable = null;
    private final List<DataConfigItem> dataList = null;

    public XmlConfigTable(List<DataConfigItem> dataList) {
        ImmutableMap.Builder<String, List<DataConfigItem>> builder = ImmutableMap.builder();
        Map<String, List<DataConfigItem>> dataMap = Maps.newHashMap();
        Iterator var4 = dataList.iterator();

        while(var4.hasNext()) {
            DataConfigItem dataItem = (DataConfigItem)var4.next();
            String key = this.getIdxValue(dataItem);
            ((List)dataMap.computeIfAbsent(key, (k) -> {
                return Lists.newArrayList();
            })).add(dataItem);
        }

        this.dataTable = builder.putAll(dataMap).build();
        dataList = Collections.unmodifiableList(dataList);
    }

    private XmlConfigTable() {
    }

    public DataConfigItem getDataConfigItemByKey(String key) {
        return (DataConfigItem)((List)this.dataTable.get(key)).get(0);
    }

    public List<DataConfigItem> getConfigListByKey(String key) {
        return (List)this.dataTable.get(key);
    }

    public List<String> getIdList() {
        return this.dataTable.keySet().asList();
    }

    public boolean isExist(String id) {
        return this.dataTable.containsKey(id);
    }

    public List<DataConfigItem> getConfigList() {
        return this.dataList;
    }

    private String getIdxValue(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();

        for(int j = 0; j < fields.length; ++j) {
            Field field = fields[j];
            DataConfigId ano = (DataConfigId)field.getAnnotation(DataConfigId.class);
            if (null != ano) {
                try {
                    if (field.getType().getName().equals("int")) {
                        return field.get(obj).toString();
                    }

                    return (String)field.get(obj);
                } catch (IllegalAccessException var7) {
                    var7.printStackTrace();
                }
            }
        }
        return "tag";
    }
}
