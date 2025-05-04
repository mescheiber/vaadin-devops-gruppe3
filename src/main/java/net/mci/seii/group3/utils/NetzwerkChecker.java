package net.mci.seii.group3.utils;

public class NetzwerkChecker {

    public static boolean istImUniNetz(String ip) {
        return ip.startsWith("10.") || ip.startsWith("192.168.");
    }
}
