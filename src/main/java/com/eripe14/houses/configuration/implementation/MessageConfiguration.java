package com.eripe14.houses.configuration.implementation;

import com.eripe14.houses.configuration.ReloadableConfig;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;

import java.io.File;
import java.util.List;

public class MessageConfiguration implements ReloadableConfig {

    @Description( { " ", "# Wrong command usage" })
    public WrongUsage wrongUsage = new WrongUsage();

    @Description( { " ", "# Sekcja domów" })
    public House house = new House();

    @Description( { " ", "# Sekcja wynajmu" })
    public Rent rent = new Rent();

    @Description( { " ", "# Sekcja alertów" })
    public Alert alert = new Alert();

    @Description( { " ", "# Sekcja rabunków" })
    public Robbery robbery = new Robbery();

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

        public String cantCreateApartment = "&cNie udało się stworzyć apartmentu!";

        public String preCreateRegions = "&aKliknij F, jak skończysz zaznaczać drugi obszar!";

        public String createdBothRegions = "&aStworzono regiony!";

        public String createdHouse = "&aStworzono dom o id &e{HOUSE_ID}&a!";

        public String createdApartmentHouse = "&aStworzono apartment o id &e{HOUSE_ID}&a w bloku &e{BLOCK}!";

        public String houseAlreadyExists = "&cDom o takim id &4({HOUSE_ID})&c już istnieje!";

        public String boughtHouse = "&aZakupiono dom o id &e{HOUSE_ID}, &aza &e{BUY_PRICE}zł&a!";

        public String notEnoughMoneyToBuy = "&cNie masz wystarczająco pieniędzy na zakup domu!";

        public String canNotBoughtHouseNow = "&cNie możesz teraz kupić domu! Dom jest aktualnie okradany!";

        public String requiredRentalTime = "&cCzas wynajmu nie może być mniejszy niż {MIN_RENT_TIME} dni!";

        public String requiredExtendRentTime = "&cMinimalne przedłużenie wynajmu to 1 dzień!";

        public String extendRentTime = "&aPrzedłużono wynajem domu o &e{DAYS}&a dni za &e{PRICE}!";

        public String rentedHouse = "&aWynajęto dom o id &e{HOUSE_ID} &aza &e{PRICE}&a, na okres &e{RENT_TIME}&a dni!";

        public String rentedApartment = "&aWynajęto dom o id &e{HOUSE_ID} &aw &e{BLOCK} &aza &e{PRICE}&a, na okres &e{RENT_TIME}&a dni!";

        public String notEnoughMoneyToRent = "&cNie masz wystarczająco pieniędzy na wynajem!";

        public String createdInvite = "&aKliknij na gracza, by dodać go do domu.";

        public String inviteExpired = "&cZaproszenie wygasło!";

        public String playerAddedToHouse = "&aGracz &e{PLAYER}&a potwierdził twoje zaproszenie - został on dodany do domu!";

        public String joinedHouse = "&aDołączono do domu gracza &e{INVITER}&a &e({HOUSE_ID})&a!";

        public String playerCancelledInvitation = "&cGracz &4{PLAYER}&c, odrzucił twoje zaproszenie do domu!";

        public String cancelledInvitation = "&cOdrzucono zaproszenie do domu od gracza &4{INVITER}&c!";

        public String createdOwnerInvite = "&aKliknij na gracza, by mianować go właścicielem domu.";

        public String playerMustMemberToBecomeOwner = "&cGracz musi być członkiem domu, żeby zostać właścicielem!";

        public String cancelledSendingInviteToChangeOwner = "&cAnulowano próbe zmienienia właściciela domu!";

        public String inviteSent = "&aZaproszenie zostało wysłane!";

        public String changedOwner = "&aGracz &e{PLAYER}&a potwierdził twoje zaproszenie -  został on nowym właścicielem domu!";

        public String becomeOwner = "&aZostałeś nowym właścicielem domu! {HOUSE_ID}";

        public String playerCancelledOwnerInvitation = "&cGracz &4{PLAYER}&c, odrzucił twoje zaproszenie do zostania właścicielem domu!";

        public String cancelledOwnerInvitation = "&cOdrzucono zaproszenie do zostania właścicielem domu od gracza &4{INVITER}&c!";

        public String playerRemovedFromHouse = "&aGracz został usunięty z domu!";

        public String removedFromHouseSubject = "Usunięto z domu!";

        public String removedFromHouseMessage = "Zostałeś usunięty z domu gracza {OWNER}!";

        public String addedCoOwner = "&aMianowałeś gracza &e{PLAYER}&a współwłaścicielem!";

        public String addedCoOwnerSubject = "Zostałeś współwłaścicielem!";

        public String addedCoOwnerMessage = "Zostałeś współwłaścicielem domu gracza {OWNER}, {HOUSE_ID}!";

