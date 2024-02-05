package com.virser.testapp.data.api

import com.virser.testapp.data.ApiResult
import retrofit2.Response

class CommonApiCallHandler : ApiCallHandler {

    override suspend fun <DTO, DATA> makeCall(
        call: suspend () -> Response<DTO>,
        mapper: (dto: DTO) -> DATA,
    ): ApiResult<DATA> {
        return runCatching {
            call.invoke().let { response ->
                response.body().takeIf { response.isSuccessful }?.let {
                    runCatching {
                        ApiResult.Success(mapper(it))
                    }.getOrElse {
                        ApiResult.Error(it)
                    }
                } ?: run {
                    ApiResult.Error(
                        Exception(
                            "Request failed with ${response.code()}, body ${
                                response.errorBody()?.string()
                            }"
                        )
                    )
                }
            }
        }.getOrElse {
            ApiResult.Error(it)
        }
    }
}
