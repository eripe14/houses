package com.eripe14.houses.purchase;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public class PurchaseService {

    private final Economy economy;

    public PurchaseService(Economy economy) {
        this.economy = economy;
    }

    public boolean hasEnoughMoney(Player player, double amount) {
        return this.economy.has(player, amount);
    }

    public void withdrawMoney(Player player, double amount) {
        this.economy.withdrawPlayer(player, amount);
    }

    public void depositMoney(Player player, double amount) {
        this.economy.depositPlayer(player, amount);
    }

}