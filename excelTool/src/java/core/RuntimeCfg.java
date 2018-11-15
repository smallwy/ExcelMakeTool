package core;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


public class RuntimeCfg {
    public static RuntimeCfg instance = new RuntimeCfg();
    private String configPath = "";
    private static Class<? extends RuntimeCfg> subClz;

    public RuntimeCfg() {
    }

    public String getConfigPath() {
        return this.configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
}

