package com.gb.filestorage.server;

public interface ServerListener {
    void onServerMessage(ServerController serverController, String msg);
}