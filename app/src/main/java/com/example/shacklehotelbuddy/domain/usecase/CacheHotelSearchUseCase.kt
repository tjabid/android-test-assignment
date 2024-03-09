package com.example.shacklehotelbuddy.domain.usecase

import com.example.shacklehotelbuddy.data.repository.HotelRepository
import com.example.shacklehotelbuddy.domain.model.HotelSearch
import javax.inject.Inject

class CacheHotelSearchUseCase @Inject constructor(private val repository: HotelRepository) {

    suspend operator fun invoke(hotelSearch: HotelSearch) {
        repository.cacheHotelSearch(hotelSearch)
    }
}