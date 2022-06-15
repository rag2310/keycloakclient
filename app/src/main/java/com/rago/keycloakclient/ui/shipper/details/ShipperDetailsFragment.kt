package com.rago.keycloakclient.ui.shipper.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rago.keycloakclient.databinding.FragmentShipperDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShipperDetailsFragment : Fragment() {

    private val mViewModel: ShipperDetailsViewModel by viewModels()
    private val mArgs: ShipperDetailsFragmentArgs by navArgs()
    private lateinit var mBinding: FragmentShipperDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentShipperDetailsBinding.inflate(inflater)
        setUpUI()
        setUpClicks()
        setUpFlows()
        return mBinding.root
    }

    private fun setUpClicks() {
        mBinding.apply {
            delete.setOnClickListener {
                mViewModel.delete(mArgs.currentShipper)
                findNavController().navigate(ShipperDetailsFragmentDirections.actionShipperDetailsFragmentToShipperFragment())
            }

            update.setOnClickListener {
                mViewModel.update(mArgs.currentShipper)
                findNavController().navigate(ShipperDetailsFragmentDirections.actionShipperDetailsFragmentToShipperFragment())
            }
        }
    }

    private fun setUpUI() {
        mBinding.apply {
            titleShipper.text = mArgs.currentShipper.title
        }
    }

    private fun setUpFlows() {
        lifecycleScope.launchWhenCreated {
            launch {
                mViewModel.shipperDetailsUIState.collect(this@ShipperDetailsFragment::shipperDetailsUIState)
            }
        }
    }

    private fun shipperDetailsUIState(shipperDetailsUIState: ShipperDetailsUIState) {
        when (shipperDetailsUIState) {
            ShipperDetailsUIState.NotState -> {}
            is ShipperDetailsUIState.Permissions -> {
                setUpUIPermissions(shipperDetailsUIState)
            }
        }
    }

    private fun setUpUIPermissions(shipperDetailsUIState: ShipperDetailsUIState.Permissions) {
        mBinding.apply {
            if (shipperDetailsUIState.deleteShipper) {
                delete.visibility = View.VISIBLE
            }

            if (shipperDetailsUIState.updateShipper) {
                update.visibility = View.VISIBLE
            }
        }
    }
}