        public String removedCoOwner = "&cUsunięto gracza &4{PLAYER}&c z współwłaścicieli!";

        public String removedCoOwnerSubject = "Usunięto współwłaściciela!";

        public String removedCoOwnerMessage = "Zostałeś usunięty z współwłaścicieli domu gracza {OWNER}!";

        public String changedPermission = "&aZmieniono uprawnienia gracza &e{PLAYER}&a!";

        public String changedPermissionSubject = "Zmiana uprawnien!";

        public String changedPermissionMessage = "Zmieniono twoje uprawnienia w domu ({HOUSE}) gracza {OWNER}!";

        public String permissionToOpenDoors = "&cNie masz uprawnień do otwierania drzwi w tym domu!";

        public String permissionToOpenChests = "&cNie masz uprawnień do otwierania skrzyń w tym domu!";

        public String permissionToPlaceFurniture = "&cNie masz uprawnień do stawiania mebli w tym domu!";

        public String permissionToBreakFurniture = "&cNie masz uprawnień do niszczenia mebli w tym domu!";

        public String youNeedToBeOwner = "&cNie możesz wykonać tej akcji! Żeby to zrobić musisz być właścicielem domu!";

        public String youNeedToBeOwnerOrCoOwner = "&cNie możesz wykonać tej akcji! Żeby to zrobić musisz być właścicielem lub współwłaścicielem domu!";

        public String soldHouse = "&aSprzedano dom (&e{HOUSE_ID}&a)za &e{SELL_PRICE}zł&a!";

        public String provideRenovationRequest = "&aPodaj powód renowacji domu!";

        public String provideRenovationTime = "&aPodaj ilość dni potrzebnych na renowacje domu!";

        public String notEnoughMoneyToRequestRenovate = "&cNie masz wystarczająco pieniędzy na renowację domu!";

        public String renovationRequestCanNotBeEmpty = "&cPowód renowacji nie może być pusty! Zacznij tworzenie wniosku jeszcze raz!";

        public String renovationTimeCanNotBeEmpty = "&cCzas renowacji nie może być pusty! Zacznij tworzenie wniosku jeszcze raz!";

        public String renovationTimeCanNotBe = "&cCzas renowacji musi być z zakresu 1-{TIME}!";

        public String renovationTimeMustBeNumber = "&cCzas renowacji musi być liczbą!";

        public String renovationRequestCancelled = "&cWniosek o renowację domu został anulowany!";

        public String renovationRequestCompleted = "&aWniosek o renowację domu został zakończony oraz zatwierdzony przez Ciebie! Teraz musisz czekać na zatwierdzenie go przez urzędnika";

        public String renovationRequestAccepted = "&aZaakceptowano wniosek o renowację domu!";

        public String renovationRequestDenied = "&cOdrzucono wniosek o renowację domu!";

        public String renovationRequestSubject = "Nowe wnioski o renowację!";

        public String renovationRequestMessage = "W domu o id &c{HOUSE}&e pojawił się nowy wniosek o renowację! Sprawdź go!";

        public String renovationRequestAcceptedSubject = "Zaakceptowano wniosek o renowację!";

        public String renovationRequestAcceptedMessage = "Twój wniosek o renowację domu ({HOUSE_ID}) został zaakceptowany!";

        public String renovationRequestDeniedSubject = "Odrzucono wniosek o renowację!";

        public String renovationRequestDeniedMessage = "Twój wniosek o renowację domu ({HOUSE_ID} został odrzucony!";

        public String renovationTerminateOwnerSubject = "Remont się zakończył!";

        public String renovationTerminateOwnerMessage = "Remont twojego domu został zakończony! Poczekaj na zatwierdzenie zmian!";

        public String renovationTerminateSubject = "Zakończono remont!";

        public String renovationTerminateMessage = "Remont domu (id &c{HOUSE}&e) został zakończony! Zaakceptuj zmiany lub je odrzuć!";

        public String renovationChangesAccepted = "&aZaakceptowano remont w domu o id &e{HOUSE}!";

        public String renovationChangesAcceptedSubject = "Zaakceptowano zmiany!";

        public String renovationChangesAcceptedMessage = "Twoje zmiany w remoncie domu zostały zaakceptowane!";

        public String renovationChangesDenied = "&aOdrzucono remont w domu o id &e{HOUSE}!";

        public String renovationChangesDeniedSubject = "Odrzucono zmiany!";

        public String renovationChangesDeniedMessage = "Twoje zmiany w remoncie domu zostały odrzucone!";

        public String renovationAcceptanceSubject = "Nowe wnioski!";

        public String renovationAcceptanceMessage = "Zobacz panel dotyczący akceptacji remontów, ponieważ pojawiły się nowe wnioski o remont domów!";

