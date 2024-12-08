package com.eripe14.houses.command.argument;

import com.eripe14.houses.house.region.protection.ProtectionService;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.rollczi.litecommands.argument.Argument;
import dev.rollczi.litecommands.argument.parser.ParseResult;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.suggestion.Suggestion;
import dev.rollczi.litecommands.suggestion.SuggestionContext;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegionResolver extends ArgumentResolver<CommandSender, RegionArgument> {

    private final ProtectionService protectionService;

    public RegionResolver(ProtectionService protectionService) {
        this.protectionService = protectionService;
    }

    @Override
    protected ParseResult<RegionArgument> parse(Invocation<CommandSender> invocation, Argument<RegionArgument> context, String argument) {
        ProtectedRegion region = this.protectionService.getRegion(argument);

        if (argument.equalsIgnoreCase("-")) {
            return ParseResult.success(new RegionArgument(argument));
        }

        if (region == null) {
            return ParseResult.failure("Region with name " + argument + " not found.");
        }

        return ParseResult.success(new RegionArgument(region.getId()));
    }

    @Override
    public SuggestionResult suggest(Invocation<CommandSender> invocation, Argument<RegionArgument> argument, SuggestionContext context) {
        CommandSender sender = invocation.sender();
        Player player = sender instanceof Player ? (Player) sender : null;

        if (player == null) {
            return SuggestionResult.empty();
        }

        SuggestionResult collect = this.protectionService.getLocationRegions(player.getLocation())
                .getRegions()
                .stream()
                .map(ProtectedRegion::getId)
                .collect(SuggestionResult.collector());
        collect.add(Suggestion.of("-"));

        return collect;
    }
}