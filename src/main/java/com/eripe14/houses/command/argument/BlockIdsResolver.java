package com.eripe14.houses.command.argument;

import com.eripe14.houses.house.HouseService;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;

public class BlockIdsResolver extends ArgumentResolver<CommandSender, BlockId> {

    private final HouseService houseService;

    public BlockIdsResolver(HouseService houseService) {
        this.houseService = houseService;
    }

    @Override
    protected ParseResult<BlockId> parse(Invocation<CommandSender> invocation, Argument<BlockId> context, String argument) {
        return ParseResult.success(new BlockId(argument));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<BlockId> argument, SuggestionContext context) {
        return this.houseService.getAllBlockOfFlatsIds().stream().collect(SuggestionResult.collector());
    }
}