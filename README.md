# Houses #
## Main info: ##
An advanced plugin that allows players to rent or buy houses, exclusive properties or apartments. It has an extensive house panel system. The plugin also has the function of renovating your own house, there are three types of renovations:
1. complete
2. major
3. not interfering
## Features: ##
- Full customizable inventories, with the ability to add items, blocks, custom heads, and even items with custom NBT or custom model data.
- Full customizable messages and plugin configuration
- Managing house members permissions
- Ability to modify offline players
- Robbery system, with the ability to break doors, windows or use lockpicks. The main assumption is to stole furniture.
- ItemsAdder custom furniture support
- Schematics system, that allows administrators to back up and restore houses
- Block of flats system
- And many more!

## Commands: ##
### /house 
### permission: rp.house.command 
- /house - send help message
- /house reload - reload plugin configuration
- /house create <house-id> <house-district> <house-type> <block-of-flats-id> <rental-price-per-day> [purchase-price] - create new house (before sending command you should select plot region, after send command you should select house region and press F button to accept)
- /house clearRegions - helpful command while testing, it removes all WorldGuard regions
- /house updateDatabase - update database with current values
- /house clearDatabase - clear database
- /house schem <house-id> - shows all schematics that are concern to house
- /house schemLoad <house-id> <schematic-name> - paste specific schematic into house region
- /house list - shows all houses and theirs information
- /house edit <house-id> <house-district> <house-type> <block-of-flats-id> <rental-price-per-day> [purchase-price] - edit house
- /house delete <house-id> - delete house and all regions that are concern to house
- /house info <house-id> - shows information about house
- /house userInfo <player-name> - shows information about player houses and his houses history like left or sold houses
- /house items - give all items that are concern to houses to the sender

## Authors
- [eripe14](https://www.github.com/eripe14)