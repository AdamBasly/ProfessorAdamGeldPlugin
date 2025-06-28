package net.professoradamgeldplugin.api;

import java.util.Map;
import java.util.UUID;

public interface EconomyProvider {
    int getBalance(UUID uuid);
    void deposit(UUID uuid, int amount);
    boolean withdraw(UUID uuid, int amount);
    boolean has(UUID uuid, int amount);
    int getTotalCoins();
    Map<UUID, Integer> getTopBalances(int limit);
}