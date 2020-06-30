package com.thegalos.petbook.objects;

public class User {

    String id;
    String userName;
    Long time;
    String lastMessage;
    String isMessageSeen;

    public User() {
    }

    public User( String userName, Long time , String id , String lastMessage , String isMessageSeen) {

        this.userName = userName;
        this.time = time;
        this.id = id;
        this.lastMessage = lastMessage;
        this.isMessageSeen = isMessageSeen;

    }

    public String getIsMessageSeen() {
        return isMessageSeen;
    }

    public void setMessageSeen(String messageSeen) {
        isMessageSeen = messageSeen;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


}
