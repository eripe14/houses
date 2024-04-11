package com.eripe14.houses.hook;

import java.util.concurrent.CompletableFuture;

public interface Hook {

    CompletableFuture<Boolean> initialize();

    String pluginName();

}