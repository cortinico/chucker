package com.chuckerteam.chucker.internal.ui.transaction

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

internal class TransactionPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment = when (position) {
        OVERVIEW_SCREEN_POSITION -> TransactionOverviewFragment()
        REQUEST_SCREEN_POSITION -> TransactionPayloadFragment.newInstance(TransactionPayloadFragment.TYPE_REQUEST)
        RESPONSE_SCREEN_POSITION -> TransactionPayloadFragment.newInstance(TransactionPayloadFragment.TYPE_RESPONSE)
        else -> throw IllegalStateException("Invalid adapter position")
    }

    override fun getItemCount(): Int = TRANSACTION_SCREENS_NUMBER

    companion object {
        internal const val TRANSACTION_SCREEN_OFFSCREEN_LIMIT = 2
        internal const val TRANSACTION_SCREENS_NUMBER = 3

        internal const val OVERVIEW_SCREEN_POSITION = 0
        internal const val REQUEST_SCREEN_POSITION = 1
        internal const val RESPONSE_SCREEN_POSITION = 2
    }
}
