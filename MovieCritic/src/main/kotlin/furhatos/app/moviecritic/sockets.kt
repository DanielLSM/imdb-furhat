package furhatos.app.moviecritic

import org.zeromq.ZMQ

val context: ZMQ.Context = ZMQ.context(1)

fun getConnectedSocketSUB(socketType: Int, port: String, receiveTimeout: Int = -1): ZMQ.Socket {
    val socket = context.socket(socketType)
    if (receiveTimeout >= 0) socket.receiveTimeOut = receiveTimeout
    socket.subscribe("")
    socket.connect(port)
    return socket
}

fun getConnectedSocketPUB(socketType: Int, port: String, receiveTimeout: Int = -1): ZMQ.Socket {
    val socket = context.socket(socketType)
    if (receiveTimeout >= 0) socket.receiveTimeOut = receiveTimeout
    socket.connect(port)
    return socket
}

