package ru.skillbranch.devintensive.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.visible
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.ui.custom.CircleImageView

class ChatAdapter(
    val listener: (ChatItem) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatItemViewHolder>() {

    companion object {
        private const val ARCHIVE_TYPE = 0
        private const val SINGLE_TYPE = 1
        private const val GROUP_TYPE = 2
    }

    var items: List<ChatItem> = listOf()

    fun updateData(data: List<ChatItem>) {
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

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SINGLE_TYPE -> SingleViewHolder(
                inflater.inflate(
                    R.layout.item_chat_single,
                    parent,
                    false
                )
            )
            GROUP_TYPE -> GroupViewHolder(inflater.inflate(R.layout.item_chat_group, parent, false))
            else -> GroupViewHolder(inflater.inflate(R.layout.item_chat_group, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemViewType(position: Int) = when (items[position].chatType) {
        ChatType.ARCHIVE -> ARCHIVE_TYPE
        ChatType.SINGLE -> SINGLE_TYPE
        ChatType.GROUP -> GROUP_TYPE
    }

    abstract inner class ChatItemViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        abstract fun bind(item: ChatItem, listener: (ChatItem) -> Unit)
    }

    inner class SingleViewHolder(
        override val containerView: View
    ) : ChatItemViewHolder(containerView), ItemTouchViewHolder {

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemCleared() {
            itemView.setBackgroundColor(Color.WHITE)
        }

        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
            if (item.avatar == null) {
                Glide.with(itemView)
                        .clear(itemView.findViewById<CircleImageView>(R.id.iv_avatar_single))
                itemView.findViewById<CircleImageView>(R.id.iv_avatar_single).initials =
                        item.initials
            } else {
                Glide.with(itemView)
                    .load(item.avatar)
                    .into(itemView.findViewById(R.id.iv_avatar_single))
            }

            itemView.findViewById<View>(R.id.sv_indicator).visible = item.isOnline
            with(itemView.findViewById<TextView>(R.id.tv_date_single)) {
                visible = item.lastMessageDate != null
                text = item.lastMessageDate
            }

            with(itemView.findViewById<TextView>(R.id.tv_counter_single)) {
                visible = item.messageCount > 0
                text = item.messageCount.toString()
            }

            itemView.findViewById<TextView>(R.id.tv_title_single).text = item.title
            itemView.findViewById<TextView>(R.id.tv_message_single).text = item.shortDescription

            itemView.setOnClickListener {
                listener.invoke(item)
            }
        }
    }

    inner class GroupViewHolder(
        override val containerView: View
    ) : ChatItemViewHolder(containerView), ItemTouchViewHolder {

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemCleared() {
            itemView.setBackgroundColor(Color.WHITE)
        }

        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
            itemView.findViewById<CircleImageView>(R.id.iv_avatar_group).initials = item.initials

            with(itemView.findViewById<TextView>(R.id.tv_date_group)) {
                visible = item.lastMessageDate != null
                text = item.lastMessageDate
            }

            with(itemView.findViewById<TextView>(R.id.tv_counter_group)) {
                visible = item.messageCount > 0
                text = item.messageCount.toString()
            }

            itemView.findViewById<TextView>(R.id.tv_title_group).text = item.title
            itemView.findViewById<TextView>(R.id.tv_message_group).text = item.shortDescription

            with(itemView.findViewById<TextView>(R.id.tv_message_author)) {
                visible = item.messageCount > 0
                text = item.author
            }

            itemView.setOnClickListener {
                listener.invoke(item)
            }
        }
    }
}
