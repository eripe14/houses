package com.eripe14.houses.configuration.implementation;

import com.eripe14.houses.configuration.ReloadableConfig;
import com.eripe14.houses.house.member.HouseMemberPermission;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemFlag;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class PluginConfiguration implements ReloadableConfig {

    public String serviceId = "eripe14";

    public String serviceToken = "MTcxNDc2NDgzNTI2NE5qRXlaREkyTm1FM1pEbGxZakpsT0RVME56QTFObUUzWVRjMFlUQXhNR0UyWW1OaU5HRTBNakEzWldZeU5qY3lZbUl3WlRRME1HSmpNRE16WmpSak9BPT0=.db146f601b40fdeaa7b5e1c3ff";

    public static String HOUSES_WORLD_NAME = "world";

    @Description({ " ", "# Z jaką częstotliwością, task od sprawdzania czy dany remont się skończył, ma się wykonywać"})
    public Duration renovationExpireTaskFrequency = Duration.ofMinutes(5);

    @Description({ " ", "# Z jaką częstotliwością, task od sprawdzania czy dany najem się skończył, ma się wykonywać"})
    public Duration rentExpireTaskFrequency = Duration.ofMinutes(5);

    @Description({ " ", "# Czas potrzebny do ustawienia domu w regionie (w sekundach)"})
    public Duration timeToSetHomeRegion = Duration.ofSeconds(10);

    @Description({ " ", "# Namespaced ID bloku z items addera, który pojawia się po stworzeniu domu (block odpowiedzialny za kupno/wynajem domu)"})
    public String itemsAdderPurchaseNamespacedId = "stefor:gym_machine_1";

    @Description({ " ", "# Czas na zaproszenia do domu (dodanie do domu / zmiana właściciela itp.) (w sekundach)"})
    public Duration timeToConfirmHouseInvite = Duration.ofSeconds(10);

    @Description({ " ", "# Namespaced ID bloku z items addera, dotyczący panelu domu"})
    public String itemsAdderHousePanelNamespacedId = "stefor:gym_machine_14";

    @Description({ " ", "# Domyślna konfiguracja permisji gracza"})
    public Map<HouseMemberPermission, Boolean> defaultHouseMemberPermission = Map.of(
            HouseMemberPermission.OPEN_CHESTS, false,
            HouseMemberPermission.OPEN_DOORS, true,
            HouseMemberPermission.PLACE_FURNITURE, false,
            HouseMemberPermission.RENOVATE, false
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

    @Description({ " ", "# Lista słów, które mogą przerwać składanie wniosku o renowację"})
    public List<String> renovationRequestCancelWords = List.of("cancel", "anuluj", "stop");

    @Description({ " ", "# Namespaced ID bloku z items addera, który jest odpowiedzialny za otwieranie GUI z listą wniosków o remont domów"})
    public String renovationApplicationsNamespacedId = "stefor:gym_machine_8";

    @Description({ " ", "# Permisja (permisja urzędnika), która pozwala na otwieranie GUI z listą wniosków o remont domów"})
    public String renovationApplicationsPermission = "rp.house.renovation.applications";

    @Description({ " ", "# Maksymalna ilość dni czasu renowacji"})
    public int maxRenovationDays = 3;

    @Description({ " ", "# Czas potrzebny na wpisanie osobno powodu i czasu (na wpisanie każdej z tych rzeczy czas będzie taki sam)"})
    public Duration timeToProvideRenovationRequest = Duration.ofSeconds(10);

    @Description({ " ", "# Na ile kratek do góry od maksymalnego punktu działki, gracz może kopać/stawiać bloki"})
    public int diggingHeight = 5;

    @Description({ " ", "# Cena pobierana za stworzenie wniosku o remont"})
    public int completeRenovationRequestPrice = 200;
    public int majorRenovationRequestPrice = 100;
    public int notInterferingRenovationRequestPrice = 50;

    @Description({ " ", "# Permisja (admina), która umożliwia budowanie oraz niszczenia każdego bloku w domu"})
    public String bypassPermission = "rp.house.bypass";

    @Description({ " ", "# Custom item odpowiedzialny, za otwieranie GUI z listą remontów do zaakceptowania"})
    public ItemConfiguration renovationAcceptanceItem = new ItemConfiguration(
            "renovation_acceptance",
            "&eAkceptacja remontów",
            List.of("&7Kliknij, aby otworzyć listę remontów do zaakceptowania"),
            List.of(ItemFlag.HIDE_ATTRIBUTES),
            Material.PAPER,
            true
    );

    @Description({ " ", "# Namespace ID custom furniture, który jest alarmem"})
    public String alarmNamespacedId = "stefor:gym_machine_15";

    @Description({ " ", "# Dźwięk odtwarzany, jeśli gracz pomyślnie wyśle zaproszenie do innego gracza"})
    public Sound inviteSentSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

    @Description({ " ", "# Namespace ID custom furniture, który jest odpowiedzialny za otwieranie gui z listą mieszkań do kupna"})
    public String apartmentsBuyerNamespacedId = "stefor:gym_machine_7";

    @Description({ " ", "# Namespace ID custom furniture, który jest odpowiedzialny za otwieranie gui z remontami"})
    public String renovationNamespacedId = "stefor:gym_machine_6";

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "config.yml");
    }

}