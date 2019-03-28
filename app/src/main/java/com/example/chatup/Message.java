package com.example.chatup;

class Message {

    private String messageText;
    private boolean published;

    Message(String messageText, boolean published) {
        this.messageText = messageText;
        this.published = published;
    }

    String getMessageText() {
        return messageText;
    }

    public boolean isPublished() {
        return published;
    }
}
