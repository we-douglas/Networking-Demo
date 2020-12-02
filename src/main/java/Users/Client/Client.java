package Users.Client;

import Network.RMIInterface;
import Network.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Client implements Runnable, ClientListener {
    public int id=-1;
    public int freq = 3000;
    public String cmd ="";
    public int remote;
    public int localPort;
    public int fallbackPort;
    public Server home;


    public Client(Server home,int s,int localPort) throws RemoteException {
        remote =s;
        this.localPort=localPort;
        this.home=home;
    }
    @Override
    public String clientMsg(String msg) {
        System.out.println(msg);
        return "rec";
    }

    @Override
    public void run() {
        RMIInterface rmiInterface;

        try {
            Registry r = LocateRegistry.getRegistry(remote);
            rmiInterface = (RMIInterface) r.lookup("MyServer");
            id= rmiInterface.requestJoin(freq,cmd,localPort);


           // System.out.println("join respond "+id);
            if(id!=-1) {
                System.out.println("Connected!");

                while (true) {
                    fallbackPort=rmiInterface.getFallbackPort(id);
                    home.updateFallback(fallbackPort,remote);
                    try {
                        rmiInterface.ping(id);
                        //System.out.println("ping "+remote);
                        rmiInterface.pong(id, localPort+"");
                        Thread.sleep(freq);


                        // make a seperate thread with high freq
                        String cmd = rmiInterface.cmd(id);
                        //System.out.println("CMD is: "+cmd);
                        if (!cmd.equals("")) {
                            rmiInterface.cmdDone(id, "ok");
                            if(cmd.contains("HIT,")){
                                System.out.println("Return Search: " + cmd);
                                home.returnSearch(cmd);
                            }
                            else if(cmd.contains("◊")){

                                System.out.println("Got Search: " + cmd);
                                home.searchList.add(cmd);
                                home.runSearch(cmd);

                            }
                            else{
                                System.out.println("Client Heard: " + cmd);
                                //rmiInterface.cmdDone(id, "ok");
                            }
                        }
                    }catch (Exception e){
                        System.out.println("Node at "+remote+" is Dead");
                        //home.removeClient(remote);
                        break;
                    }
                }
            }
            else{System.out.println("Server Denied Request");}

        } catch ( IOException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
    public static String execCmd() throws java.io.IOException {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"a.exe", "-get t"};
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        // Read the output from the command
        String s;
        String out="";
        while ((s = stdInput.readLine()) != null) {
            out =out+s;
        }
        return out;
    }

    public static Client runClient(Server home,int localPort,int remotePort) throws IOException {
        Client client =new Client(home,remotePort,localPort);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(client);
        return client;
    }


}