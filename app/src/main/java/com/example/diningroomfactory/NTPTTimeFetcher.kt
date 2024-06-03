package com.example.diningroomfactory

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.Date

object NTPTimeFetcher {

    private const val NTP_SERVER = "pool.ntp.org"
    private const val NTP_PORT = 123
    private const val NTP_PACKET_SIZE = 48

    fun getCurrentTime(): Date {
        val ntpData = ByteArray(NTP_PACKET_SIZE)
        ntpData[0] = 0x1B

        // Создаем сокет и подключаемся к NTP серверу
        val socket = DatagramSocket()
        socket.soTimeout = 5000 // Устанавливаем таймаут соединения

        val address = InetAddress.getByName(NTP_SERVER)
        val requestPacket = DatagramPacket(ntpData, ntpData.size, address, NTP_PORT)
        socket.send(requestPacket)

        // Получаем ответ от сервера
        val responsePacket = DatagramPacket(ntpData, ntpData.size)
        socket.receive(responsePacket)
        socket.close()

        // Вычисляем время из ответа
        val secondsSince1900 = getSecondsSince1900(ntpData)
        val epochMillis = (secondsSince1900 - 2208988800L) * 1000
        return Date(epochMillis)
    }

    private fun getSecondsSince1900(data: ByteArray): Long {
        var seconds = 0L
        for (i in 0..3) {
            seconds = seconds shl 8 or (data[36 + i].toLong() and 0xff)
        }
        return seconds
    }
}