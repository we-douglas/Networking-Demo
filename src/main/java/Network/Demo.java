package Network;
import Users.Admin.AdminUser;
import Users.Agent.Agent;
import Users.Agent.AgentMonitor;
import Users.Client.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Demo {
    public static void main(String[] args) throws IOException {
        InetAddress localhost = InetAddress.getLocalHost();
        System.out.println("System IP Address : " + (localhost.getHostAddress()).trim());
        runServer();

    }
    public static void runServer() throws IOException{
        System.out.println("Port Number");
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();


        ArrayList<Agent> agentList =new ArrayList<Agent>();
        ArrayList<Client> clientList =new ArrayList<Client>();
        Server server =new Server(agentList,clientList,s);
        AdminUser user =new AdminUser(server);
        AgentMonitor monitor = new AgentMonitor(server);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        executorService.submit(server);
        executorService.submit(user);
        executorService.submit(monitor);
    }
}
