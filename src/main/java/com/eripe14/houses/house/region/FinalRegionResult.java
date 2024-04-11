package com.eripe14.houses.house.region;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import panda.std.Option;

public record FinalRegionResult(boolean success, Option<ProtectedRegion> plot, Option<ProtectedRegion> house) {

}