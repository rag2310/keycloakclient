package com.rago.keycloakclient.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rago.keycloakclient.databinding.RowShipperBinding
import com.rago.keycloakclient.db.entities.Shipper

class ShipperAdapter(
    private val onNavDetails: (Shipper) -> Unit = {}
) : ListAdapter<Shipper, ShipperAdapter.ShipperViewHolder>(ShipperComparator()) {

    class ShipperViewHolder(
        private val binding: RowShipperBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Shipper, onClick: (Shipper) -> Unit) {
            binding.apply {
                title.text = item.title
                content.setOnClickListener {
                    onClick(item)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): ShipperViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: RowShipperBinding = RowShipperBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return ShipperViewHolder(binding)
            }
        }
    }

    class ShipperComparator : DiffUtil.ItemCallback<Shipper>() {
        override fun areItemsTheSame(oldItem: Shipper, newItem: Shipper): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Shipper, newItem: Shipper): Boolean =
            oldItem == newItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShipperViewHolder =
        ShipperViewHolder.create(parent)

    override fun onBindViewHolder(holder: ShipperViewHolder, position: Int) {
        holder.bind(getItem(position), onNavDetails)
    }
}