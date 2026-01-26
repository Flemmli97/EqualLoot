# Equal Drops
[![](http://cf.way2muchnoise.eu/full_1445150_CurseForge_%20.svg)![](http://cf.way2muchnoise.eu/versions/1445150.svg)](https://www.curseforge.com/minecraft/mc-mods/equal-loot)  
[![](https://img.shields.io/modrinth/dt/gycZJL9N?logo=modrinth&label=Modrinth)![](https://img.shields.io/modrinth/game-versions/gycZJL9N?logo=modrinth&label=Latest%20for)](https://modrinth.com/mod/equal-loot)  
[![Discord](https://img.shields.io/discord/790631506313478155?color=0a48c4&label=discord)](https://discord.gg/8Cx26tfWNs)

A minecraft mod allowing killed mobs to drop per their loot for each player that helped with killing it.

To use this mod as a dependency add the following snippet to your build.gradle:  
```groovy
repositories {
    maven {
        name = "Flemmli97"
        url "https://maven.blazing-coop.net/releases"
    }
}

dependencies {    
    //Fabric/Loom==========    
    modImplementation("io.github.flemmli97:equal_loot:${minecraft_version}-${mod_version}-${mod_loader}")
    
    //NeoForge==========    
    implementation("io.github.flemmli97:equal_loot:${minecraft_version}-${mod_version}-${mod_loader}")
}
```
