package com.myprojects.acronymexpander.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.myprojects.acronymexpander.R
import com.myprojects.acronymexpander.databinding.FullFormItemBinding
import com.myprojects.data.ApiObjects

class FullFormAdapter(private var fullForms: List<ApiObjects.FullForm>): RecyclerView.Adapter<FullFormAdapter.FullFormViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullFormViewHolder {
        val binding = DataBindingUtil.inflate<FullFormItemBinding>(LayoutInflater.from(parent.context),R.layout.full_form_item,parent,false)
        return FullFormViewHolder(binding)
    }

    override fun getItemCount() = fullForms.size

    override fun onBindViewHolder(holder: FullFormViewHolder, position: Int) {
        holder.bind(fullForms[position])
    }

    fun refresh(fullForms: List<ApiObjects.FullForm>){
        this.fullForms = fullForms
        notifyDataSetChanged()
    }

    inner class FullFormViewHolder(private val binding: FullFormItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fullForm: ApiObjects.FullForm){
            binding.fullform = fullForm
        }
    }
}