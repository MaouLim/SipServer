package bupt.sipchat.server;

import bupt.networks.sip.exceptions.InitFailureException;

import java.util.Scanner;

/*
 * Created by Maou Lim on 2017/7/12.
 */
public class RunServer {

    public static final String SIP_CONFIG_URL    = "res/sip-config.yml";
    public static final String SERVER_CONFIG_URL = "res/server-config.yml";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {
            ServerController controller =
                    new ServerController(SIP_CONFIG_URL, SERVER_CONFIG_URL);

            String line = null;
            while (!"quit".equals(line = scanner.nextLine())) {
                System.out.println("system running");
            }

            controller.close();
        }
        catch (InitFailureException ex) {
            ex.printStackTrace();
        }
    }
}
