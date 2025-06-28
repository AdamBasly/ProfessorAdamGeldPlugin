package net.professoradamgeldplugin.api;

public class CoreEconomy {
    private static EconomyProvider provider;

    public static void setProvider(EconomyProvider p) {
        provider = p;
    }

    public static EconomyProvider getProvider() {
        if (provider == null) throw new IllegalStateException("Kein EconomyProvider registriert.");
        return provider;
    }
}