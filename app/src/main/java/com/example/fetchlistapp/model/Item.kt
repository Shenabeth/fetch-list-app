package com.example.fetchlistapp.model

/**
 * Data class representing an item from the JSON data.
 *
 * @property id The unique identifier of the item
 * @property listId The group identifier for the item
 * @property name The name of the item (can be null or empty)
 */
data class Item (
    val id: Int,
    val listId: Int,
    val name: String?
)

/**
 * Data class representing a group of items with the same listId.
 * Used for displaying items in the UI grouped by listId.
 *
 * @property listId The group identifier
 * @property items The list of items belonging to this group
 */
data class ItemGroup (
    val listId: Int,
    val items: List<Item>
)