package com.myprojects.repository.repo

import com.myprojects.data.ApiObjects
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AcronymApi {

    @GET("acromine/dictionary.py")
    suspend fun getFullForms(@Query("sf") acronym: String): Response<List<ApiObjects.AcronymResponse>>
}