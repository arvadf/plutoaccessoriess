package com.example.plutoaccessories.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plutoaccessories.R
import com.example.plutoaccessories.models.UserModel

class ManageUsersAdapter(private val userList: List<UserModel>) :
    RecyclerView.Adapter<ManageUsersAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNamaUser)
        val tvEmail: TextView = view.findViewById(R.id.tvEmailUser)
        val tvRole: TextView = view.findViewById(R.id.tvRoleUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_manage, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.tvNama.text = user.nama
        holder.tvEmail.text = user.email
        holder.tvRole.text = user.role
    }

    override fun getItemCount(): Int = userList.size
}