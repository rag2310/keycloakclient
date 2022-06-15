package com.rago.keycloakclient.ui.token

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rago.keycloakclient.databinding.FragmentTokenBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TokenFragment : Fragment() {

    private lateinit var mBinding: FragmentTokenBinding
    private val mViewModel: TokenViewModel by viewModels()

    private val authentication =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            mViewModel.signOut()
            findNavController().navigate(TokenFragmentDirections.actionTokenFragmentToLoginFragment())
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentTokenBinding.inflate(inflater)
        setUpFlow()
        setUpClicks()
        return mBinding.root
    }

    private fun setUpClicks() {
        mBinding.apply {
            refresh.setOnClickListener {
                mViewModel.refreshToken()
            }
            logout.setOnClickListener {
                mViewModel.logOut {
                    authentication.launch(it)
                }
            }
            shipper.setOnClickListener {
                findNavController().navigate(TokenFragmentDirections.actionTokenFragmentToShipperFragment())
            }
        }
    }

    private fun setUpFlow() {
        lifecycleScope.launchWhenCreated {
            launch {
                mViewModel.email.collect(::email)
            }

            launch {
                mViewModel.name.collect(::name)
            }

            launch {
                mViewModel.exp.collect(::exp)
            }
            launch {
                mViewModel.tokenUIState.collect(::tokenUiState)
            }
        }
    }

    private fun tokenUiState(value: TokenUIState) {
        when (value) {
            TokenUIState.NotState -> {}
            is TokenUIState.Permissions -> {
                setUpUIPermissions(value)
            }
        }
    }

    private fun setUpUIPermissions(value: TokenUIState.Permissions) {
        mBinding.apply {
            if (value.viewBooking) {
                booking.visibility = View.VISIBLE
            }
            if (value.viewShipper) {
                shipper.visibility = View.VISIBLE
            }
        }
    }

    private fun email(value: String) {
        mBinding.email.text = value
    }

    private fun exp(value: String) {
        mBinding.exp.text = value
    }

    private fun name(value: String) {
        mBinding.name.text = value
    }
}