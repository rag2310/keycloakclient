package com.rago.keycloakclient.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rago.keycloakclient.databinding.RowShipperBinding
import com.rago.keycloakclient.db.entities.Booking

class BookingAdapter(
    private val onNavDetails: (Booking) -> Unit = {}
) : ListAdapter<Booking, BookingAdapter.BookingViewHolder>(BookingComparator()) {

    class BookingViewHolder(
        private val binding: RowShipperBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Booking, onClick: (Booking) -> Unit) {
            binding.apply {
                title.text = item.title
                content.setOnClickListener {
                    onClick(item)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): BookingViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: RowShipperBinding = RowShipperBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return BookingViewHolder(binding)
            }
        }
    }

    class BookingComparator : DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean =
            oldItem == newItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder =
        BookingViewHolder.create(parent)

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position), onNavDetails)
    }
}