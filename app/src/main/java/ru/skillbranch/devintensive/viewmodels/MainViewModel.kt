package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.TextMessage
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.repositories.ChatRepository

class MainViewModel : ViewModel() {

    private val query = mutableLiveData("")
    private val chatRepository: ChatRepository = ChatRepository
    private var unreadableMessages: MutableList<BaseMessage> = mutableListOf()

    // подписываемся на чаты, которые получаем из чат-репозитория, трансформируем их в ChatItem
    // и фильтруем
    private val chats = Transformations.map(chatRepository.loadChats()) { chats ->
        return@map chats
            .filter { !it.isArchived }
            .map { it.toChatItem() }
            .sortedBy { it.id.toInt() }
    }

    private val archiveChats = Transformations.map(chatRepository.loadChats()) { archiveChats ->
        unreadableMessages = mutableListOf()
        return@map archiveChats
            .filter { it.isArchived }
            .map {
                val messagesUnread =
                    it.messages.filter { m -> m is TextMessage && !m.isRead }.toMutableList()
                if (messagesUnread.size > 0)
                    unreadableMessages.addAll(messagesUnread)
                it
            }
            .sortedBy { it.id.toInt() }
    }

    fun getChatData(): LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()

        val filterFunction = {
            val queryStr = query.value!!
            val chats = getChatsWithArchiveItem()

            result.value = if (queryStr.isEmpty()) chats
            else chats.filter { it.title.contains(queryStr, true) }
        }
        with(result) {
            addSource(chats) { filterFunction.invoke() }
            addSource(archiveChats) { filterFunction.invoke() }
            addSource(query) { filterFunction.invoke() }
        }
        return result
    }

    private fun getChatsWithArchiveItem(): List<ChatItem> {
        if (archiveChats.value != null && archiveChats.value!!.isNotEmpty()) {
            return listOf(getArchiveChatItem()) + chats.value!!
        }
        return chats.value!!
    }

    private fun getArchiveChatItem(): ChatItem {
        unreadableMessages.sortBy { it.date }
        val chat = archiveChats.value!!.last().copy(messages = unreadableMessages)
        return chat.toArchiveChatItem()
    }

//    private fun loadChats(): List<ChatItem> {
//        val chats = chatRepository.loadChats()
//        return chats.map{it.toChatItem()}
//            .sortedBy { it.id.toInt() }
//    }
//
//    fun addItems() {
//        val newItems = DataGenerator.generateChatsWithOffset(chats.value!!.size, 5).map { it.toChatItem() }
//        val copy = chats.value!!.toMutableList()
//        copy.addAll(newItems)
//        chats.value = copy.sortedBy { it.id.toInt() }
//    }

    fun addToArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

    fun handleSearchQuery(text: String) {
        query.value = text
    }
}
