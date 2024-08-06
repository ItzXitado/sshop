![App Screenshot](https://i.imgur.com/xnXwU41.png)

# SSHOP

Its a Java plugin for the Spigot API. This makes server owners be able to create a virtual creature spawner shop, for players to buy.

## Features

- Vault Integration
- Buy History
- Infinite Pagination in all menus
- Infinite spawner creation
- MongoDB Storage (Async)
- Developer API


## Future features

- Add Maria DB, MySQL, and SQLITE
- Implement Holder system to reorganize the menus


## UserManager API

The `UserManager` class is responsible for managing users and their histories within the plugin. It provides methods to create, fetch, and manage users, as well as to handle history related to entity types.

### Fields

- **`users`** (`Map<UUID, User>`): A concurrent map that holds all users identified by their UUIDs.

### Methods

#### `fetchUserWithId(UUID playerId): User`

Fetches a `User` object using the player's UUID.

- **Parameters:**
  - `UUID playerId`: The UUID of the player.
- **Returns:** The `User` object associated with the provided `UUID`.

#### `fetchUserWithName(String playerName): User`

Fetches a `User` object using the player's name.

- **Parameters:**
  - `String playerName`: The name of the player.
- **Returns:** The `User` object associated with the provided player name.

#### `createUser(UUID playerId): void`

Creates a new `User` with the specified UUID if it does not already exist.

- **Parameters:**
  - `UUID playerId`: The UUID of the player to create.
- **Returns:** None.

#### `fetchHistoryByEntity(UUID playerId, EntityType entityType): History`

Fetches the history of a specific entity type for a player.

- **Parameters:**
  - `UUID playerId`: The UUID of the player.
  - `EntityType entityType`: The type of entity to fetch history for.
- **Returns:** The `History` object associated with the specified entity type, or `null` if not found.

#### `register(User user): void`

Registers a user in the system. If the user is already present, this method will not overwrite the existing user.

- **Parameters:**
  - `User user`: The `User` object to register.
- **Returns:** None.

#### `throwSpawnerInHistory(UUID playerId, EntityType type): void`

Updates the player's history with a new spawner event for the specified entity type. If a history entry already exists for the entity type, it increments the amount bought and updates the date. Otherwise, it creates a new history entry.

- **Parameters:**
  - `UUID playerId`: The UUID of the player.
  - `EntityType type`: The entity type for the spawner.
- **Returns:** None.

#### `getSortedList(List<History> playerHistory, Sort sort): ArrayList<ItemStack>`

Returns a sorted list of `ItemStack` objects based on the player's history and the specified sorting criteria.

- **Parameters:**
  - `List<History> playerHistory`: The list of history records to be sorted.
  - `Sort sort`: The sorting criteria (`DATE`, `DATE_REVERSED`, `AMOUNT`, `AMOUNT_REVERSED`).
- **Returns:** A sorted `ArrayList<ItemStack>` based on the provided sorting criteria.

### Sorting Criteria Enum (`Sort`)

- `DATE`: Sort by date, oldest first.
- `DATE_REVERSED`: Sort by date, newest first.
- `AMOUNT`: Sort by amount bought, smallest first.
- `AMOUNT_REVERSED`: Sort by amount bought, largest first.

## SpawnerManager API

The `SpawnerManager` class is responsible for managing the spawners in your plugin. It loads spawner configurations and provides methods to retrieve spawners based on entity types.

### Fields

- **`spawnerList`** (`List<Spawner>`): A list that holds all the spawners loaded from the configuration.

### Constructor

#### `SpawnerManager()`

Initializes a new instance of the `SpawnerManager` class and loads the spawner configurations into the `spawnerList`.

### Methods

#### `findSpawnerByType(EntityType entityType): Spawner`

Finds and returns a `Spawner` object based on the specified entity type.

- **Parameters:**
  - `EntityType entityType`: The type of entity to find the spawner for.
- **Returns:** The `Spawner` object associated with the specified entity type, or `null` if not found.

### Internal Methods

#### `loadSpawners(): void`

Loads spawners from the plugin configuration file (`config.yml`) and populates the `spawnerList`. Logs any invalid configuration entries to the console.

- **Note:** This method is called automatically during the construction of `SpawnerManager` and is not intended to be called directly.
