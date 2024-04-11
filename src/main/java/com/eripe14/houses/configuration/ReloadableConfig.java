package com.eripe14.houses.configuration;

import net.dzikoysk.cdn.source.Resource;

import java.io.File;

@FunctionalInterface
public interface ReloadableConfig {

    Resource resource(File folder);

}