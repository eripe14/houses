package com.eripe14.houses.configuration.implementation;

import com.eripe14.houses.configuration.ReloadableConfig;
import com.eripe14.houses.house.member.HouseMemberPermission;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;
import org.bukkit.Material;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class PluginConfiguration implements ReloadableConfig {

    @Description({ " ", "# Czas potrzebny do ustawienia domu w regionie (w sekundach)"})
    public Duration timeToSetHomeRegion = Duration.ofSeconds(10);

    @Description({ " ", "# Namespaced ID bloku z items addera, który pojawia się po stworzeniu domu (block odpowiedzialny za kupno/wynajem domu)"})
    public String itemsAdderPurchaseNamespacedId = "stefor:gym_machine_1";

    @Description({ " ", "# Czas na potwierdzenie zaproszenia do domu (w sekundach)"})
    public Duration timeToConfirmHouseInvite = Duration.ofSeconds(10);

    @Description({ " ", "# Namespaced ID bloku z items addera, dotyczący panelu domu"})
    public String itemsAdderHousePanelNamespacedId = "stefor:gym_machine_14";

    @Description({ " ", "# Domyślna konfiguracja permisji gracza"})
    public Map<HouseMemberPermission, Boolean> defaultHouseMemberPermission = Map.of(
            HouseMemberPermission.OPEN_CHESTS, false,
            HouseMemberPermission.OPEN_DOORS, true,
            HouseMemberPermission.PLACE_FURNITURE, false
    );

    @Description({ " ", "# Przed jakim czasem od końca okresu najmu, ma wysłać graczowi wezwanie do zapłaty"})
    public Duration timeBeforeRentEndToReminder = Duration.ofDays(7);

    @Description({ " ", "# Minimalna liczba dni najmu domu"})
    public int minRentDays = 3;

    @Description({ " ", "# Po ilu sekundach po wejściu na serwer, gracz ma dostać powiadomienia"})
    public Duration alertDelay = Duration.ofSeconds(5);

    @Description({ " ", "# Lista blokowanych drzwi dla gracza bez permisji"})
    public List<Material> doors = List.of(
            Material.ACACIA_DOOR,
            Material.BIRCH_DOOR,
            Material.CRIMSON_DOOR,
            Material.DARK_OAK_DOOR,
            Material.IRON_DOOR,
            Material.JUNGLE_DOOR,
            Material.OAK_DOOR,
            Material.SPRUCE_DOOR,
            Material.WARPED_DOOR,
            Material.MANGROVE_DOOR
    );

    @Description({ " ", "# Lista blokowanych skrzynek dla gracza bez permisji"})
    public List<Material> chests = List.of(
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.ENDER_CHEST,
            Material.BARREL,
            Material.SHULKER_BOX,
            Material.BLACK_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX,
            Material.GRAY_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX,
            Material.LIME_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX,
            Material.PINK_SHULKER_BOX,
            Material.PURPLE_SHULKER_BOX,
            Material.RED_SHULKER_BOX,
            Material.WHITE_SHULKER_BOX,
            Material.YELLOW_SHULKER_BOX
    );

    @Description({ " ", "# Procent kwoty kupna, która zostanie zwrócona po sprzedaży domu"})
    public int percentOfPurchasePriceReturned = 70;

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "config.yml");
    }

}