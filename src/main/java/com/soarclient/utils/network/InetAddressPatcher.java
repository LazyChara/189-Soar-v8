package com.soarclient.utils.network;

import com.google.common.net.InetAddresses;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressPatcher {
	public static InetAddress patch(String hostName, InetAddress addr) throws UnknownHostException {
		if (InetAddresses.isInetAddress(hostName)) {
			InetAddress patched = InetAddress.getByAddress(addr.getHostAddress(), addr.getAddress());
			addr = patched;
		}
		return addr;
	}
}