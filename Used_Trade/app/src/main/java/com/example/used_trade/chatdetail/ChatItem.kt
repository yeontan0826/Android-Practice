package com.example.used_trade.chatdetail

data class ChatItem(
    val sendId: String,
    val message: String
) {
    constructor(): this("", "")
}
