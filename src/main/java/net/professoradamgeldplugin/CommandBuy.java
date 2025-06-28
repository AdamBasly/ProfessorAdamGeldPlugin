package net.professoradamgeldplugin;

import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandBuy implements CommandExecutor {

    private final Map<Material, Integer> verkaufspreise;

    // Constructor accepting verkaufspreise
    public CommandBuy(Map<Material, Integer> verkaufspreise) {
        this.verkaufspreise = verkaufspreise;
    }

    public CommandBuy() {
        this.verkaufspreise = loadPricesFromYaml();
    }

    private Map<Material, Integer> loadPricesFromYaml() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("buy.yml")) {
            if (inputStream == null) {
                throw new IllegalStateException("buy.yml file not found!");
            }
            Map<String, Object> yamlData = yaml.load(inputStream);
            Map<String, Integer> prices = (Map<String, Integer>) yamlData.get("prices");

            // Convert String keys to Material
            return prices.entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> Material.matchMaterial(entry.getKey()),
                            Map.Entry::getValue
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load prices from buy.yml", e);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p) || args.length != 1) return false;

        Material mat = Material.matchMaterial(args[0].toUpperCase());
        if (mat == null) {
            p.sendMessage("§cUngültiger Blockname.");
            return true;
        }

        int basispreis = verkaufspreise.getOrDefault(mat, 1);
        int kaufpreis = (int) Math.ceil(basispreis * 2.0); // ✨ z. B. 200 % vom Verkaufswert

        if (!Main.hasEnoughCoins(p.getUniqueId(), kaufpreis)) {
            p.sendMessage("§cDu hast nicht genug Coins. Preis: " + kaufpreis);
            return true;
        }

        Main.ziehCoins(p.getUniqueId(), kaufpreis); 
        p.getInventory().addItem(new ItemStack(mat, 1));
        p.sendMessage("§aGekauft: 1x " + mat.name() + " für " + kaufpreis + " Coins.");
        return true;
    }
}