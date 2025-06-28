package net.professoradamgeldplugin;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Bukkit;
import net.professoradamgeldplugin.api.EconomyProvider;
import net.professoradamgeldplugin.api.CoreEconomy;
import net.professoradamgeldplugin.HUDManager;
import net.professoradamgeldplugin.CommandOpEnchant;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {
    public static final Map<Material, Integer> spezialpreise = new HashMap<>();
    public static final Map<UUID, Integer> kontostand = new HashMap<>();
    public static final Map<Material, Integer> verkaufspreise = new HashMap<>();
    public static final Map<Location, ChestShop> shops = new HashMap<>();
    public static final List<Grundstück> gsList = new ArrayList<>();
    private static final Map<UUID, Integer> cashBalances = new HashMap<>();
    public static final Material CASH_ITEM = Material.PAPER;

    private static Main instance;
    public static Main getInstance() { return instance; }

    private Economy economy;
    public Economy getEconomy() { return economy; }
    public boolean isVaultEnabled() { return economy != null; }

    private static int goldPrice = 60; // Default gold price

    public static int getGoldPrice() {
        return goldPrice;
    }

    public static void setGoldPrice(int newPrice) {
        goldPrice = newPrice;
    }

    @Override
    public void onEnable() {
        instance = this;

        setupEconomy(); // Vault optional

        // Register the economy provider for other plugins
        CoreEconomy.setProvider(new net.professoradamgeldplugin.api.ProfessorEconomyProvider());
        getServer().getServicesManager().register(EconomyProvider.class, CoreEconomy.getProvider(), this, ServicePriority.Highest);

        getServer().getServicesManager().register(
            Economy.class,
            new VaultEconomyAdapter(),
            this,
            ServicePriority.Highest
        );

        HUDManager.startHUDUpdater(); // Start the HUD updater

        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        initPreise();
        ladeKontostand();
        ladeGrundstücke();
        ladeChestShops(); // Load chest shops

        if (getCommand("money") != null) getCommand("money").setExecutor(new CommandMoney());
        if (getCommand("pay") != null) getCommand("pay").setExecutor(new CommandPay());
        if (getCommand("buy") != null) getCommand("buy").setExecutor(new CommandBuy(verkaufspreise));
        if (getCommand("sell") != null) getCommand("sell").setExecutor(new CommandSell(verkaufspreise));
        if (getCommand("chestshop") != null) getCommand("chestshop").setExecutor(new CommandChestShop());
        if (getCommand("gs") != null) getCommand("gs").setExecutor(new CommandGrundstück());
        if (getCommand("gsdelete") != null) getCommand("gsdelete").setExecutor(new CommandGrundstückDelete());
        if (getCommand("gsinfo") != null) getCommand("gsinfo").setExecutor(new CommandGrundstückInfo());
        if (getCommand("gsshow") != null) getCommand("gsshow").setExecutor(new CommandGrundstückShow());
        if (getCommand("exchange") != null) getCommand("exchange").setExecutor(new CommandExchange()); // Register the new command
        if (getCommand("openchant") != null) getCommand("openchant").setExecutor(new CommandOpEnchant());
        if (getCommand("spawnplane") != null) getCommand("spawnplane").setExecutor(new CommandSpawnPlane());

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new JobListener(), this);
        getServer().getPluginManager().registerEvents(new ChestShopListener(), this);
        getServer().getPluginManager().registerEvents(new GrundstückListener(), this);
        getServer().getPluginManager().registerEvents(new AirplaneManager(), this);

        // Schedule gold value updates every 2 seconds
        getServer().getScheduler().runTaskTimer(this, () -> ChestShop.updateGoldValue(), 0L, 40L); // 40 ticks = 2 seconds

        getLogger().info("Vault-Unterstützung: " + (isVaultEnabled() ? "Aktiv ✔" : "Nicht verfügbar ❌"));
        getLogger().info("ProfessorAdamGeldPlugin aktiviert!");
    }

    @Override
    public void onDisable() {
        speichereKontostand();
        speichereGrundstücke();
        speichereChestShops(); // Save chest shops
        getLogger().info("ProfessorAdamGeldPlugin deaktiviert.");
    }
    private void speichereChestShops() {
        File file = new File(getDataFolder(), "chestshops.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (Location loc : shops.keySet()) {
            ChestShop shop = shops.get(loc);
            String key = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
            config.set(key + ".owner", shop.getOwner().toString());
            config.set(key + ".preis", shop.getPreis());
        }
        try {
            config.save(file);
            getLogger().info("ChestShops gespeichert: " + shops.size());
        } catch (IOException e) {
            getLogger().warning("Fehler beim Speichern der ChestShops: " + e.getMessage());
        }
    }

    private void ladeChestShops() {
        File file = new File(getDataFolder(), "chestshops.yml");
        if (!file.exists()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            try {
                String[] parts = key.split(",");
                World world = getServer().getWorld(parts[0]);
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                int z = Integer.parseInt(parts[3]);
                Location loc = new Location(world, x, y, z);

                UUID owner = UUID.fromString(config.getString(key + ".owner"));
                int preis = config.getInt(key + ".preis");
                Material item = Material.matchMaterial(config.getString(key + ".item"));
                if (item == null) {
                    getLogger().warning("Ungültiges Item in chestshops.yml: " + config.getString(key + ".item"));
                    continue;
                }

                shops.put(loc, new ChestShop(owner, loc, preis, item));
            } catch (Exception e) {
                getLogger().warning("Fehler beim Laden von ChestShop: " + key + " → " + e.getMessage());
            }
        }
        getLogger().info("ChestShops geladen: " + shops.size());
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    private void ladeKontostand() {
        File file = new File(getDataFolder(), "data.yml");
        if (!file.exists()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int coins = config.getInt(key);
                kontostand.put(uuid, coins);
            } catch (IllegalArgumentException e) {
                getLogger().warning("Ungültige UUID in data.yml: " + key);
            }
        }
    }

    private void speichereKontostand() {
        File file = new File(getDataFolder(), "data.yml");
        YamlConfiguration config = new YamlConfiguration();
        for (UUID uuid : kontostand.keySet()) {
            config.set(uuid.toString(), kontostand.get(uuid));
        }
        try {
            config.save(file);
            getLogger().info("Coins gespeichert: " + kontostand.size());
        } catch (IOException e) {
            getLogger().warning("Fehler beim Speichern: " + e.getMessage());
        }
    }

    public void speichereGrundstücke() {
        File file = new File(getDataFolder(), "gs.yml");
        YamlConfiguration config = new YamlConfiguration();
        int i = 0;
        for (Grundstück gs : gsList) {
            String key = "claim" + (i++);
            config.set(key + ".uuid", gs.owner.toString());
            config.set(key + ".x", gs.center.getBlockX());
            config.set(key + ".y", gs.center.getBlockY());
            config.set(key + ".z", gs.center.getBlockZ());
            config.set(key + ".radius", gs.radius);
        }
        try {
            config.save(file);
            getLogger().info("Grundstücke gespeichert: " + gsList.size());
        } catch (IOException e) {
            getLogger().warning("Fehler beim Speichern der Grundstücke: " + e.getMessage());
        }
    }

    public void ladeGrundstücke() {
        File file = new File(getDataFolder(), "gs.yml");
        if (!file.exists()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(config.getString(key + ".uuid"));
                int x = config.getInt(key + ".x");
                int y = config.getInt(key + ".y");
                int z = config.getInt(key + ".z");
                int radius = config.getInt(key + ".radius");
                Location center = new Location(getServer().getWorlds().get(0), x, y, z);
                gsList.add(new Grundstück(uuid, center, radius));
            } catch (Exception e) {
                getLogger().warning("Fehler beim Laden von Grundstück: " + key + " → " + e.getMessage());
            }
        }
        getLogger().info("Grundstücke geladen: " + gsList.size());
    }

    private void initPreise() {
        verkaufspreise.put(Material.STONE, 2);
        verkaufspreise.put(Material.COBBLESTONE, 1);
        verkaufspreise.put(Material.DIAMOND_ORE, 40);
        verkaufspreise.put(Material.DIAMOND_BLOCK, 200);
        verkaufspreise.put(Material.IRON_ORE, 6);
        verkaufspreise.put(Material.IRON_BLOCK, 30);
        verkaufspreise.put(Material.GOLD_ORE, 8);
        verkaufspreise.put(Material.GOLD_BLOCK, 50);
        verkaufspreise.put(Material.COAL_ORE, 3);
        verkaufspreise.put(Material.COAL_BLOCK, 8);
        verkaufspreise.put(Material.ANCIENT_DEBRIS, 100);
    }

    // === Eingebaute ProfessorEconomy-API ===

    public static int getBalance(UUID uuid) {
        return kontostand.getOrDefault(uuid, 0);
    }

    public static void deposit(UUID uuid, int betrag) {
        kontostand.put(uuid, getBalance(uuid) + betrag);
    }

    public static boolean withdraw(UUID uuid, int betrag) {
        int current = getBalance(uuid);
        if (current >= betrag) {
            kontostand.put(uuid, current - betrag);
            return true;
        }
        return false;
    }

    public static boolean has(UUID uuid, int betrag) {
        return getBalance(uuid) >= betrag;
    }

    public static void transfer(UUID from, UUID to, int amount) {
        if (!has(from, amount)) {
            throw new IllegalArgumentException("Sender has insufficient funds.");
        }
        withdraw(from, amount);
        deposit(to, amount);
    }

    public static int getTotalCoins() {
        return kontostand.values().stream().mapToInt(Integer::intValue).sum();
    }

    public static Map<UUID, Integer> getTopBalances(int limit) {
        return kontostand.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
            .limit(limit)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // === Cash Balance Management ===

    public static ItemStack createCashItem(int amount) {
        ItemStack cash = new ItemStack(CASH_ITEM, amount);
        ItemMeta meta = cash.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§aCash");
            meta.setLore(Collections.singletonList("§7Authentifiziert durch ProfessorAdamGeldPlugin"));
            cash.setItemMeta(meta);
        }
        return cash;
    }

    public static boolean isValidCashItem(ItemStack item) {
        if (item == null || item.getType() != CASH_ITEM) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getLore() != null && meta.getLore().contains("§7Authentifiziert durch ProfessorAdamGeldPlugin");
    }

    public static int getCashBalance(UUID uuid) {
        return kontostand.getOrDefault(uuid, 0);
    }

    public static void depositCash(UUID uuid, int amount) {
        kontostand.put(uuid, getCashBalance(uuid) + amount);
    }

    public static boolean withdrawCash(UUID uuid, int amount) {
        int current = getCashBalance(uuid);
        if (current >= amount) {
            kontostand.put(uuid, current - amount);
            return true;
        }
        return false;
    }

    // === Kompatibilitäts-Wrapper ===

    public static void gibCoins(UUID uuid, int betrag) {
        deposit(uuid, betrag);
    }

    public static boolean ziehCoins(UUID uuid, int betrag) {
        return withdraw(uuid, betrag);
    }

    public static boolean hasEnoughCoins(UUID uuid, int betrag) {
        return has(uuid, betrag);
    }

    // === Transaction Logging ===

    public static void logTransaction(UUID playerUUID, String action, int amount) {
        String logMessage = String.format("[Transaction] Player: %s, Action: %s, Amount: %d", playerUUID, action, amount);
        Bukkit.getLogger().info(logMessage);
    }
}