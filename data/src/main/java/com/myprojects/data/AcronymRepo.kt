package com.myprojects.data

interface AcronymRepo {

    suspend fun getFullForms(acronym: String = "") : ApiObjects.ApiResponse<List<ApiObjects.AcronymResponse>>
}