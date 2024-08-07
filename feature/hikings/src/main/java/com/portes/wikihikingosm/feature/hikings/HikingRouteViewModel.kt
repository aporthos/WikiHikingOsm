package com.portes.wikihikingosm.feature.hikings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portes.wikihikingosm.core.domain.usecases.AddRouteUseCase
import com.portes.wikihikingosm.core.models.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HikingRouteViewModel @Inject constructor(
    private val addRouteUseCase: AddRouteUseCase
) : ViewModel() {

    fun addRoute() {
        viewModelScope.launch {
            addRouteUseCase(AddRouteUseCase.Params(Route(latitude = 3232.0, longitude = 3232.0)))
        }
    }
}