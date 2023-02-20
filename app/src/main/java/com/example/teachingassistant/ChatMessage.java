package com.example.teachingassistant;

public class ChatMessage {


    public String sideMessage;
    public String userMessage;
    public String contentMessage;
    public String dateMessage;
    public String timeMessage;

    public ChatMessage(String sideMessage, String userMessage, String contentMessage, String dateMessage, String timeMessage) {
        this.sideMessage = sideMessage;
        this.userMessage = userMessage;
        this.contentMessage = contentMessage;
        this.dateMessage = dateMessage;
        this.timeMessage = timeMessage;
    }

    public String getSideMessage() {
        return sideMessage;
    }

    public void setSideMessage(String sideMessage) {
        this.sideMessage = sideMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getContentMessage() {
        return contentMessage;
    }

    public void setContentMessage(String contentMessage) {
        this.contentMessage = contentMessage;
    }

    public String getDateMessage() {
        return dateMessage;
    }

    public void setDateMessage(String dateMessage) {
        this.dateMessage = dateMessage;
    }

    public String getTimeMessage() {
        return timeMessage;
    }

    public void setTimeMessage(String timeMessage) {
        this.timeMessage = timeMessage;
    }
}
