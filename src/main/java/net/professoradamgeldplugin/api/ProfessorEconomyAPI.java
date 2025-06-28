package net.professoradamgeldplugin.api;

import java.util.UUID;
import java.util.Map;

public class ProfessorEconomyAPI {

    public static int getBalance(UUID uuid) {
        return CoreEconomy.getProvider().getBalance(uuid);
    }

    public static void deposit(UUID uuid, int amount) {
        CoreEconomy.getProvider().deposit(uuid, amount);
    }

    public static boolean withdraw(UUID uuid, int amount) {
        return CoreEconomy.getProvider().withdraw(uuid, amount);
    }

    public static boolean has(UUID uuid, int amount) {
        return CoreEconomy.getProvider().has(uuid, amount);
    }

    public static void transfer(UUID from, UUID to, int amount) {
        if (!has(from, amount)) {
            throw new IllegalArgumentException("Sender has insufficient funds.");
        }
        withdraw(from, amount);
        deposit(to, amount);
    }

    public static int getTotalCoins() {
        return CoreEconomy.getProvider().getTotalCoins();
    }

    public static Map<UUID, Integer> getTopBalances(int limit) {
        return CoreEconomy.getProvider().getTopBalances(limit);
    }

    public static boolean setBalance(UUID uuid, int amount) {
        int currentBalance = getBalance(uuid);
        if (amount < 0) {
            return false;
        }
        if (amount > currentBalance) {
            deposit(uuid, amount - currentBalance);
        } else {
            withdraw(uuid, currentBalance - amount);
        }
        return true;
    }
}