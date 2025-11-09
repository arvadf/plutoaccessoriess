package com.example.plutoaccessories.utils

import android.app.Activity
import android.content.Intent
import android.widget.TextView
import com.example.plutoaccessories.R
import com.example.plutoaccessories.activities.MainActivity
import com.example.plutoaccessories.activities.CartActivity
import com.example.plutoaccessories.activities.ProfileActivity

object BottomNav {

	fun setup(activity: Activity, selected: Selected) {
		val navHome: TextView = activity.findViewById(R.id.nav_home)
		val navCart: TextView = activity.findViewById(R.id.nav_cart)
		val navProfile: TextView = activity.findViewById(R.id.nav_profile)

		// highlight selected
		when (selected) {
			Selected.HOME -> navHome.setTextColor(activity.getColor(R.color.pluto_gold))
			Selected.CART -> navCart.setTextColor(activity.getColor(R.color.pluto_gold))
			Selected.PROFILE -> navProfile.setTextColor(activity.getColor(R.color.pluto_gold))
		}

		navHome.setOnClickListener {
			if (activity !is MainActivity) {
				activity.startActivity(Intent(activity, MainActivity::class.java))
			}
		}
		navCart.setOnClickListener {
			if (activity !is CartActivity) {
				activity.startActivity(Intent(activity, CartActivity::class.java))
			}
		}
		navProfile.setOnClickListener {
			if (activity !is ProfileActivity) {
				activity.startActivity(Intent(activity, ProfileActivity::class.java))
			}
		}
	}

	enum class Selected { HOME, CART, PROFILE }
}


