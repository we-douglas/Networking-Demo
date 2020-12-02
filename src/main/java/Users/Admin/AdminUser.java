package Users.Admin;

import Network.Server;
import Users.Client.Client;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
this class is what a user would interact with for this typing test
 */
public class AdminUser implements Runnable, Serializable {
    private Server server;

    public AdminUser(Server server){
        this.server =server;
    }

    @Override
    public void run() {
        while (true){
            Scanner in = new Scanner(System.in);
            String s = in. nextLine();

            if(s.equals("test")){
                if(server.agentList.size()!=0) {
                    printClients();
                    System.out.print("Enter a client to test>");
                }else {print("Please connect an Agent First");}
            }
            else if(s.equals("printc")) {
                if(server.clientList.size()!=0){printClients();}
                else {print("Please connect a Client First");}
            }
            else if(s.equals("printFile")) {
                if(server.localFileList.size()!=0){printLocalFiles();}
                else {print("Please add a file First");}
            }
            else if(s.equals("print")) {
                if(server.agentList.size()!=0){printAgents();}
                else {print("Please connect an Agent First");}
            }
            else if(s.equals("pong")) {
                if(server.dispPongs){server.dispPongs=false;}
                else {server.dispPongs=true;}
            }
            else if(s.equals("help")){
                System.out.println(
                        "Connect agents to: "+server.portNum+"\n"+
                                "print      displays all connected ports \n"+
                                "test       runs RPC tests on given port \n"+
                                "ip         display managers ip \n"+
                                "help       Displays this help msg"
                );}
            else if(s.equals("add")) {
                try {
                    server.clientList.add(runClient(server,server.portNum));
                }catch (IOException e){e.printStackTrace();}
            }
            else if(s.equals("addFile")) {
                System.out.print("Enter File Name>");
                String msg = in.nextLine();
                server.localFileList.add(msg);
            }
            else if(s.equals("c")) {System.out.println(server.clientList.size());}
            else if(s.equals("push")) {
                System.out.print("Enter msg to send to all Nodes>");
                String msg = in.nextLine();
                for(int i=0;i<server.agentList.size();i++){
                    if(!server.agentList.get(i).dead){
                        server.agentList.get(i).cmd=msg;}
                }
            }
            else if(s.equals("search")) {
                System.out.print("Enter Search Msg> ");
                String msg = in.nextLine();
                System.out.print("Enter Time-to-Live> ");
                long searchId =System.currentTimeMillis();
                server.searchId=searchId;
                msg = "◊"+server.portNum+"◊"+msg+"◊"+searchId+"◊"+in.nextLine()+"◊"+server.portNum+""+"◊";
                for(int i=0;i<server.agentList.size();i++){
                    if(!server.agentList.get(i).dead){
                        server.agentList.get(i).cmd=msg;}
                }
            }
            else{
                System.out.print("Enter a client id to send msg to>");

                server.agentList.get(Integer.parseInt(in.nextLine())).cmd=s;}
        }

    }
    private void print(String out){ System.out.println("ADMIN USER:     "+out); }

    private void printAgents(){
        System.out.println("________________________________________________");
        System.out.println("|Agent ID |Port  |Freq  |Dead?  |Last Ping      |");
        for(int i = 0; i< server.agentList.size(); i++){
            System.out.println("|     "+i+"   | "+
                    server.agentList.get(i).port+" | "+
                    server.agentList.get(i).freq+" | "+
                    server.agentList.get(i).dead+" | "+
                    server.agentList.get(i).lastPing+" |"+
                    server.agentList.get(i).fallbackPort+" |");
        }
        System.out.println("________________________________________________");
    }
    private void printClients(){
        System.out.println("________________________________________________");
        System.out.println("|Client ID|Port  |Freq  |Dead?  |Last Ping      |");
        for(int i = 0; i< server.clientList.size(); i++){
            System.out.println("|     "+i+"   | "+
                    server.clientList.get(i).remote+" | "+
                    server.clientList.get(i).freq+" | "+
                    server.clientList.get(i).id+" | "+
                    server.clientList.get(i).cmd+" |");
        }
        System.out.println("________________________________________________");
    }
    private void printLocalFiles(){
        System.out.println("________________________________________________");
        System.out.println("|File Name                                     |");
        for(int i = 0; i< server.localFileList.size(); i++){
            System.out.println("|     "+i+"   | "+
                    server.localFileList.get(i));
            //server.clientList.get(i).freq+" | "+
            //server.clientList.get(i).id+" | "+
            //server.clientList.get(i).cmd+" |");
        }
        System.out.println("________________________________________________");
    }


    public static Client runClient(Server home, int localPort) throws IOException {
        System.out.println("Port Number");
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        Client client =new Client(home,Integer.parseInt(s),localPort);
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(client);
        return client;
    }
}
