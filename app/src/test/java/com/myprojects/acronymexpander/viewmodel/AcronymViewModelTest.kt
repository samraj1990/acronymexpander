package com.myprojects.acronymexpander.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.myprojects.acronymexpander.R
import com.myprojects.data.AcronymRepo
import com.myprojects.data.ApiObjects
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class AcronymViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
    private lateinit var viewModel: AcronymViewModel
    private val acronymRepo: AcronymRepo = mock()
    private val fullFormsObserver: Observer<List<ApiObjects.FullForm>> = mock()
    private val errorObserver: Observer<Int> = mock()


    @Before
    fun setUp(){
        viewModel = AcronymViewModel(acronymRepo)
        viewModel.onError.observeForever(errorObserver)
        viewModel.fullForms.observeForever(fullFormsObserver)
    }

    @Test
    fun tearDown(){
        viewModel.onError.removeObserver(errorObserver)
        viewModel.fullForms.removeObserver(fullFormsObserver)
    }

    @Test
    fun load_full_form_without_acronym(){
        testCoroutineRule.runBlockingTest {
            viewModel.loadFullForms()
            Mockito.verify(errorObserver).onChanged(R.string.no_acronym_entered)
            Mockito.verify(acronymRepo, never()).getFullForms(any())
        }
    }

    @Test
    fun load_full_form_with_acronym(){
        val acronym = "USA"
        val fullForm = ApiObjects.FullForm("United States of America","450","1950")
        val apiResponse = ApiObjects.ApiResponse(listOf(ApiObjects.AcronymResponse(acronym,listOf(fullForm))))
        viewModel.acronymText.text = acronym
        testCoroutineRule.runBlockingTest {
            Mockito.`when`(acronymRepo.getFullForms(acronym)).thenReturn(apiResponse)
            viewModel.loadFullForms()
            Mockito.verify(errorObserver, never()).onChanged(any())
            val captor: KArgumentCaptor<List<ApiObjects.FullForm>> = argumentCaptor()
            Mockito.verify(fullFormsObserver, timeout(500)).onChanged(captor.capture())
            val fullForms = captor.firstValue
            Assert.assertNotNull(fullForms.firstOrNull())
            fullForms.firstOrNull()?.let {
                Assert.assertEquals(fullForm.name,it.name)
                Assert.assertEquals(fullForm.frequency,it.frequency)
                Assert.assertEquals(fullForm.since,it.since)
            }
        }
    }

    @Test
    fun load_full_form_with_acronym_error(){
        val acronym = "USA"
        val apiResponse = ApiObjects.ApiResponse(emptyList<ApiObjects.AcronymResponse>(),1234)
        viewModel.acronymText.text = acronym
        testCoroutineRule.runBlockingTest {
            Mockito.`when`(acronymRepo.getFullForms(acronym)).thenReturn(apiResponse)
            viewModel.loadFullForms()
            Mockito.verify(fullFormsObserver, timeout(500)).onChanged(eq(emptyList()))
            Mockito.verify(errorObserver).onChanged(apiResponse.errorMessage)
        }
    }

    @Test
    fun clear_text_test(){
        val acronym = "USA"
        viewModel.acronymText.text = acronym
        viewModel.clearText()
        Assert.assertEquals("",viewModel.acronymText.text)
        Assert.assertTrue(viewModel.fullForms.value?.isEmpty() == true)
    }




}