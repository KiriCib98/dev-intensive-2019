package ru.skillbranch.devintensive.repositories

import androidx.lifecycle.MutableLiveData
import ru.skillbranch.devintensive.data.managers.CacheManager
import ru.skillbranch.devintensive.models.data.Chat

object ChatRepository {

    private val chats = CacheManager.loadChats()

    fun loadChats(): MutableLiveData<List<Chat>> {
        return chats // DataGenerator.generateChats(10)
    }

    fun update(chat: Chat) {
        val index = chats.value!!.indexOfFirst { it.id == chat.id }
        if (index == -1) return

        val copy = chats.value!!.toMutableList()
        copy[index] = chat
        chats.value = copy
    }

    fun find(chatId: String): Chat? {
        val index = chats.value!!.indexOfFirst { it.id == chatId }
        return chats.value!!.getOrNull(index)
    }
}
