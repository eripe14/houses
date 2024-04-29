package com.eripe14.houses.configuration.implementation;

import com.eripe14.houses.configuration.ReloadableConfig;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

import java.io.File;
import java.util.List;

public class InventoryConfiguration implements ReloadableConfig {

    @Description( { " ", "# Gui, dotyczące wyboru zakupu lub wynajmu domu" })
    public SelectPurchaseOption selectPurchaseOption = new SelectPurchaseOption();

    @Description( { " ", "# Gui, dotyczące wynajmu domu" })
    public Rent rent = new Rent();

    @Description( { " ", "# Gui, dotyczące panelu wynajętego domu" })
    public RentedPanel rentedPanel = new RentedPanel();

    @Description( { " ", "# Gui, dotyczące panelu zakupionego domu" })
    public PurchasedPanel purchasedPanel = new PurchasedPanel();

    @Description( { " ", "# Gui, dotyczące przedłużania wynajmu" })
    public ExtendRent extendRent = new ExtendRent();

    @Description( { " ", "# Gui, dotyczące potwierdzania" })
    public Confirm confirm = new Confirm();

    @Description( { " ", "# Gui, dotyczące listy członków domu" })
    @Description( { " ", "# To jest inventory, gdzie będą wyświetlane główki graczy, akcja po interakcji z główką zależy od implementacji. Nazwa tego inventory będzie taka jak ustawisz w implementacji np. w inventory od usuwania graczy", " " })
    public ListOfHouseMembers listOfHouseMembers = new ListOfHouseMembers();

    @Description( { " ", "# Gui, dotyczące listy współwłaścicieli domu" })
    @Description( { " ", "# To jest inventory, gdzie będą wyświetlane główki graczy, akcja po interakcji z główką zależy od implementacji. Nazwa tego inventory będzie taka jak ustawisz w implementacji np. w inventory od usuwania współwłaścicieli", " " })
    public ListOfCoOwners listOfCoOwners = new ListOfCoOwners();

    @Description( { " ", "# Gui, dotyczące usuwania gracza z domu" })
    public RemovePlayer removePlayer = new RemovePlayer();

    @Description( { " ", "# Gui, dotyczące dodawania współwłaściciela" })
    public CoOwner coOwner = new CoOwner();

    @Description( { " ", "# Gui, dotyczące zmiany uprawnień gracza" })
    public ChangePermission changePermission = new ChangePermission();



    @Contextual
    public static class SelectPurchaseOption {
        public String title = "&eWybierz opcję...";

        public int rows = 1;

        public ItemConfiguration buyItem = new ItemConfiguration(
                3,
                "&aKup dom", List
                .of("&7Kliknij aby kupić dom za &e{BUY_PRICE}&a."),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.CAMPFIRE,
                true
        );

