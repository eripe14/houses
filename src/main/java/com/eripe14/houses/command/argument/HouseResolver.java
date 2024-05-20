package com.eripe14.houses.command.argument;

import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;
import panda.std.Option;

public class HouseResolver extends ArgumentResolver<CommandSender, House> {

    private final HouseService houseService;

    public HouseResolver(HouseService houseService) {
        this.houseService = houseService;
    }

    @Override
    protected ParseResult<House> parse(Invocation<CommandSender> invocation, Argument<House> context, String argument) {
        Option<House> houseOption = this.houseService.getHouse(argument);

        if (houseOption.isEmpty()) {
            return ParseResult.failure("House not found!");
        }

        return ParseResult.success(houseOption.get());
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<House> argument, SuggestionContext context) {
        return this.houseService.getAllHouses().stream()
                .map(House::getHouseId)
                .collect(SuggestionResult.collector());
    }
}