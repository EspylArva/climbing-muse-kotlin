package com.iteration.climbingmuse.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.iteration.climbingmuse.databinding.PermissionCardViewBinding

class PermissionCardAdapter(val permissions: List<PermissionCardInfo>) : RecyclerView.Adapter<PermissionCardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PermissionCardViewBinding.inflate(inflater)
        return PermissionCardViewHolder(binding)
//            .apply {binding.root.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)}
    }

    override fun onBindViewHolder(holder: PermissionCardViewHolder, position: Int) {
        // Get the infos
        val cardInfo = permissions[position]

        holder.setTitle(cardInfo.title)
        holder.setDescription(cardInfo.description)
        holder.setIcon(cardInfo.icon)

        // OBserve livedata?
        holder.setButtonsClickable(cardInfo.authorized.value!!)
        holder.setAuthorizeOnClickListener { cardInfo.getPermission() }
        holder.setRevokeOnClickListener { cardInfo.revokePermission() }
    }

    override fun getItemCount(): Int {
        // This should be a fixed size, there is a finite number of permissions to require
        return 2
    }

}

class PermissionCardViewHolder(private val binding: PermissionCardViewBinding) : RecyclerView.ViewHolder(binding.root) {
    fun setTitle(title: String) {
        binding.cardTitle.text = title
    }

    fun setDescription(description: String) {
        binding.cardDescription.text = description
    }

    fun setIcon(ref: Int) {
        binding.cardLogo.setImageResource(ref)
    }

    fun setButtonsClickable(isAuthorized: Boolean) {
        binding.cardAuthorize.isActivated = !isAuthorized
        binding.cardRevoke.isActivated = isAuthorized
    }

    fun setAuthorizeOnClickListener(listener: View.OnClickListener) {
        binding.cardAuthorize.setOnClickListener(listener)
    }

    fun setRevokeOnClickListener(listener: View.OnClickListener) {
        binding.cardRevoke.setOnClickListener(listener)
    }
}
