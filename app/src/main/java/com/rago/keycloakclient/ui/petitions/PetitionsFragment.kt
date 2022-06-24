package com.rago.keycloakclient.ui.petitions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rago.keycloakclient.adapters.AuthenticatorProvidersAdapter
import com.rago.keycloakclient.databinding.FragmentPetitionsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PetitionsFragment : Fragment() {

    private lateinit var mBinding: FragmentPetitionsBinding
    private val mViewModel: PetitionsViewModel by viewModels()
    private lateinit var mAdapter: AuthenticatorProvidersAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPetitionsBinding.inflate(inflater)
        mBinding.rvAP.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = AuthenticatorProvidersAdapter()
        mBinding.rvAP.adapter = mAdapter
        setUpFlows()
        return mBinding.root
    }

    private fun setUpFlows() {
        lifecycleScope.launchWhenCreated {
            launch {
                mViewModel.list.collect {
                    if (it.isNotEmpty()) {
                        mAdapter.submitList(it)
                    }
                }
            }
        }
    }
}