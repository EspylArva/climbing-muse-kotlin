package com.iteration.climbingmuse.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.iteration.climbingmuse.R
import com.iteration.climbingmuse.databinding.PermissionCardViewBinding

class PermissionCardView(context: Context, attrs: AttributeSet) :
    CardView(context, attrs) {

    init {
        inflate(context, R.layout.permission_card_view, this)

        val title : MaterialTextView = findViewById(R.id.card_title)
        val icon : ImageView = findViewById(R.id.card_logo)
        val description : MaterialTextView = findViewById(R.id.card_description)
        val authorize : MaterialButton = findViewById(R.id.card_authorize)
        val revoke : MaterialButton = findViewById(R.id.card_revoke)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.PermissionCardView)

        title.text = attributes.getString(R.styleable.PermissionCardView_title)
        icon.setImageResource(attributes.getResourceId(R.styleable.PermissionCardView_icon, R.drawable.ic_baseline_lock_24))

        attributes.recycle()
    }


}