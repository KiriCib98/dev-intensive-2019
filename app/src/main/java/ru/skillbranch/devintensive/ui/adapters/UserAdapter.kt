package ru.skillbranch.devintensive.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.visible
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.ui.custom.CircleImageView

class UserAdapter(
    val listener: (UserItem) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    var items = listOf<UserItem>()

    fun updateData(data: List<UserItem>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldPos: Int, newPos: Int) =
                items[oldPos].id == data[newPos].id

            override fun areContentsTheSame(oldPos: Int, newPos: Int) =
                items[oldPos] == data[newPos]

            override fun getOldListSize() = items.size
            override fun getNewListSize() = data.size
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = data
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_user_list, parent, false)
        return UserViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    class UserViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(user: UserItem, listener: (UserItem) -> Unit) {
            if (user.avatar != null) {
                Glide.with(itemView)
                        .load(user.avatar)
                        .into(itemView.findViewById<CircleImageView>(R.id.iv_avatar_user))
            } else {
                Glide.with(itemView)
                        .clear(itemView.findViewById<CircleImageView>(R.id.iv_avatar_user))
                itemView.findViewById<CircleImageView>(R.id.iv_avatar_user).initials = user.initials
            }
            itemView.findViewById<View>(R.id.sv_indicator).visible = user.isOnline
            itemView.findViewById<TextView>(R.id.tv_user_name).text = user.fullName
            itemView.findViewById<TextView>(R.id.tv_last_activity).text = user.lastActivity
            itemView.findViewById<ImageView>(R.id.iv_selected).visible = user.isSelected
            itemView.setOnClickListener { listener.invoke(user) }
        }
    }
}
