package com.whenwhat.attachmentfilechooser.other


open class ObservableList<T>(private val list: MutableList<T> = mutableListOf()) :
    MutableList<T> by list {

    private val subscribers = mutableListOf<(oldList: List<T>, newList: List<T>) -> Unit>()

    fun subscribeOnChange(action: (oldList: List<T>, newList: List<T>) -> Unit): Subscription {
        subscribers += action
        return object : Subscription {
            override fun unSubscribe() {
                subscribers -= action
            }
        }
    }

    open fun unSubscribe(action: (oldList: List<T>, newList: List<T>) -> Unit) {
        subscribers -= action
    }

    open fun clearSubscribers() {
        subscribers.clear()
    }

    fun transaction(action: MutableList<T>.() -> Unit) = change(action)

    override fun set(index: Int, element: T): T = change {
        set(index, element)
    }

    override fun remove(element: T): Boolean = change {
        remove(element)
    }

    override fun removeAll(elements: Collection<T>): Boolean = change {
        removeAll(elements)
    }

    override fun removeAt(index: Int): T = change {
        removeAt(index)
    }

    override fun add(element: T): Boolean = change {
        add(element)
    }

    override fun add(index: Int, element: T) = change {
        add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean = change {
        addAll(index, elements)
    }

    override fun addAll(elements: Collection<T>): Boolean = change {
        addAll(elements)
    }

    override fun clear() = change {
        clear()
    }

    fun replaceOn(newList: List<T>) = transaction {
        clear()
        addAll(newList)
    }

    private fun <R> change(changer: MutableList<T>.() -> R): R {
        val oldList = ArrayList(list)
        return changer(list).apply {
            subscribers.forEach { it.invoke(oldList, list) }
        }
    }
}