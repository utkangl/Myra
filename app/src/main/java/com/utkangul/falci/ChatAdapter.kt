package com.utkangul.falci

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.utkangul.falci.internalClasses.dataClasses.ChatMessage

class ChatAdapter(private val context: Context, private val messages: List<ChatMessage>) :
    BaseAdapter() {

    override fun getCount(): Int {
        return messages.size
    }

    override fun getItem(position: Int): Any {
        return messages[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private class ViewHolder(view: View, val messageTextViewRes: Int) {
        val messageTextView: TextView = view.findViewById(R.id.messageTextView)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val message = getItem(position) as ChatMessage
        val layoutInflater = LayoutInflater.from(context)

        val layoutRes = if (message.isMyMessage) {
            com.utkangul.falci.R.layout.item_my_message
        } else {
            com.utkangul.falci.R.layout.item_api_message
        }

        val view: View
        val viewHolder: com.utkangul.falci.ChatAdapter.ViewHolder

        if (convertView == null || (convertView.tag as com.utkangul.falci.ChatAdapter.ViewHolder).messageTextViewRes != layoutRes) {
            // convertView null ise veya görünüm türü değiştiyse yeni bir görünüm oluşturun
            view = layoutInflater.inflate(layoutRes, parent, false)
            viewHolder = com.utkangul.falci.ChatAdapter.ViewHolder(view, layoutRes)
            view.tag = viewHolder
        } else {
            // convertView yeniden kullanılabilirse, var olan görünümü alın
            view = convertView
            viewHolder = view.tag as com.utkangul.falci.ChatAdapter.ViewHolder
        }

        viewHolder.messageTextView.text = message.text

        return view
    }
}