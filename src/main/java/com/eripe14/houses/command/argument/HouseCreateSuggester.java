package com.eripe14.houses.command.argument;

import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.suggester.Suggester;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

public class HouseCreateSuggester implements Suggester<CommandSender, String> {

    private final HouseService houseService;

    public HouseCreateSuggester(HouseService houseService) {
        this.houseService = houseService;
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<String> argument, SuggestionContext suggestionContext) {
        if (this.houseService.getAllHouses().isEmpty()) {
            return SuggestionResult.of("<houseId>");
        }

        SuggestionResult result = this.houseService.getAllHouses().stream()
                .map(House::getHouseId)
                .collect(SuggestionResult.collector());
        result.add(Suggestion.of("<houseId>"));

        return result;
    }

}