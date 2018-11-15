package core;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


public class ResourceMonitor implements ApplicationContextAware {
    private String[] resourceFileSuffix = CoreConfig.stringValue("ResourceSuffix").split(",");
    private String resourcePath;
    private Map<String, ResourceLoaderWrapper> loaders = new HashMap();

    public ResourceMonitor() {
    }

    private void init(File rootDirectory) {
        File[] files = rootDirectory.listFiles();
        File[] var3 = files;
        int var4 = files.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            File file = var3[var5];
            if (file.isDirectory()) {
                this.init(file);
            }

            String name = file.getName();
            String[] var8 = this.resourceFileSuffix;
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                String suffix = var8[var10];
                if (name.endsWith(suffix)) {
                    String resource = name.substring(0, name.lastIndexOf("."));
                    ResourceMonitor.ResourceLoaderWrapper wrapper = (ResourceMonitor.ResourceLoaderWrapper)this.loaders.get(resource);
                    if (wrapper != null && wrapper.loader != null) {
                        wrapper.file = file;
                    }
                    break;
                }
            }
        }

    }

    private void prepare() {
        List<ResourceLoaderWrapper> list = new ArrayList(this.loaders.values());
        Collections.sort(list, new Comparator<ResourceLoaderWrapper>() {
            public int compare(ResourceMonitor.ResourceLoaderWrapper o1, ResourceMonitor.ResourceLoaderWrapper o2) {
                return o1.priority - o2.priority;
            }
        });

        ResourceMonitor.ResourceLoaderWrapper wrapper;
        for(Iterator var2 = list.iterator(); var2.hasNext();) {
            wrapper = (ResourceMonitor.ResourceLoaderWrapper)var2.next();
            FileInputStream is = null;

            try {
                if (wrapper.file == null) {
                    throw new RuntimeException(wrapper.resourceName + "对应的资源文件没有找到.");
                }

                is = new FileInputStream(wrapper.file);
                wrapper.loader.load(is);
            } catch (Exception var14) {
                var14.printStackTrace();
                System.exit(0);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException var13) {
                        ;
                    }
                }

            }
        }

    }

    public void monitoring() {
        if (this.resourcePath == null) {

        } else {
            FileAlterationObserver observer = new FileAlterationObserver(this.resourcePath, FileFilterUtils.and(new IOFileFilter[]{FileFilterUtils.fileFileFilter(), new SuffixFileFilter(this.resourceFileSuffix)}), (IOCase)null);
            observer.addListener(new ResourceMonitor.FileListener());
            FileAlterationMonitor monitor = new FileAlterationMonitor(CoreConfig.longValue("ResourcesMonitorInterval"), new FileAlterationObserver[]{observer});

            try {
                monitor.start();
            } catch (Exception var4) {
                var4.printStackTrace();
            }

        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ResourceLoader> loaderMap = applicationContext.getBeansOfType(ResourceLoader.class);
        Iterator var3 = loaderMap.values().iterator();

        while(var3.hasNext()) {
            ResourceLoader loader = (ResourceLoader)var3.next();
            ResourceMonitor.ResourceLoaderWrapper wrapper = new ResourceMonitor.ResourceLoaderWrapper();
            wrapper.loader = loader;
            wrapper.resourceName = loader.getResourceName();
            wrapper.priority = loader.getPriority();
            this.loaders.put(loader.getResourceName(), wrapper);
        }

        String path = getConfigPath();
        File rootFile = new File(path);
        if (rootFile.exists()) {
            if (rootFile.isDirectory()) {
                this.resourcePath = rootFile.getPath();
                this.init(rootFile);
                this.prepare();
            } else {
                this.resourcePath = null;

            }
        } else {
        }

    }

    public static String getConfigPath() {
        String path = null;
        if (!StringUtils.isEmpty(RuntimeCfg.instance.getConfigPath())) {
            path = RuntimeCfg.instance.getConfigPath();
        } else {
            path = System.getProperty("user.dir") + "/conf/resource";
        }

        return path + "/";
    }

    static class ResourceLoaderWrapper {
        ResourceLoader loader;
        File file;
        String resourceName;
        int priority;

        ResourceLoaderWrapper() {
        }

        void load() {
            if (this.loader == null) {
            } else {
                FileInputStream is = null;

                try {
                    is = new FileInputStream(this.file);
                    this.loader.load(is);
                } catch (Exception var11) {
                    var11.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException var10) {
                            ;
                        }
                    }

                }

            }
        }

        public String toString() {
            return "ResourceLoaderWrapper [loader=" + this.loader + ", file=" + this.file + ", resourceName=" + this.resourceName + ", priority=" + this.priority + "]";
        }
    }

    class FileListener extends FileAlterationListenerAdaptor {
        FileListener() {
        }

        private void fireLoad(File file) {
            String name = file.getName();
            String resourceName = name;
            if (name.indexOf(".") > 0) {
                resourceName = name.substring(0, name.lastIndexOf("."));
            }

            if (".xml".equals(name.substring(name.lastIndexOf(".")))) {
                DataConfigManager.instance.reloadFile(resourceName);
            } else {
                ResourceMonitor.ResourceLoaderWrapper wrapper = (ResourceMonitor.ResourceLoaderWrapper)ResourceMonitor.this.loaders.get(resourceName);
                if (wrapper != null) {
                    wrapper.load();
                    if (wrapper.loader.afterLoad() != null) {
                        ResourceMonitor.ResourceLoaderWrapper after = (ResourceMonitor.ResourceLoaderWrapper)ResourceMonitor.this.loaders.get(wrapper.loader.afterLoad());
                        if (after != null) {
                            after.load();
                        }
                    }
                }
            }

        }

        public void onFileCreate(File file) {
            this.fireLoad(file);
        }

        public void onFileChange(File file) {
            this.fireLoad(file);
        }

        public void onFileDelete(File file) {
        }
    }
}
