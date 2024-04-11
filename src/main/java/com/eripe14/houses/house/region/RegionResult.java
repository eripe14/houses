package com.eripe14.houses.house.region;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import panda.std.Option;

public record RegionResult(boolean success, Option<ProtectedRegion> optionalRegion) {

}