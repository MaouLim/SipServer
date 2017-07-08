package bupt.networks;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SipDemo {

    public static void main(String[] args) {
        try {

            System.out.println(
                //InetAddress.getLocalHost().getHostName()
                InetAddress.getLocalHost().getHostAddress()
            );


        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}