plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("xyz.jpenilla.run-paper") version "2.0.1"
}

group = "com.eripe14"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()

    maven { url = uri("https://repo.panda-lang.org/releases") }
    maven { url = uri("https://repo.codemc.io/repository/maven-public/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://maven.citizensnpcs.co/repo") }
    maven { url = uri("https://maven.enginehub.org/repo/") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/craftcityrp/ccrpdev-api")
        credentials {
            username = "eripe14"
            password = System.getenv("GITHUB_TOKEN") ?: ""
        }
    }
}

dependencies {
    // Spigot
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")

    compileOnly("pl.craftcityrp.developerapi:ccrpdev-api:1.8-SNAPSHOT")

    // Kyori Adventure
    implementation("net.kyori:adventure-platform-bukkit:4.3.2")
    implementation("net.kyori:adventure-text-minimessage:4.16.0")

    // Cdn
    implementation("net.dzikoysk:cdn:1.14.4")

    // LiteCommands
    implementation("dev.rollczi:litecommands-bukkit:3.4.1")

    // TriumphGui
    implementation("dev.triumphteam:triumph-gui:3.1.7")

    // Panda Utilities
    implementation("org.panda-lang:panda-utilities:0.5.2-alpha")

    // Vault
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    // ItemsAdder
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.1")

    // Citizens2
    compileOnly("net.citizensnpcs:citizens-main:2.0.33-SNAPSHOT") {
        exclude("*", "*")
    }

    // FAWE
    implementation(platform("com.intellectualsites.bom:bom-newest:1.42")) // Ref: https://github.com/IntellectualSites/bom
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }

    // WorldGuard
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.8-SNAPSHOT")

    // NbtApi
    implementation("de.tr7zw:item-nbt-api:2.12.4")

    // Expiring Map
    implementation("net.jodah:expiringmap:0.5.11")
}

bukkit {
    main = "com.eripe14.houses.HousesPlugin"
    apiVersion = "1.13"
    prefix = "Houses"
    name = "Houses"
    author = "eripe14"
    version = "${project.version}"
    depend = listOf("FastAsyncWorldEdit", "WorldGuard", "ItemsAdder", "Vault", "Citizens")
    loadBefore = listOf("ccrpdev-api")
}

tasks {
    runServer {
        minecraftVersion("1.19.3")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("Houses v${project.version} (MC 1.19.x).jar")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "META-INF/**",
        "javax/**"
    )

    val prefix = "com.eripe14.houses.libs"
    listOf(
        "panda",
        "org.panda_lang",
        "net.dzikoysk",
        "net.kyori",
        "dev.rollczi",
        "dev.triumphteam",
        "com.github.ben-manes.caffeine",
        "de.tr7zw.changeme.nbtapi"
    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}