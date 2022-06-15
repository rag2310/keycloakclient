package com.rago.keycloakclient.ui.shipper.shipper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rago.keycloakclient.adapters.ShipperAdapter
import com.rago.keycloakclient.databinding.FragmentShipperBinding
import com.rago.keycloakclient.db.entities.Shipper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ShipperFragment : Fragment() {

    private lateinit var mBinding: FragmentShipperBinding
    private val mViewModel: ShipperViewModel by viewModels()
    private  var mAdapter: ShipperAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentShipperBinding.inflate(inflater)
        setUpFlows()
        return mBinding.root
    }

    private fun setUpFlows() {
        lifecycleScope.launchWhenCreated {
            launch {
                mViewModel.allShipper.collect(::setListShipper)
            }
            launch {
                mViewModel.shipperUIState.collect(this@ShipperFragment::shipperUIState)
            }
        }
    }

    private fun shipperUIState(shipperUIState: ShipperUIState) {
        when (shipperUIState) {
            ShipperUIState.NotState -> {}
            is ShipperUIState.Permissions -> {
                setUIPermissions(shipperUIState)
            }
        }
    }

    private fun setUIPermissions(value: ShipperUIState.Permissions) {
        mBinding.apply {
            val onClickDetails: (Shipper) -> Unit = if (value.deleteOrUpdateShipper) {
                {
                    findNavController().navigate(
                        ShipperFragmentDirections.actionShipperFragmentToShipperDetailsFragment(
                            it
                        )
                    )
                }
            } else {
                {}
            }

            if (value.createdShipper) {
                addShipper.visibility = View.VISIBLE
                addShipper.setOnClickListener {
                    mViewModel.insert()
                }
            }

            rvShipper.layoutManager = LinearLayoutManager(requireContext())
            if(mAdapter == null)
                mAdapter = ShipperAdapter(onClickDetails)
            rvShipper.adapter = mAdapter

        }
    }

    private fun setListShipper(list: List<Shipper>) {
        if (list.isNotEmpty()) {
            mAdapter?.submitList(list)
        }
    }
}