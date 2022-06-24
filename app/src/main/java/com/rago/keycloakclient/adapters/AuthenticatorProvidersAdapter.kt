package com.rago.keycloakclient.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rago.keycloakclient.databinding.RowApBinding
import com.rago.keycloakclient.network.model.AuthenticatorProviders

class AuthenticatorProvidersAdapter :
    ListAdapter<AuthenticatorProviders, AuthenticatorProvidersAdapter.AuthenticatorProvidersViewHolder>(
        AuthenticatorProvidersComparator()
    ) {

    class AuthenticatorProvidersViewHolder(
        private val binding: RowApBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AuthenticatorProviders) {
            binding.apply {
                id.text = item.id
                displayName.text = item.displayName
                description.text = item.description
            }
        }

        companion object {
            fun create(parent: ViewGroup): AuthenticatorProvidersViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding: RowApBinding = RowApBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return AuthenticatorProvidersViewHolder(binding)
            }
        }
    }

    class AuthenticatorProvidersComparator : DiffUtil.ItemCallback<AuthenticatorProviders>() {
        override fun areItemsTheSame(
            oldItem: AuthenticatorProviders,
            newItem: AuthenticatorProviders
        ): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: AuthenticatorProviders,
            newItem: AuthenticatorProviders
        ): Boolean =
            oldItem == newItem

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AuthenticatorProvidersViewHolder =
        AuthenticatorProvidersViewHolder.create(parent)


    override fun onBindViewHolder(holder: AuthenticatorProvidersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}