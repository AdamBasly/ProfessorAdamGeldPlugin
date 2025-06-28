package net.professoradamgeldplugin;

import net.milkbowl.vault.economy.*;
import org.bukkit.OfflinePlayer;

import java.util.List;

public class VaultEconomyAdapter implements Economy {

    @Override public boolean isEnabled() { return true; }
    @Override public String getName() { return "ProfessorEconomy"; }
    @Override public boolean hasBankSupport() { return false; }
    @Override public int fractionalDigits() { return 0; }
    @Override public String format(double amount) { return ((int) amount) + " Coins"; }
    @Override public String currencyNamePlural() { return "Coins"; }
    @Override public String currencyNameSingular() { return "Coin"; }

    @Override public boolean hasAccount(OfflinePlayer player) { return true; }
    @Override public boolean hasAccount(String name) { return false; }

    @Override public double getBalance(OfflinePlayer player) {
        return Main.getBalance(player.getUniqueId());
    }

    @Override public double getBalance(String name) { return 0; }

    @Override public boolean has(OfflinePlayer player, double amount) {
        return Main.has(player.getUniqueId(), (int) amount);
    }

    @Override public boolean has(String name, double amount) { return false; }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        boolean success = Main.withdraw(player.getUniqueId(), (int) amount);
        if (!success) {
            success = Main.withdrawCash(player.getUniqueId(), (int) amount); // Attempt to withdraw from cash
        }
        return new EconomyResponse(amount, getBalance(player),
                success ? EconomyResponse.ResponseType.SUCCESS : EconomyResponse.ResponseType.FAILURE,
                success ? "" : "Nicht genug Coins");
    }

    @Override public EconomyResponse withdrawPlayer(String name, double amount) { return notSupported(); }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String name, String worldName, double amount) {
        return notSupported();
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        Main.deposit(player.getUniqueId(), (int) amount);
        Main.depositCash(player.getUniqueId(), (int) amount); // Deposit into cash as well
        return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override public EconomyResponse depositPlayer(String name, double amount) { return notSupported(); }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String name, String worldName, double amount) {
        return notSupported();
    }

    @Override public boolean createPlayerAccount(OfflinePlayer player) { return true; }
    @Override public boolean createPlayerAccount(OfflinePlayer player, String worldName) { return true; }
    @Override public boolean createPlayerAccount(String name) { return false; }
    @Override public boolean createPlayerAccount(String name, String worldName) { return false; }

    @Override public EconomyResponse createBank(String name, String player) { return notSupported(); }
    @Override public EconomyResponse createBank(String name, OfflinePlayer player) { return notSupported(); }
    @Override public EconomyResponse deleteBank(String name) { return notSupported(); }
    @Override public EconomyResponse bankBalance(String name) { return notSupported(); }
    @Override public EconomyResponse bankHas(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse bankWithdraw(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse bankDeposit(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse isBankOwner(String name, String player) { return notSupported(); }
    @Override public EconomyResponse isBankOwner(String name, OfflinePlayer player) { return notSupported(); }
    @Override public EconomyResponse isBankMember(String name, String player) { return notSupported(); }
    @Override public EconomyResponse isBankMember(String name, OfflinePlayer player) { return notSupported(); }
    @Override public List<String> getBanks() { return List.of(); }

    private EconomyResponse notSupported() {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Nicht unterstützt");
    }
    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return false;
    }
    @Override
    public double getBalance(OfflinePlayer player, String worldName) {
        return getBalance(player);
    }
    @Override
    public double getBalance(String playerName, String worldName) {
        return 0;
    }
    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }
    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return false;
    }
}