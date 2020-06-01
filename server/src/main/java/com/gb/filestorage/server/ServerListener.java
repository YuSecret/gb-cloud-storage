package com.gb.filestorage.server;

public interface ServerListener {
    void onServerMessage(Server server, String msg);
}