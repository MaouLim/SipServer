package bupt.networks;

import com.sun.istack.internal.NotNull;

import java.net.InetAddress;

/*
 * Created by Maou Lim on 2017/7/8.
 */
public class Endpoint {

    private String address = null;
    private int    port    = 8080;

    public Endpoint(@NotNull String address, int port) {
        this.address = address;
        assert (0 <= port && port < 65536);
        this.port = port;
    }

    public Endpoint(InetAddress address, int port) {
        this.address = address.toString().split("/")[1];
        assert (0 <= port && port < 65536);
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
