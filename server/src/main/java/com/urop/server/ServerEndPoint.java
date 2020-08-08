package com.urop.server;

import com.urop.common.Connection;

public interface ServerEndPoint {
    void connected(Connection lc);

    void disconnected(Connection lc);
}
