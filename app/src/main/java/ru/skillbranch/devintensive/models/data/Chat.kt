package ru.skillbranch.devintensive.models.data

import androidx.annotation.VisibleForTesting
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.ImageMessage
import ru.skillbranch.devintensive.models.TextMessage
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class Chat(
    val id: String,
    val title: String,
    val members: List<User> = listOf(),
    var messages: MutableList<BaseMessage> = mutableListOf(),
    var isArchived: Boolean = false
) {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun unreadableMessageCount(): Int {
        return if (messages.size == 0) 0 else messages.filter { it is TextMessage && !it.isRead }.size
    }

    /**
     * Возвращает дату последнего сообщения
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageDate(): Date? {
        return if (messages.size == 0) {
            null
        } else {
            messages.sortBy { it.date }
            messages.last().date
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun lastMessageShort(): Pair<String, String?> { //= when(val lastMessage = messages.lastOrNull()){
        return if (messages.size == 0) {
            "Сообщений еще нет" to null //"@John Doe"
        } else {
            messages.sortBy { it.date }
            return when (val lastMessage = messages.last()) {
                is TextMessage -> lastMessage.text.orEmpty() to "${lastMessage.from.firstName}"
                is ImageMessage -> "${lastMessage.from.firstName.orEmpty()} отправил фото" to "${lastMessage.from.firstName}"
                else -> "" to ""
            }
        }
    }

    private fun isSingle(): Boolean = members.size == 1

    fun toChatItem(): ChatItem {
        return if (isSingle()) {
            val user = members.first()
            ChatItem(
                id,
                user.avatar,
                Utils.toInitials(user.firstName, user.lastName) ?: "??",
                "@${user.firstName ?: ""} ${user.lastName ?: ""}",
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                user.isOnline
            )
        } else {
            ChatItem(
                id,
                null,
                "",
                title,
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                false,
                ChatType.GROUP,
                lastMessageShort().second
            )
        }
    }

    /**
     * Формирование 0-го элемента списка чатов "Архив чатов"
     */
    fun toArchiveChatItem(): ChatItem {
        val lastMessageShortInfo = lastMessageShort()
        return ChatItem(
            id,
            null,
            "",
            "Архив чатов",
            lastMessageShortInfo.first,
            messages.size,
            lastMessageDate()?.shortFormat(),
            false,
            ChatType.ARCHIVE,
            when {
                messages.size > 0 -> lastMessageShortInfo.second
                members.size == 1 -> "${members.first().firstName}"
                else -> ""
            }
        )
    }
}

enum class ChatType {
    SINGLE,
    GROUP,
    ARCHIVE
}



