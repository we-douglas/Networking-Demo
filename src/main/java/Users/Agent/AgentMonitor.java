package Users.Agent;

import Network.Server;
import Users.Agent.Agent;
import Users.Client.Client;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AgentMonitor implements Runnable{
    private Server server;

    public AgentMonitor(Server udpServer){
        this.server = udpServer;
    }


    @Override
    public void run() {
        //In this case, AgentMonitor prints out an alert message to inform the system
        //administrator


        while (true){

            int temp=0;

            try {
                for(int i = 0; i< server.agentList.size(); i++){
                    if(isDead(server.agentList.get(i))){
                        if(server.agentList.get(i).fallbackPort!=-1){
                            try {
                                System.out.println("Connecting To Fallback Port");
                                Client client =new Client(server,server.agentList.get(i).fallbackPort,server.portNum);
                                ExecutorService executorService = Executors.newFixedThreadPool(1);
                                executorService.submit(client);
                                server.clientList.add(client);
                                server.agentList.get(i).fallbackPort=-1;

                            }catch (IOException e){e.printStackTrace();}
                        }


                    }else{temp=1;}
                }
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
    private void print(String out){ System.out.println("UDP Client Monitor:     "+out); }

    public boolean isDead(Agent agent){
        if(System.currentTimeMillis()-agent.lastPing > (2*agent.freq)){
            agent.dead=true;
            return true;
        }
        else{
            return false;
        }
    }
    public static Client runClient(Server home,int fallback,int localPort) throws IOException {
        Client client =new Client(home,fallback,localPort);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(client);
        return client;
    }
}
