package com.rago.keycloakclient.ui.booking.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rago.keycloakclient.adapters.BookingAdapter
import com.rago.keycloakclient.databinding.FragmentBookingBinding
import com.rago.keycloakclient.db.entities.Booking
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookingFragment : Fragment() {

    private lateinit var mBinding: FragmentBookingBinding
    private val mViewModel: BookingViewModel by viewModels()
    private var mAdapter: BookingAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentBookingBinding.inflate(inflater)
        setUpFlows()
        return mBinding.root
    }

    private fun setUpFlows() {
        lifecycleScope.launchWhenCreated {
            launch {
                mViewModel.bookingUIState.collect(this@BookingFragment::bookingUIState)
            }
            launch {
                mViewModel.allBooking.collect(this@BookingFragment::setListBooking)
            }
        }
    }

    private fun setListBooking(list: List<Booking>) {
        if (list.isNotEmpty()) {
            mAdapter?.submitList(list)
        }
    }

    private fun bookingUIState(state: BookingUIState) {
        when (state) {
            BookingUIState.NotState -> {}
            is BookingUIState.Permissions -> {
                setUpUIPermissions(state)
            }
        }
    }

    private fun setUpUIPermissions(permissions: BookingUIState.Permissions) {
        mBinding.apply {
            if (permissions.createdBooking) {
                addBooking.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        mViewModel.insert()
                    }
                }
            }

            val onClick: (Booking) -> Unit = if (permissions.updateOrDeleteBooking) {
                {
                    findNavController().navigate(
                        BookingFragmentDirections.actionBookingFragmentToBookingDetailsFragment(
                            it
                        )
                    )
                }
            } else {
                {}
            }

            if (mAdapter == null) {
                mAdapter = BookingAdapter(onClick)
            }

            rvBooking.layoutManager = LinearLayoutManager(requireContext())
            rvBooking.adapter = mAdapter
        }
    }
}