package com.eripe14.houses.configuration.implementation;

import com.eripe14.houses.configuration.ReloadableConfig;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;

import java.io.File;
import java.util.List;

public class MessageConfiguration implements ReloadableConfig {

    @Description({ " ", "# Wrong command usage" })
    public WrongUsage wrongUsage = new WrongUsage();

    @Description({ " ", "# Sekcja domów" })
    public House house = new House();

    @Description({ " ", "# Sekcja wynajmu" })
    public Rent rent = new Rent();

    @Description({ " ", "# Sekcja alertów" })
    public Alert alert = new Alert();

    @Contextual
    public static class WrongUsage {
        public String invalidUsage = "&4Wrong command usage &8>> &7{COMMAND}.";

        public String invalidUsageHeader = "&cWrong command usage!";

        public String invalidUsageEntry = "&8 >> &7";

        public String noPermission = "&4You don't have permission to perform this command.";

        public String cantFindPlayer = "&4Can not find that player!";

        public String onlyForPlayer = "&4Command only for players!";
    }

    @Contextual
    public static class House {
        public String cantCreateRegions = "&cNie udało się stworzyć regionów!";

        public String createdBothRegions = "&aStworzono regiony!";

        public String createdHouse = "&aStworzono dom o id &e{HOUSE_ID}&a!";

        public String houseAlreadyExists = "&cDom o takim id &4({HOUSE_ID})&c już istnieje!";

        public String boughtHouse = "&aZakupiono dom o id &e{HOUSE_ID}, &aza &e{BUY_PRICE}zł&a!";

        public String notEnoughMoneyToBuy = "&cNie masz wystarczająco pieniędzy na zakup domu!";

        public String requiredRentalTime = "&cCzas wynajmu nie może być mniejszy niż {MIN_RENT_TIME} dni!";

        public String requiredExtendRentTime = "&cMinimalne przedłużenie wynajmu to 1 dzień!";

        public String extendRentTime = "&aPrzedłużono wynajem domu o &e{DAYS}&a dni!";

        public String rentedHouse = "&aWynajęto dom o id &e{HOUSE_ID} &aza &e{PRICE}&a, na okres &e{RENT_TIME}&a dni!";

        public String notEnoughMoneyToRent = "&cNie masz wystarczająco pieniędzy na wynajem!";

        public String createdInvite = "&aKliknij na gracza, by dodać go do domu.";

        public String inviteExpired = "&cZaproszenie wygasło!";

        public String playerAddedToHouse = "&aGracz &e{PLAYER}&a potwierdził twoje zaproszenie - został on dodany do domu!";

        public String joinedHouse = "&aDołączono do domu gracza &e{INVITER}&a!";

        public String playerCancelledInvitation = "&cGracz &4{PLAYER}&c, odrzucił twoje zaproszenie do domu!";

        public String cancelledInvitation = "&cOdrzucono zaproszenie do domu od gracza &4{INVITER}&c!";

        public String createdOwnerInvite = "&aKliknij na gracza, by mianować go właścicielem domu.";

        public String playerMustMemberToBecomeOwner = "&cGracz musi być członkiem domu, żeby zostać właścicielem!";

        public String changedOwner = "&aGracz &e{PLAYER}&a potwierdził twoje zaproszenie -  został on nowym właścicielem domu!";

        public String becomeOwner = "&aZostałeś nowym właścicielem domu!";

        public String playerCancelledOwnerInvitation = "&cGracz &4{PLAYER}&c, odrzucił twoje zaproszenie do zostania właścicielem domu!";

        public String cancelledOwnerInvitation = "&cOdrzucono zaproszenie do zostania właścicielem domu od gracza &4{INVITER}&c!";

        public String playerRemovedFromHouse = "&aGracz został usunięty z domu!";

        public String removedFromHouseSubject = "Usunięto z domu!";

        public String removedFromHouseMessage = "Zostałeś usunięty z domu gracza {OWNER}!";

        public String addedCoOwner = "&aMianowałeś gracza &e{PLAYER}&a współwłaścicielem!";

        public String addedCoOwnerSubject = "Zostałeś współwłaścicielem!";

        public String addedCoOwnerMessage = "Zostałeś współwłaścicielem domu gracza {OWNER}!";

        public String removedCoOwner = "&cUsunięto gracza &4{PLAYER}&c z współwłaścicieli!";

        public String removedCoOwnerSubject = "Usunięto współwłaściciela!";

        public String removedCoOwnerMessage = "Zostałeś usunięty z współwłaścicieli domu gracza {OWNER}!";

        public String changedPermission = "&aZmieniono uprawnienia gracza &e{PLAYER}&a!";

        public String changedPermissionSubject = "Zmiana uprawnień!";

        public String changedPermissionMessage = "Zmieniono twoje uprawnienia w domu gracza {OWNER}!";

        public String permissionToOpenDoors = "&cNie masz uprawnień do otwierania drzwi w tym domu!";

        public String permissionToOpenChests = "&cNie masz uprawnień do otwierania skrzyń w tym domu!";

        public String permissionToPlaceFurniture = "&cNie masz uprawnień do stawiania mebli w tym domu!";

        public String permissionToBreakFurniture = "&cNie masz uprawnień do niszczenia mebli w tym domu!";

        public String youNeedToBeOwner = "&cNie możesz wykonać tej akcji! Żeby to zrobić musisz być właścicielem domu!";

        public String soldHouse = "&aSprzedano dom za &e{SELL_PRICE}zł&a!";
    }

    @Contextual
    public static class Rent {
        public String rentEndSoon = "&cTwój wynajem kończy się za &4{DAYS}&c dni!";
    }

    @Contextual
    public static class Alert {
        public List<String> alertMessage = List.of(
                "&8&l--&c&lAlert&8&l--",
                "&cTemat: &e{SUBJECT}",
                "&cWiadomość: &e{MESSAGE}",
                " "
        );
    }

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "messages.yml");
    }

}