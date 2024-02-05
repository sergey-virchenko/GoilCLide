package com.virser.testapp.data.api

import com.virser.testapp.data.ApiResult
import retrofit2.Response

interface ApiCallHandler {

    suspend fun <DTO, DATA> makeCall(
        call: suspend () -> Response<DTO>,
        mapper: (dto: DTO) -> DATA,
    ): ApiResult<DATA>
}