        public String canNotModifyYourself = "&cNie możesz siebie modyfikować!";

        public String setUpHouseFurnitureForSale = "&aUstawiono obiekt, odpowiadający za zakup domu!";

        public String removedHouseFurnitureForSale = "&aUsunięto obiekt, odpowiadający za zakup domu!";

        public String houseListCommandHeader = "&7Lista wszystkich domów:";

        public String houseListCommandEntry = "&8 >>" +
                " &7id &e{HOUSE_ID} &8-" +
                " &7typ &e{HOUSE_TYPE} &8-" +
                " &7dzielnica &e{HOUSE_DISTRICT} &8-" +
                " &7właściciel &e{HOUSE_OWNER}";

        public String houseDeleted = "&aUsunięto dom o id &e{HOUSE_ID}&a!";

        public String houseUpdated = "&aZaktualizowano parametry domu! Wpisz /house info <id domu>, aby zobaczyć jego parametry";

        public String ownerInfo = "właściciel";

        public String tenantInfo = "najemca";

        public String houseInfo =
                "&7id &e{HOUSE_ID} &8-" +
                " &7typ &e{HOUSE_TYPE} &8-" +
                " &7dzielnica &e{HOUSE_DISTRICT} &8-" +
                " &7{PURCHASE_METHOD} &e{HOUSE_OWNER}&7 od &e{OWNER_SINCE} &8-" +
                " &7dzienna cena wynajmu &e{HOUSE_RENTAL_PRICE} &8-" +
                " &7cena kupna &e{HOUSE_BUY_PRICE}";

        public String houseInfoMembersHeader = "&7Członkowie:";

        public String houseInfoMembersEmpty = "&cBrak członków!";

        public String houseInfoMembersEntry = "&8 >>" +
                " &7nick: &e{MEMBER_NAME} &8-" +
                " &7dodany do domu od: &e{MEMBER_JOINED_AT} &8-" +
                " &7współwłaściciel: &e{MEMBER_IS_CO_OWNER}";

        public String houseInfoCoOwnersHeader = "&7Współwłaściciele:";

        public String houseInfoCoOwnersEmpty = "&cBrak współwłaścicieli!";

        public String houseInfoCoOwnersEntry = "&8 >>" +
                " &7nick: &e{CO_OWNER_NAME} &8-" +
                " &7dodany do współwłaścicieli od: &e{CO_OWNER_JOINED_AT}";

        public String houseRentInfo = "Dom wynajęty przez: &e{RENTER} &7na &e{DAYS}";

        public String houseBuyInfo = "Dom kupiony przez: &e{BUYER} &7za &e{PRICE}zł";

        public String houseInfoUserHeader = "&7Informacje o graczu &e{TARGET}:";

        public String houseInfoUserRankOwner = "właściciel";

        public String houseInfoUserRankCoOwner = "współwłaściciel";

        public String houseInfoUserRankMember = "członek";

        public String houseInfoUserEntry = "&8 >> " +
                " &7id domu: &e{HOUSE_ID} &8-" +
                " &7{PURCHASE_INFO} &8-" +
                " &7Ranga: &e{RANK} &7od &e{RANK_SINCE}";

        public String houseInfoUserPermissionsHeader = "&7Uprawnienia:";

        public String canOpenDoorsPermission = " &8- &7Może otwierać drzwi &e{CAN_OPEN_DOORS}";

        public String canOpenChestsPermission = " &8- &7Może otwierać skrzynie &e{CAN_OPEN_CHESTS}";

        public String canPlaceFurniturePermission = " &8- &7Może stawiać meble &e{CAN_PLACE_FURNITURE}";

        public String canRenovationPermission = " &8- &7Może zgłaszać renowacje &e{CAN_RENOVATE}";

        public String soldHousesInfoUserHeader = "&7Sprzedane domy:";

        public String soldHousesInfoUserEmpty = "&cBrak sprzedanych domów!";

        public String soldHousesInfoUserEntry = "&8 >> " +
                " &7id domu: &e{HOUSE_ID} &8-" +
                " &7cena: &e{PRICE}zł &8-" +
                " &7data sprzedaży: &e{DATE}";

        public String leftHousesInfoUserHeader = "&7Opuścił domy:";

        public String leftHousesInfoUserEmpty = "&cBrak opuszczonych domów!";

        public String leftHousesInfoUserEntry = "&8 >> " +
                " &7id domu: &e{HOUSE_ID}";

        public String previousApplicationNotAcceptedYet = "&cNie możesz złożyć kolejnego wniosku o renowację, dopóki poprzedni nie zostanie zaakceptowany!";

        public String noRenovationInProgress = "&cNie ma aktualnie żadnego remontu w trakcie!";

