package org.winplus.serial.utils;

interface ISerialPortService {
	void open();
	int read(out byte[] buffer, int byteOffset, int byteCount);
	void write(in byte[] buffer, int byteOffset, int byteCount);
	void close();
}