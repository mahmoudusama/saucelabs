package com.sauceLabs.common.utils.helpers;

import com.sauceLabs.common.utils.logs.MyLogger;
import org.apache.logging.log4j.core.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * used for checking network connections
 *
 * @author MahmoudOsama
 */
public class Connections {
    private static HashMap<String, Boolean> holder = new HashMap<>();
    private static Logger log = new MyLogger().getLogger();


    /**
     * Checks if a connection can be established to a specific host and port.
     *
     * This method attempts to create a socket connection to the given host and port number.
     * It returns a boolean indicating whether the connection was successful. Detailed log
     * messages provide information on the connection status, and any exceptions encountered
     * during the connection attempt are logged for troubleshooting purposes.
     *
     * @param hostName   The name or IP address of the host to connect to.
     * @param portNumber The port number to attempt the connection on.
     * @return Boolean indicating whether the connection to the host and port was successful.
     */
    private Boolean checkHostPortConn(String hostName, int portNumber) {
        Boolean isConnected = false;
        log.debug("Checking if there's a connection to host: {}, on port: {}", hostName, portNumber);

        try (Socket socket = new Socket(hostName, portNumber)) {
            isConnected = socket.isConnected();
            log.info("Connection to host: {} on port: {} was successful: {}", hostName, portNumber, isConnected);
        } catch (ConnectException e) {
            log.error("Connection refused. Failed to connect to host: {} on port: {}", hostName, portNumber, e);
        } catch (UnknownHostException e) {
            log.error("Unknown host: {}. Failed to connect on port: {}", hostName, portNumber, e);
        } catch (IOException e) {
            log.error("I/O error while attempting to connect to host: {} on port: {}", hostName, portNumber, e);
        }

        log.debug("Returning connection status: {} for host: {} on port: {}", isConnected, hostName, portNumber);
        return isConnected;
    }


    /**
     * Pings a host to check if it is reachable within a specified timeout.
     *
     * This method attempts to ping the given host by resolving its IP address and
     * sending a simple request to check if it's reachable. It logs detailed messages
     * about the pinging process, and any exceptions encountered are logged for
     * troubleshooting purposes.
     *
     * @param hostName The name or IP address of the host to ping.
     * @return Boolean indicating whether the host is reachable within the timeout period.
     */
    private boolean ping(String hostName) {
        boolean isConnected = false;
        log.debug("Pinging host: {}", hostName);
        try {
            isConnected = InetAddress.getByName(hostName).isReachable(3000);
            log.info("Ping to host: {} was successful: {}", hostName, isConnected);
        } catch (ConnectException e) {
            log.error("Connection refused during ping. Host: {}", hostName, e);
        } catch (UnknownHostException e) {
            log.error("Unknown host: {}. Unable to ping.", hostName, e);
        } catch (IOException e) {
            log.error("I/O error while pinging host: {}", hostName, e);
        }

        log.debug("Returning ping status: {} for host: {}", isConnected, hostName);
        return isConnected;
    }

    /**
     * Checks the connection to a host and port, or just the host by passing a port number with a value of 0.
     *
     * This method checks whether a host is reachable either through a ping or by attempting a socket connection
     * to a specified port. If the host has not been checked previously (as tracked in the `holder` map),
     * it performs the appropriate check and stores the result. It logs detailed messages during the connection
     * process, including success or failure for both host and port checks.
     *
     * @param hostName   The host or IP address to check.
     * @param portNumber The port number to check, or 0 to just check the host.
     * @return Boolean indicating the connection status to the host and port (or just the host).
     */
    public boolean checkConnection(String hostName, int portNumber) {
        log = new MyLogger().getLogger();
        boolean isConnected = false;
        log.debug("Checking connection for host: {}, on port: {}", hostName, portNumber);
        // Check if the host has already been checked
        if (!holder.containsKey(hostName)) {
            if (portNumber == 0) {
                log.info("Attempting to ping the host: {}", hostName);
                isConnected = ping(hostName);
                log.info("Ping to host: {} resulted in: {}", hostName, isConnected);
            } else {
                log.info("Attempting to check connection to host: {} on port: {}", hostName, portNumber);
                isConnected = checkHostPortConn(hostName, portNumber);
                log.info("Connection to host: {} on port: {} resulted in: {}", hostName, portNumber, isConnected);
            }
            // Store the result in the holder map
            holder.put(hostName, isConnected);
            log.debug("Stored connection status: {} for host: {}", isConnected, hostName);
        } else {
            log.debug("Host: {} has already been checked. Returning stored connection status: {}", hostName, holder.get(hostName));
        }
        return holder.get(hostName);
    }



    /**
     * Checks the connection to a host without logging the result.
     *
     * This method attempts to ping a host and returns whether the host is reachable.
     * If the host has not been checked previously (tracked in the `holder` map), it performs the check
     * and stores the result. This method does not log any information regarding the connection status.
     *
     * @param hostname The host or IP address to check.
     * @return Boolean indicating the connection status to the host.
     */
    public boolean controlWithoutLog(String hostname) {
        boolean isConnected = false;
        // Check if the host has already been checked
        if (!holder.containsKey(hostname)) {
            try {
                isConnected = InetAddress.getByName(hostname).isReachable(3000);
            } catch (ConnectException e) {
                // Handle connection error without logging
            } catch (UnknownHostException ex) {
                // Handle unknown host error without logging
            } catch (IOException e) {
                // Handle IO error without logging
            }
            // Store the connection result in the holder map
            holder.put(hostname, isConnected);
        }
        // Return the stored connection status
        return holder.get(hostname);
    }
}