        public String noCoOwners = "&cBrak współwłaścicieli!";

        public String noMembers = "&cBrak członków!";

        public String alreadyCoOwner = "&cGracz jest już współwłaścicielem!";

        public String cannotInviteOwner = "&cNie możesz zaprosić właściciela do domu!";

        public String maxExtendRentDays = "&cNie możesz przedłużyć wynajmu o więcej niż {MAX_DAYS} dni!";
    }

    @Contextual
    public static class Robbery {
        public String startedLockPicking = "&aRozpoczęto lockpicking! Gdy pojawi się czerwona kreska, masz &e{TIME}&a, na kliknięcie klawisza &eF&a!";

        public String lostLockPicking = "&cPrzegrałeś lockpicking! Nie zdążyłeś kliknąć na czas!";

        public String wonLockPicking = "&aWygrałeś lockpicking! Otworzyłeś drzwi!";

        public String startedKickingDoor = "&aRozpoczęto kopanie drzwi! Klikaj SHIFT, żeby progres się zwiększył, bezczynność spowoduje regres!";

        public String endedKickingDoor = "&aUdało ci się otworzyć drzwi!";

        public String failedToKickDoor = "&cNie udało ci się otworzyć drzwi!";

        public String alreadyHasRobbery = "&cJuż masz aktywny rabunek!";

        public List<String> startedRobberyRandomMessages = List.of(
                "&aRozpoczęto rabunek domu o id &e{HOUSE}&a! Zbieraj przedmioty, aż zapełnisz swój plecak!",
                "&7Rozpoczęto rabunek domu o id &9{HOUSE}&7! Zbieraj przedmioty, aż zapełnisz swój plecak!",
                "&dRozpoczęto rabunek domu o id &a{HOUSE}&d! Zbieraj przedmioty, aż zapełnisz swój plecak!"
        );

        public List<String> endedRobberyRandomMessages = List.of(
                "&aZakończono rabunek domu o id &e{HOUSE}&a!",
                "&7Zakończono rabunek domu o id &9{HOUSE}&7!",
                "&dZakończono rabunek domu o id &a{HOUSE}&d!"
        );

        public String policeRobberySubject = "Włamanie do domu!";

        public String policeRobberyMessage = "W domu o id &c{HOUSE}&e, doszło do włamania!";

        public String houseStuffRobberySubject = "Włamanie do domu!";

        public String houseStuffRobberyMessage = "W twoim domu o id &c{HOUSE}&e, doszło właśnie do włamania!";

        public String itemCouldBeUsedOnlyWhileRobbing = "&cTen przedmiot może być użyty tylko podczas rabunku!";

        public String itemCouldBeUsedOnlyInRobbingHouse = "&cTen przedmiot może być użyty tylko w domu, który masz obrabować!";

        public String thisItemIsBlocked = "&cTen przedmiot jest zablokowany i nie może być wyniesiony z rabunku!";

        public String alreadyStealingItem = "&cJuż kradniesz jakiś przedmiot! Poczekaj, aż go ukradniesz, by zabrać kolejny!";

        public String startStealingItem = "&aRozpoczęto kradzież przedmiotu! Przytrzymaj shift, aby przerwać pakowanie!";

        public String cancelledStealingItem = "&cPrzerwano kradzież przedmiotu!";

        public String stoleItem = "&aUkradłeś przedmiot! Waga: &e{WEIGHT}/{MAX_WEIGHT}&a!";

        public String maxWeightReached = "&cOsiągnąłeś maksymalną wagę przedmiotów! Idź zwrócić itemy!";

        public String failedToFindHouseToRob = "&cNie udało się znaleźć domu do rabunku!";

        public String noCurrentRobbery = "&cNie masz aktywnego rabunku!";

        public String sellStolenItem = "&aSprzedano ukradziony przedmiot &e{ITEM}&a za &e{PRICE}zł&a!";

        public String brokeLockPick = "&cTwój lockpick się zepsuł! Nie udało się kliknąć na czas czerwonej kreski!";

        public String houseHasBeenRobbedSubject = "Dom okradziony!";

        public String houseHasBeenRobbedMessage = "Twój dom o id &c{HOUSE}&e został okradziony!";

        public String bulletProofGlassPane = "&cTa szyba jest kuloodporna!";
    }

    @Contextual
    public static class Rent {
        public String rentEndSoonSubject = "Bliski koniec wynajmu!";

        public String rentEndSoonMessage = "Twój wynajem domu kończy się za &e{TIME}!";

        public String rentEndedSubject = "Koniec wynajmu!";

        public String rentEndedMessage = "Twój wynajem domu o id &c{HOUSE}&e zakończył się!";
    }

    @Contextual
    public static class Alert {
        public List<String> alertMessage = List.of(
                " ",
                "&c&lAlert",
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