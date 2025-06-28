package net.professoradamgeldplugin.api;

import java.util.UUID;
import java.util.Map;
import net.professoradamgeldplugin.Main;

public class ProfessorEconomyProvider implements EconomyProvider {

    @Override
    public int getBalance(UUID uuid) {
        return net.professoradamgeldplugin.Main.getBalance(uuid);
    }

    @Override
    public void deposit(UUID uuid, int amount) {
        net.professoradamgeldplugin.Main.deposit(uuid, amount);
    }

    @Override
    public boolean withdraw(UUID uuid, int amount) {
        return net.professoradamgeldplugin.Main.withdraw(uuid, amount);
    }

    @Override
    public boolean has(UUID uuid, int amount) {
        return net.professoradamgeldplugin.Main.has(uuid, amount);
    }

    @Override
    public int getTotalCoins() {
        return Main.getTotalCoins();
    }

    @Override
    public Map<UUID, Integer> getTopBalances(int limit) {
        return Main.getTopBalances(limit);
    }
}