package com.example.fetchlistapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchlistapp.R
import com.example.fetchlistapp.model.Item
import com.example.fetchlistapp.model.ItemGroup

/**
 * This adapter controls the outer RecyclerView.
 * It shows a list of groups. Each group includes a title (listId)
 * and a nested inner RecyclerView with the group’s items.
 */
class ItemGroupAdapter : RecyclerView.Adapter<ItemGroupAdapter.GroupViewHolder>() {

    // Holds all the item groups to be shown in the outer RecyclerView
    private var itemGroups: List<ItemGroup> = emptyList()

    /**
     * This function updates the outer list with new groups.
     * It takes a list of ItemGroup objects and refreshes the UI.
     */
    fun updateData(newItemGroups: List<ItemGroup>) {
        this.itemGroups = newItemGroups
        notifyDataSetChanged() // tells RecyclerView to redraw the screen
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        // create and return a ViewHolder for a group item layout (item_group.xml)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        // connect the group at this position to the ViewHolder
        val itemGroup = itemGroups[position]
        holder.bind(itemGroup)
    }

    override fun getItemCount(): Int = itemGroups.size // get number of groups in the list

    /**
     * ViewHolder for each item group.
     * It shows the group’s listId as a title and includes a nested RecyclerView of that group’s items.
     */
    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // create views for the group title and the inner list of items
        private val groupTitleTextView: TextView = itemView.findViewById(R.id.groupTitleTextView)
        private val itemsRecyclerView: RecyclerView = itemView.findViewById(R.id.itemsRecyclerView)

        // adapter for the inner RecyclerView
        private val itemAdapter = ItemAdapter()

        /**
         * Displays one group’s title and sets up its inner RecyclerView of items.
         */
        fun bind(itemGroup: ItemGroup) {
            // set the text like "List ID: 1"
            groupTitleTextView.text = buildString {
                append("List ID: ")
                append(itemGroup.listId)
            }

            // set up the inner RecyclerView with vertical scrolling and link the adapter
            itemsRecyclerView.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = itemAdapter
            }

            // give the inner adapter the actual list of items in this group
            itemAdapter.updateData(itemGroup.items)
        }
    }
}

/**
 * This adapter controls the inner RecyclerView that shows the actual items inside each group.
 */
class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    // create list of items that belong to one group
    private var items: List<Item> = emptyList()

    /**
     * This function updates the inner list of items.
     * It takes a new list of Item objects and refreshes the UI.
     */
    fun updateData(newItems: List<Item>) {
        this.items = newItems
        notifyDataSetChanged() // refresh the inner RecyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create and return a ViewHolder for a single item row (item_row.xml)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // show the item at this position in the ViewHolder
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size // get number of items in the group

    /**
     * ViewHolder for one item.
     * Shows the item’s name and ID.
     */
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // create views that show the item’s name and ID
        private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        private val itemIdTextView: TextView = itemView.findViewById(R.id.itemIdTextView)

        /**
         * Displays a single item’s name and ID in the row.
         */
        fun bind(item: Item) {
            // if the item has a name, show it
            // if not, show blank
            itemNameTextView.text = item.name ?: ""

            // show the item ID
            itemIdTextView.text = buildString {
                append("ID: ")
                append(item.id)
            }
        }
    }
}
