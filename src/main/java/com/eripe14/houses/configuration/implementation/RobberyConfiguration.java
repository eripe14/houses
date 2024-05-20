package com.eripe14.houses.configuration.implementation;

import com.eripe14.houses.configuration.ReloadableConfig;
import com.eripe14.houses.configuration.contextual.NpcData;
import com.eripe14.houses.house.region.HouseDistrict;
import com.eripe14.houses.position.Position;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.inventory.ItemFlag;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class RobberyConfiguration implements ReloadableConfig {

    @Description( { "# Lista lokalizacji, w których mógł pojawić się NPC odpowiedzialny za rozpoczynanie rabunku" })
    public List<Position> robberyNpcLocation = List.of(
            new Position(163.5, 68.0, -126.475, 0.0f, 0.0f, "world"),
            new Position(165.5, 68.0, -126.475, 0.0f, 0.0f, "world"),
            new Position(167.5, 68.0, -126.475, 0.0f, 0.0f, "world")
    );

    @Description( { " ", "# Lista lokalizacji, w których mógł pojawić się NPC odpowiedzialny za przyjmowanie skradzionych itemów" })
    public List<Position> thiefNpcLocation = List.of(
            new Position(163.5, 68.0, -125.475, 0.0f, 0.0f, "world"),
            new Position(165.5, 68.0, -125.475, 0.0f, 0.0f, "world"),
            new Position(167.5, 68.0, -125.475, 0.0f, 0.0f, "world")
    );

    @Description( { " ", "# Custom śnieżka, która rozpoczyna rabunek domu" })
    public ItemConfiguration robberySnowball = new ItemConfiguration(
            "snowball",
            "&eRabunek domu",
            List.of("&7Rzuć w szybę, aby rozpocząć rabunek domu"),
            List.of(ItemFlag.HIDE_ATTRIBUTES),
            Material.SNOWBALL,
            true
    );

    @Description( { " ", "# Custom item do lockpickingu" })
    public ItemConfiguration lockpickItem = new ItemConfiguration(
            "lockpick",
            "&eLockpick",
            List.of("&7Użyj, aby otworzyć zamek"),
            List.of(ItemFlag.HIDE_ATTRIBUTES),
            Material.TRIPWIRE_HOOK,
            true
    );

    @Description( { " ", "# Custom item do wyważania drzwi" })
    public ItemConfiguration kickDoorItem = new ItemConfiguration(
            "kick_door",
            "&eWyważ drzwi",
            List.of("&7Użyj, aby wyważyć drzwi"),
            List.of(ItemFlag.HIDE_ATTRIBUTES),
            Material.WOODEN_SWORD,
            true
    );

    @Description( { " ", "# Data npc, który daje zlecenia" })
    public NpcData principalNpcData = new NpcData(
            "Rabunki",
            "ewogICJ0aW1lc3RhbXAiIDogMTcxNTE4Mjk1NDU0MiwKICAicHJvZmlsZUlkIiA6ICIxYjQwYzcxMGZjMTY0NmQ2OTIxOTVmYzY3YzZlMTE0ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJ3c3pvbHNvbiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kYzMyODlmNWYxMWUwOWZiNzAwNzUyY2Q0M2I0MjU4YTkwNWFlNWQyNDI4N2Q3NWY3ZmRkNzZiYzQwNjY1ZTQzIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
            "LBG2tMF89iu18Cm57KRFaIhFuBVSCj0kPyCpN3UWgSvJMVpqiE5iq7leOvk9w0QcgJdn+iahehlkyIoXBpjeOUwgFS04hwT/KpdOoktnEvFwvRtCb2AWFJIMubPXTzcRdxIB183yrPjnp9U95e1jtM6zDuv8lzeF+GFRPf+gdAHd799RhzWZ4OEflR8cwOXR5pLx09mMVgAzYuUEam41d0jpkbVGlXdemVlegfOaSNwxWRjtUacBBJyliN3PykO13Sagl8iXPsqYsUL82+kvVGzT1GasUrX5TUUqRELA84xqfbr4UykS8iNoB4NZoDInPvjQnj024vJdC9uwDDgdxNh/5KVn2OmdGESSTqkYFRySZVQFW673KUx+fztpzSR7Ltm682yc32mZyO/c99xzljbfIo0U8pTyvE1K/6iuzQcYvIJ64A9ITeJjZw7rxmZOdPQ9GftycZ/44gZWr6OWYrfZkiq0dLsCIBIQ/kug7ERnOtuflly/m3ZElrEuszTKdxiQoCj9CfwYRhjO38zIqTxqTd5l360CwMt1nFxPv+VFw6/v2t4bmIboBVESVjOq4K23OsV+fEOMkBTaGM2w0Dx7YRRqUFAjJeV8Ql1t0yupIsDAiTeKBpxSZQTCzrYRzIVduPSKG7ZR4T5l9EM5Zfq3xYkIGBCrH5NraBqJbMk="
    );

    @Description( { " ", "# Data npc, który przyjmuje skradzione itemy" })
    public NpcData thiefNpcData = new NpcData(
            "Złodziej",
            "eyJ0aW1lc3RhbXAiOjE1ODYxMTE2Mzc4MzcsInByb2ZpbGVJZCI6IjkxZjA0ZmU5MGYzNjQzYjU4ZjIwZTMzNzVmODZkMzllIiwicHJvZmlsZU5hbWUiOiJTdG9ybVN0b3JteSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTE2MGI2MTQ1ZjM1ZmNhZWVlMzcyZWE4MTNiNzU5ZDgxMjE2NzgyZGJmZTY1OTY3OGYzMzVhMzMxMzlhZDNkOSJ9fX0=",
            "IyInUeeTlmlDYGvx3UEyA3nO892YBlbhwfP4JXPPsvrELTd1bJhiy0pjD5RKOXGSZwEQuxcid7z43nEeF+iC7souRSkcH0hv+gYP3kyY7l8Gmj6tKugoEaG0nJjDMW+jLvk33bQ6+7k5/JNUlD31uibOi0E04R6/h5wiMn07Xi+OqC1SkXy1k/NBrQwh8d8859Z06c2huSeF/yXhWGvAoKsS6Q2YBRZIEM6x/y0rAzkJex4epEhQrKM9XULfkS8ovB7DMTN2UMEYYkG3kHC0jM9KQyB/wCjzNlpQj7wJ671QxrS6wFHlK6ZCuJe58nrHrUpyPt6TEMESlIIzO9yQUu9leEA+PjJzP2PKNNlx1yaPMNjh8kA94ZTI625bySN0p4g/wP95NZ5dYqY2YVygqHoXRkOealc+HvM6BTDgLFxt2gLsO8nsC/yXhjIX6mUNx0pcNwNlmq2B4bf0d/OcvkMYYrl+pPo/hKNZE8ZBIgLQQcAdRZ4xy4RTmRoGZKR111wenS42lnZU6qHw6puky19jMyL5QJXmRdyJeJSm53prsoYA0O6ow9yUS47b2ZXe83vJGNZRta3r5PO9Jbqv+FoEt0YBXCTKFcYkgFozqWR1+vsNsixy7kirI0s02VwFZEROebJBRZDjvJ2hgjYEj9PO/41EE9tLszUHUgLM+Rg="
    );

    @Description( { " ", "# Co ile ma się odświeżać pozycja npc" })
    public Duration npcRefreshRate = Duration.ofMinutes(5);

    @Description( { " ", "# Napis ma title'u podczas lockpicka" })
    public String lockPickingTitle = "&eLockpicking";

    @Description( { " ", "# Ile sekund ma trwać lockpicking" })
    public int lockPickingDuration = 18;

    @Description( { " ", "# Ile ma być wszystkich kresek" })
    public int totalBars = 35;

    @Description( { " ", "# Ile ma być sekund na zrobienie akcji, gdy pojawi się czerwona kreska" })
    public int redBarTime = 2;

    @Description( { " ", "# Kolor bossbara podczas wyważania drzwi" })
    public BarColor kickDoorBossBarColor = BarColor.GREEN;

    @Description( { " ", "# Napis na bossbarze podczas wyważania drzwi" })
    public String kickDoorBossBarTitle = "&ePoziom ";

    @Description( { " ", "# Szansa na wylosowanie się rabunku domu NPC (Podana liczba to wartość procentowa)" })
    public double npcHouseProbability = 90;

    @Description( { " ", "# Szansa procentowa na wylosowanie każdej z dzielnic" })
    public Map<Double, HouseDistrict> districtProbability = Map.of(
            0.99, HouseDistrict.SLUMS,
            1.0, HouseDistrict.POOR,
            1.1, HouseDistrict.NORMAL,
            7.0, HouseDistrict.RICH,
            90.0, HouseDistrict.EXCLUSIVE
    );

    @Description( { " ", "# Przedział do losowania maksymalnej wagi przedmiotów, które gracz może wynieść z rabunku" })
    public int minMaxWeight = 10;
    public int maxMaxWeight = 20;

    @Description( { " ", "# Permisja osób, które dostają powiadomienie o włamaniu (policja)" })
    public String policePermission = "rp.houses.police";

    @Description( { " ", "# Po jakim czasie policja dostaje powiadomienie o włamaniu w przypadku, gdy gracz nie ma w domu alarmu" })
    public Duration policeNotificationDelay = Duration.ofSeconds(30);

    @Description( { " ", "# Lista zablokowanych przedmiotów, które nie mogą być wynoszone z rabunku" })
    public List<String> blockedNamespaceIds = List.of(
            "stefor:gym_machine_14" // Panel domu
    );

    @Description( { " ", "# Czas pakowania przedmiotu (w sekundach)" })
    public int packingTime = 3;

    @Description( { " ", "# Przedział do losowania maksymalnej ceny przedmiotów"})
    public int minMaxPrice = 10;
    public int maxMaxPrice = 20;

    @Description( { " ", "# Po ilu sekundach od rozpoczęcia bezczynności podczas wyważania drzwi, pasek ma spadać"})
    public double regressionStartTime = 1;

    @Description( { " ", "# Dźwięk pękania lockpick'u"})
    public Sound lockpickBreakSound = Sound.ENTITY_ITEM_BREAK;

    @Description( { " ", "# Dźwięk poprawnego kliknięcia czerwonej kreski w lockpick'u"})
    public Sound lockpickSuccessSound = Sound.BLOCK_NOTE_BLOCK_PLING;

    @Description( { " ", "# Dźwięk progresu lockpick'u"})
    public Sound lockpickProgressSound = Sound.BLOCK_NOTE_BLOCK_HAT;

    @Description( { " ", "# Dźwięk rozpoczęcia pakowania itemów"})
    public Sound packingStartSound = Sound.BLOCK_NOTE_BLOCK_BASS;

    @Description( { " ", "# Dźwięk sprzedaży itemów"})
    public Sound itemSellSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "robberies.yml");
    }

}