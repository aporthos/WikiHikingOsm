package com.portes.wikihikingosm.feature.routes.helpers

import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.portes.wikihikingosm.core.common.setImage
import com.portes.wikihikingosm.core.designsystem.R
import com.portes.wikihikingosm.feature.routes.databinding.ModalBottomSheetRouteBinding

class BottomSheetCallbackHelper(
    private val context: Context,
    private val binding: ModalBottomSheetRouteBinding
) :
    BottomSheetBehavior.BottomSheetCallback() {
    override fun onStateChanged(bottomSheet: View, newState: Int) {
        when (newState) {
            BottomSheetBehavior.STATE_COLLAPSED -> binding.imageShowInfoRoute.setImage(
                context,
                R.drawable.arrow_up
            )

            BottomSheetBehavior.STATE_EXPANDED -> binding.imageShowInfoRoute.setImage(
                context,
                R.drawable.arrow_down
            )

            else -> Unit
        }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
}