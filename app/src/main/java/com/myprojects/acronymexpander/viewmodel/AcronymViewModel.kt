package com.myprojects.acronymexpander.viewmodel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myprojects.acronymexpander.BR
import com.myprojects.acronymexpander.R
import com.myprojects.data.AcronymRepo
import com.myprojects.data.ApiObjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AcronymViewModel @Inject constructor(private val acronymRepo: AcronymRepo): ViewModel(){

    private val scope = CoroutineScope(Dispatchers.IO)
    val acronymText = AcronymText()
    private val mOnError = MutableLiveData<Int>()
    val onError get() = mOnError
    private val mFullForms = MutableLiveData<List<ApiObjects.FullForm>>()
    val fullForms get() = mFullForms

    fun loadFullForms(){
        val text = acronymText.text.trim()
        if(text.isEmpty()){
            mOnError.value = R.string.no_acronym_entered
            return
        }
        acronymText.text = text
        scope.launch {
            val response = acronymRepo.getFullForms(acronymText.text)
            val fullForms = response.data?.firstOrNull()?.fullForms ?: emptyList()
            mFullForms.postValue(fullForms)
            if(fullForms.isEmpty()){
                mOnError.postValue(response.errorMessage)
            }
        }
    }

    fun clearText(){
        acronymText.text = EMPTY_STR
        mFullForms.value = emptyList()
    }

    fun clearError(){
        mOnError.value = 0
    }

    companion object{
        const val EMPTY_STR = ""
    }

    inner class AcronymText : BaseObservable() {
        @get: Bindable
        var text: String = EMPTY_STR
            set(value){
                field = value
                notifyPropertyChanged(BR.text)
            }
    }
}