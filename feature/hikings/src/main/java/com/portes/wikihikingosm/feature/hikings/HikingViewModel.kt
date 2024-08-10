package com.portes.wikihikingosm.feature.hikings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portes.wikihikingosm.core.common.domain.None
import com.portes.wikihikingosm.core.domain.usecases.AddHikeUseCase
import com.portes.wikihikingosm.core.domain.usecases.GetHikesUseCase
import com.portes.wikihikingosm.core.models.Hike
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HikingViewModel @Inject constructor(
    getHikesUseCase: GetHikesUseCase,
    private val addHikeUseCase: AddHikeUseCase,
) : ViewModel() {

    init {
        addHike()
    }

    val uiState: StateFlow<HikingUiState> = getHikesUseCase(None).map {
        HikingUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = HikingUiState.Loading,
        started = SharingStarted.WhileSubscribed()
    )

    private fun addHike() {
        viewModelScope.launch {
            addHikeUseCase(AddHikeUseCase.Params(Hike(name = "izta.gpx")))
            addHikeUseCase(AddHikeUseCase.Params(Hike(name = "barranca.gpx")))
            addHikeUseCase(AddHikeUseCase.Params(Hike(name = "cuervo.gpx")))
            addHikeUseCase(AddHikeUseCase.Params(Hike(name = "cuervo2.gpx")))
            addHikeUseCase(AddHikeUseCase.Params(Hike(name = "pena.gpx")))
            addHikeUseCase(AddHikeUseCase.Params(Hike(name = "pena2.gpx")))
            addHikeUseCase(AddHikeUseCase.Params(Hike(name = "iztaccihuatl_cumbre.gpx")))
            addHikeUseCase(AddHikeUseCase.Params(Hike(name = "cumbre_iztaccihuatl.gpx")))
        }
    }
}

sealed interface HikingUiState {
    data class Success(
        val hikes: List<Hike>,
    ) : HikingUiState

    object Loading : HikingUiState
}