        public ItemConfiguration rentItem = new ItemConfiguration(
                5,
                "&aWynajmij dom",
                List.of("&7Kliknij aby wynająć dom"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.HOPPER,
                true
        );

        public ItemConfiguration closeInventoryItem = new ItemConfiguration(
                8,
                "&cZamknij",
                List.of("&7Kliknij aby zamknąć"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.BARRIER,
                false
        );

        public boolean fillEmptySlots = true;

        public ItemConfiguration filler = new ItemConfiguration(
                0, "&8", List.of(), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.BLACK_STAINED_GLASS_PANE, false
        );
    }

    @Contextual
    public static class Rent {
        public String title = "&eWynajmij dom";

        public int rows = 3;

        public ItemConfiguration rentItem = new ItemConfiguration(
                13, "&aWynajmij dom",
                List.of("&7Kliknij aby wynająć dom", "&7Wybrana ilość dni najmu wynosi &e{DAYS}", "&7Cena za wybraną ilość dni wynosi &e{PRICE}"),
                List.of(ItemFlag.HIDE_ATTRIBUTES), Material.SUNFLOWER, false
        );

        public ItemConfiguration addDayItem = new ItemConfiguration(
                15, "&aDodaj dzień", List.of("&7Kliknij aby dodać dzień do najmu"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.EMERALD_BLOCK, true
        );

        public ItemConfiguration removeDayItem = new ItemConfiguration(
                11, "&aUsuń dzień", List.of("&7Kliknij aby usunąć dzień z najmu"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.REDSTONE_BLOCK, true
        );

        public ItemConfiguration closeInventoryItem = new ItemConfiguration(
                26, "&cZamknij", List.of("&7Kliknij aby zamknąć"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.BARRIER, false
        );

        public boolean fillEmptySlots = true;

        public ItemConfiguration filler = new ItemConfiguration(
                0, "&8", List.of(), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.BLACK_STAINED_GLASS_PANE, false
        );
    }

    @Contextual
    public static class Confirm {
        public String title = "&ePotwierdź";

        public int rows = 1;

        public ItemConfiguration confirmItem = new ItemConfiguration(
                3, "&aPotwierdź", List.of("&7Kliknij aby potwierdzić"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.EMERALD_BLOCK, true
        );

        public ItemConfiguration cancelItem = new ItemConfiguration(
                5, "&cAnuluj", List.of("&7Kliknij aby anulować"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.REDSTONE_BLOCK, true
        );

        public boolean fillEmptySlots = true;

        public ItemConfiguration filler = new ItemConfiguration(
                0, "&8", List.of(), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.BLACK_STAINED_GLASS_PANE, false);
    }

    @Contextual
    public static class RentedPanel {
        public String title = "&ePanel domu";

        public int rows = 5;

        public ItemConfiguration addPlayerToHouse = new ItemConfiguration(
                10, "&aDodaj gracza", List.of("&7Kliknij aby dodać gracza do domu", "&7Będziesz miał {TIME}, na kliknięcie PPM na gracza"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.LIME_DYE, false
        );

        public ItemConfiguration removePlayerFromHouse = new ItemConfiguration(
                12, "&cUsuń gracza", List.of("&7Kliknij aby usunąć gracza z domu"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.RED_DYE, false
        );

        public ItemConfiguration addCoOwner = new ItemConfiguration(
                14, "&aDodaj współwłaściciela", List.of("&7Kliknij aby dodać współwłaściciela domu"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.GREEN_DYE, false
        );

        public ItemConfiguration removeCoOwner = new ItemConfiguration(
                16, "&cUsuń współwłaściciela", List.of("&7Kliknij aby usunąć współwłaściciela domu"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.PURPLE_DYE, false
        );

        public ItemConfiguration changePlayerPermissions = new ItemConfiguration(
                20, "&aZmień uprawnienia", List.of("&7Kliknij aby zmienić uprawnienia gracza"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.YELLOW_DYE, false
        );

        public ItemConfiguration extendRent = new ItemConfiguration(
                22, "&aPrzedłuż wynajem", List.of("&7Kliknij aby przedłużyć wynajem"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.ORANGE_DYE, false
        );

        public ItemConfiguration changeOwner = new ItemConfiguration(
                24, "&aZmień właściciela", List.of("&7Kliknij aby zmienić najemce domu"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.BLUE_DYE, false
        );

        public ItemConfiguration closeInventoryItem = new ItemConfiguration(
                40, "&cZamknij", List.of("&7Kliknij aby zamknąć"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.BARRIER, false
        );

        public ItemConfiguration filler = new ItemConfiguration(
                0, "&8", List.of(), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.BLACK_STAINED_GLASS_PANE, false);

        public boolean fillEmptySlots = true;
    }

    @Contextual
    public static class PurchasedPanel {
        public String title = "&ePanel domu kupionego";

        public int rows = 5;

        public ItemConfiguration addPlayerToHouse = new ItemConfiguration(
                10, "&aDodaj gracza", List.of("&7Kliknij aby dodać gracza do domu", "&7Będziesz miał {TIME}, na kliknięcie PPM na gracza"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.LIME_DYE, false
        );

        public ItemConfiguration removePlayerFromHouse = new ItemConfiguration(
                12, "&cUsuń gracza", List.of("&7Kliknij aby usunąć gracza z domu"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.RED_DYE, false
        );

        public ItemConfiguration addCoOwner = new ItemConfiguration(
                14, "&aDodaj współwłaściciela", List.of("&7Kliknij aby dodać współwłaściciela domu"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.GREEN_DYE, false
        );

        public ItemConfiguration removeCoOwner = new ItemConfiguration(
                16, "&cUsuń współwłaściciela", List.of("&7Kliknij aby usunąć współwłaściciela domu"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.PURPLE_DYE, false
        );

        public ItemConfiguration changePlayerPermissions = new ItemConfiguration(
                20, "&aZmień uprawnienia", List.of("&7Kliknij aby zmienić uprawnienia gracza"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.YELLOW_DYE, false
        );

        public ItemConfiguration sell = new ItemConfiguration(
                22, "&aSprzedaj", List.of("&7Kliknij aby sprzedać dom"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.ORANGE_DYE, false
        );

        public ItemConfiguration changeOwner = new ItemConfiguration(
                24, "&aZmień właściciela", List.of("&7Kliknij aby zmienić najemce domu"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.BLUE_DYE, false
        );

        public ItemConfiguration closeInventoryItem = new ItemConfiguration(
                40, "&cZamknij", List.of("&7Kliknij aby zamknąć"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.BARRIER, false
        );

        public ItemConfiguration filler = new ItemConfiguration(
                0, "&8", List.of(), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.BLACK_STAINED_GLASS_PANE, false);

        public boolean fillEmptySlots = true;
    }

    @Contextual
    public static class ListOfHouseMembers {
        public int rows = 6;

        public ItemConfiguration headItem = new ItemConfiguration(
                0, "&e{PLAYER}", List.of("&7Kliknij aby wykonać akcję"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.PLAYER_HEAD, false
        );

        public ItemConfiguration nextPageItem = new ItemConfiguration(
                53, "&eNastępna strona", List.of("&7Kliknij aby przejść na następną stronę"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.ARROW, false
        );

        public ItemConfiguration previousPageItem = new ItemConfiguration(
                45, "&ePoprzednia strona", List.of("&7Kliknij aby przejść na poprzednią stronę"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.ARROW, false
        );

        public ItemConfiguration closeInventoryItem = new ItemConfiguration(
                49, "&cZamknij", List.of("&7Kliknij aby zamknąć"), List.of(ItemFlag.HIDE_ATTRIBUTES), Material.BARRIER, false
        );
    }

    @Contextual
    public static class ListOfCoOwners {
        public int rows = 6;

        public ItemConfiguration headItem = new ItemConfiguration(
                0, "&e{PLAYER}",
                List.of("&7Kliknij aby wykonać akcję"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.PLAYER_HEAD,
                false
        );

        public ItemConfiguration nextPageItem = new ItemConfiguration(
                53,
                "&eNastępna strona",
                List.of("&7Kliknij aby przejść na następną stronę"),
                List.of(ItemFlag.HIDE_ATTRIBUTES), Material.ARROW,
                false
        );

        public ItemConfiguration previousPageItem = new ItemConfiguration(
                45,
                "&ePoprzednia strona",
                List.of("&7Kliknij aby przejść na poprzednią stronę"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.ARROW,
                false
        );

        public ItemConfiguration closeInventoryItem = new ItemConfiguration(
                49,
                "&cZamknij",
                List.of("&7Kliknij aby zamknąć"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.BARRIER,
                false
        );
    }

    @Contextual
    public static class RemovePlayer {
        public String title = "&eUsuń gracza";
    }

    @Contextual
    public static class CoOwner {
        public String addCoOwnerTitle = "&eDodaj współwłaściciela";

        public String removeCoOwnerTitle = "&eUsuń współwłaściciela";
    }

    @Contextual
    public static class ChangePermission {
        public String listOfPlayersTitle = "&eWybierz gracza...";

        public String title = "&eZmień uprawnienia";

        public int rows = 3;

        public ItemConfiguration changeOpenDoorPermissionItem = new ItemConfiguration(
                11, "&aZmień uprawnienia otwierania drzwi",
                List.of("&7Kliknij aby zmienić uprawnienia otwierania drzwi", "&7Status: &e{STATUS_OPEN_DOOR}"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.OAK_DOOR,
                false
        );

        public ItemConfiguration changeOpenChestPermissionItem = new ItemConfiguration(
                13, "&aZmień uprawnienia otwierania skrzyń",
                List.of("&7Kliknij aby zmienić uprawnienia otwierania skrzyń", "&7Status: &e{STATUS_OPEN_CHEST}"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.CHEST,
                false
        );

        public ItemConfiguration changePlaceFurniturePermissionItem = new ItemConfiguration(
                15, "&aZmień uprawnienia stawianie mebli",
                List.of("&7Kliknij aby zmienić uprawnienia stawiania mebli", "&7Status: &e{STATUS_PLACE_FURNITURE}"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.FURNACE,
                false
        );

        public ItemConfiguration closeInventoryItem = new ItemConfiguration(
                26, "&cZamknij",
                List.of("&7Kliknij aby zamknąć"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.BARRIER,
                false
        );

        public boolean fillEmptySlots = true;

        public ItemConfiguration filler = new ItemConfiguration(
                0, "&8",
                List.of(),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.BLACK_STAINED_GLASS_PANE,
                false
        );
    }

    @Contextual
    public static class ExtendRent {
        public String title = "&ePrzedłuż wynajem";

        public int rows = 3;

        public ItemConfiguration extendRentItem = new ItemConfiguration(
                13, "&aPrzedłuż wynajem",
                List.of(
                        "&7Kliknij aby przedłużyć wynajem",
                        "&7Aktualna ilość dni do zakończenia najmu wynosi &e{DAYS_LEFT}",
                        "&7Wybrana ilość dni przedłużenia najmu wynosi &e{DAYS}",
                        "&7Cena za wybraną ilość dni wynosi &e{PRICE}"
                ),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.SUNFLOWER,
                false
        );

        public ItemConfiguration addDayItem = new ItemConfiguration(
                15, "&aDodaj dzień",
                List.of("&7Kliknij aby dodać dzień do najmu"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.EMERALD_BLOCK,
                true
        );

        public ItemConfiguration removeDayItem = new ItemConfiguration(
                11, "&aUsuń dzień",
                List.of("&7Kliknij aby usunąć dzień z najmu"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.REDSTONE_BLOCK,
                true
        );

        public ItemConfiguration closeInventoryItem = new ItemConfiguration(
                26, "&cZamknij",
                List.of("&7Kliknij aby zamknąć"),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.BARRIER,
                false
        );

        public boolean fillEmptySlots = true;

        public ItemConfiguration filler = new ItemConfiguration(
                0, "&8",
                List.of(),
                List.of(ItemFlag.HIDE_ATTRIBUTES),
                Material.BLACK_STAINED_GLASS_PANE,
                false
        );
    }

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "inventories.yml");
    }

}