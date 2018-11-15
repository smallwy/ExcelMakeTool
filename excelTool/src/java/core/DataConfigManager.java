package core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.xstream.XStream;
import org.reflections.Reflections;

import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataConfigManager {
    public static DataConfigManager instance = new DataConfigManager();
    private Map<Class<?>, XmlConfigTable> xmlConfigMap = new HashMap();
    private Map<String, Class<?>> nameClassMap = new HashMap();
    private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private String packageName = null;

    public DataConfigManager() {
    }

    public void init(String packageName) {
        this.packageName = packageName;
        this.loadXmlConfigs();
    }

    public void reloadFile(String fileName) {
        if (!this.nameClassMap.containsKey(fileName)) {

        } else {
            String path = ResourceMonitor.getConfigPath();
            Class<?> clz = (Class)this.nameClassMap.get(fileName);
            if (null != this.packageName || this.xmlConfigMap.isEmpty()) {
                readWriteLock.writeLock().lock();

                try {
                    this.xmlConfigMap.put(clz, new XmlConfigTable(this.loadSingleConfigContent(path, clz)));
                } finally {
                    readWriteLock.writeLock().unlock();
                }
            }

        }
    }

    private void loadXmlConfigs() {
        String path = ResourceMonitor.getConfigPath();
        Reflections reflections = new Reflections(this.packageName, new Scanner[0]);
        Set<Class<? extends DataConfigItem>> classes = reflections.getSubTypesOf(DataConfigItem.class);
        Map<Class<?>, List<DataConfigItem>> content = this.loadAllConfigContent(path, classes);
        Map<Class<?>, XmlConfigTable> configMap = Maps.newHashMap();
        Iterator var6 = content.keySet().iterator();

        while(var6.hasNext()) {
            Class<?> clz = (Class)var6.next();
            configMap.put(clz, new XmlConfigTable((List)content.get(clz)));
            this.nameClassMap.put(this.getFileName(clz), clz);
        }

        readWriteLock.writeLock().lock();

        try {
            this.xmlConfigMap.clear();
            this.xmlConfigMap.putAll(configMap);
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    private Map<Class<?>, List<DataConfigItem>> loadAllConfigContent(String path, Set<Class<? extends DataConfigItem>> classes) {
        Map<Class<?>, List<DataConfigItem>> map = Maps.newHashMap();
        Iterator var4 = classes.iterator();

        while(var4.hasNext()) {
            Class<?> clz = (Class)var4.next();
            map.put(clz, this.loadSingleConfigContent(path, clz));
        }

        return map;
    }

    private String getFileName(Class<?> clz) {
        String fileName = clz.getSimpleName();
        fileName = fileName.substring(0, 1).toLowerCase() + fileName.substring(1);
        fileName = fileName.substring(0, fileName.length() - 6);
        return fileName;
    }

    private List<DataConfigItem> loadSingleConfigContent(String path, Class<?> clz) {
        String fileName = this.getFileName(clz);
        Object content = Lists.newArrayList();

        try {
            String filePath = path + fileName + ".xml";
            FileInputStream is = new FileInputStream(filePath);
            XStream xs = new XStream();
            xs.alias(fileName, clz);
            content = (List)xs.fromXML(is);

        } catch (Exception var8) {

        }

        return (List)content;
    }

    public <T> T getConfigByKey(Class<T> clz, String id) {
        XmlConfigTable configTable = this.getXmlConfigTable(clz);
        List<DataConfigItem> dataConfigItems = configTable.getConfigListByKey(id);
        if (null == dataConfigItems) {
            return null;
        } else {
            return clz.cast(dataConfigItems.get(0));
        }
    }

    public <T> T getConfigByKey(Class<T> clz, int id) {
        return this.getConfigByKey(clz, String.valueOf(id));
    }

    public List<? extends DataConfigItem> getConfigListByKey(Class<? extends DataConfigItem> clz, String id) {
        if (clz == DataConfigItem.class) {
            return null;
        } else {
            XmlConfigTable configTable = this.getXmlConfigTable(clz);
            List<DataConfigItem> dataConfigItems = configTable.getConfigListByKey(id);
            if (null == dataConfigItems) {
                return null;
            } else {
                return dataConfigItems;
            }
        }
    }

    public List<? extends DataConfigItem> getConfigListByKey(Class<? extends DataConfigItem> clz, int id) {
        return this.getConfigListByKey(clz, String.valueOf(id));
    }

    public List<String> getConfigKeys(Class<? extends DataConfigItem> clz) {
        XmlConfigTable configTable = this.getXmlConfigTable(clz);
        return configTable.getIdList();
    }

    public List<? extends DataConfigItem> getConfigList(Class<? extends DataConfigItem> clz) {
        if (clz == DataConfigItem.class) {
         return null;
        } else {
            XmlConfigTable configTable = this.getXmlConfigTable(clz);
            return configTable.getConfigList();
        }
    }

    public <T> boolean isExistId(Class<T> clz, String id) {
        XmlConfigTable configTable = this.getXmlConfigTable(clz);
        return configTable.isExist(id);
    }

    public <T> boolean isExistId(Class<T> clz, int id) {
        return this.isExistId(clz, id + "");
    }

    private XmlConfigTable getXmlConfigTable(Class<?> clz) {
        if (null == this.packageName || this.xmlConfigMap.isEmpty() || !this.xmlConfigMap.containsKey(clz)) {
            String path = ResourceMonitor.getConfigPath();
            readWriteLock.writeLock().lock();

            try {
                this.xmlConfigMap.put(clz, new XmlConfigTable(this.loadSingleConfigContent(path, clz)));
                this.nameClassMap.put(this.getFileName(clz), clz);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }

        readWriteLock.readLock().lock();

        XmlConfigTable table;
        try {
            table = (XmlConfigTable)this.xmlConfigMap.get(clz);
        } finally {
            readWriteLock.readLock().unlock();
        }

        return table;
    }
}

