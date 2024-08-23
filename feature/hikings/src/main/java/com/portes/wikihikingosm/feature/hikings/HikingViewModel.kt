package com.portes.wikihikingosm.feature.hikings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portes.wikihikingosm.core.common.domain.None
import com.portes.wikihikingosm.core.domain.usecases.DeleteHikeByIdUseCase
import com.portes.wikihikingosm.core.domain.usecases.GetHikesUseCase
import com.portes.wikihikingosm.core.domain.usecases.HikingRoutePref
import com.portes.wikihikingosm.core.models.Hike
import com.portes.wikihikingosm.core.models.ImportHiking
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ticofab.androidgpxparser.parser.GPXParser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class HikingViewModel @Inject constructor(
    getHikesUseCase: GetHikesUseCase,
    private val deleteHikeByIdUseCase: DeleteHikeByIdUseCase,
    private val hikingRoutePref: HikingRoutePref,
    private val parser: GPXParser
) : ViewModel() {

    private var _importHiking: ImportHiking? = null
    val importHiking: ImportHiking?
        get() = _importHiking

    fun setImport(inputStream: InputStream) {
        _importHiking = ImportHiking(gpx = parser.parse(inputStream))
    }

    val isStartHiking = flow {
        emit(hikingRoutePref.isStartHiking())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = 0,
    )

    val uiState: StateFlow<HikingUiState> = getHikesUseCase(None).map {
        HikingUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = HikingUiState.Loading,
        started = SharingStarted.WhileSubscribed()
    )

    fun deleteHike(idHike: Long) {
        viewModelScope.launch {
            deleteHikeByIdUseCase(DeleteHikeByIdUseCase.Params(idHike = idHike))
        }
    }

}

sealed interface HikingUiState {
    data class Success(
        val hikes: List<Hike>,
    ) : HikingUiState

    object Loading : HikingUiState
}