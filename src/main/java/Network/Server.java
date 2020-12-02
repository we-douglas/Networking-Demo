package Network;


import Users.Agent.Agent;
import Users.Client.Client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends UnicastRemoteObject implements Runnable,RMIInterface{

    public ArrayList<Agent> agentList;
    public ArrayList<Client> clientList;
    public ArrayList<String> searchList;
    public ArrayList<String> localFileList;
    public int portNum;
    public boolean dispPongs;
    public long searchId;

    public Server(ArrayList<Agent> agentList,ArrayList<Client> clientList,String port) throws RemoteException {
        this.agentList =agentList;
        this.clientList=clientList;
        this.searchList=new ArrayList<String>();
        this.localFileList=new ArrayList<String>();
        portNum=Integer.parseInt(port);
        dispPongs = false;
        searchId =-1;
    }

    @Override
    public String msg(String msg) throws RemoteException {
        System.out.println(msg);
        return "Server:     recived";
    }

    @Override
    public String ASNTmsg(String msg) throws RemoteException {
        return null;
    }

    @Override
    public String cmd(int idx) throws RemoteException {
        String command =this.agentList.get(idx).cmd;
        //System.out.println(command);
        return command;
    }
    public String cmdDone(int idx,String msg) throws RemoteException {
        String command =this.agentList.get(idx).cmd;
        System.out.println("Client"+idx+" says: "+msg);
        this.agentList.get(idx).cmd="";
        return "";
    }
    public String pong(int idx,String msg) throws RemoteException {
        String command =this.agentList.get(idx).cmd;
        if(dispPongs){System.out.println("Client"+idx+" says: "+msg);}
        this.agentList.get(idx).cmd="";
        return "";
    }

    @Override
    public String ping(int idx) throws RemoteException {
        //System.out.println(idx);
        this.agentList.get(idx).lastPing=System.currentTimeMillis();
        return "Server:     recived";
    }
    @Override
    public int getFallbackPort(int idx){
        int temp =idx;
        if(idx !=-6) {
            for(int i=idx-1;i>-1;i--){
                if(!agentList.get(i).dead){
                    //System.out.println("sent "+agentList.get(i).port+" to "+idx);
                    return agentList.get(i).port;}
            }
        }
        return (-1);
    }

    @Override
    public int requestJoin(long freq,String cmd,int port) throws RemoteException {
        agentList.add(new Agent(freq,cmd,port));
        if(agentList.size()>clientList.size()){
            Client client =new Client(this,port,portNum);
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.submit(client);
            clientList.add(client);
        }
        System.out.println("requestJoin"+port+" "+agentList.size());
        return agentList.size()-1;

    }

    @Override
    public void run() {
        Registry registry = null;
        try {
            registry= LocateRegistry.createRegistry(Integer.parseInt(String.valueOf(portNum)));

            //Naming.rebind("//localhost/MyServer", this);
            registry.rebind("MyServer", this);
            System.out.println("Server ready");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

    }

    public void updateFallback(int fallback,int remote){
        for(int i=0;i<agentList.size();i++){
            //System.out.println(agentList.get(i).port+"   "+remote);
            if(agentList.get(i).port==remote){
                agentList.get(i).fallbackPort=fallback;
            }
        }
    }
    public void runSearch(String msg){
        System.out.println("Starting Search");
        String[] data =msg.split("◊");

        Boolean hit=false;
        for(int i=0;i<localFileList.size();i++){
            if(localFileList.get(i).equals((data[2]))){
                System.out.println("HIT!!!!!");
                hit=true;
                msg="HIT,"+portNum+msg;
            }
        }
        if(hit){
            String returnNode =data[5].substring(data[5].lastIndexOf(",")+1);
            String restPath=data[5].substring(0,data[5].lastIndexOf(","));
            System.out.println(restPath+"  "+returnNode);
            for(int x=0;x<agentList.size();x++){
                if(!agentList.get(x).dead
                        &&returnNode.equals(agentList.get(x).port+"")){
                    agentList.get(x).cmd=msg;}
            }
        }


        if(!hit&& !data[4].equals("0")){
            System.out.println("MISS");
            data[4]= String.valueOf(Integer.parseInt(data[4])-1);
            data[5]=data[5]+","+portNum;

            msg="◊"+data[1]+"◊"+data[2]+"◊"+data[3]+"◊"+data[4]+"◊"+data[5]+"◊";
            //System.out.println(msg);




            for(int i=0;i<agentList.size();i++){
                if(!agentList.get(i).dead
                        &&!data[5].contains(agentList.get(i).port+"")){
                    agentList.get(i).cmd=msg;}
            }
        }
    }
    public void returnSearch(String msg){
        String[] data =msg.split("◊");

        if(data[1].equals(portNum+"")){
            System.out.println("GOT FILE FROM NODE AT PORT"+data[0].substring(3));
            localFileList.add(data[2]);
        }else {


            data[5] = data[5].substring(0, data[5].length() - 5);
            System.out.println(data[5]);

            String returnNode = data[5];
            if (data[5].contains(",")) {


                String restPath=returnNode.substring(0,returnNode.lastIndexOf(","));
                returnNode = returnNode.substring(returnNode.lastIndexOf(",") + 1);

                data[5]=restPath;
                msg=data[0]+"◊"+data[1]+"◊"+data[2]+"◊"+data[3]+"◊"+data[4]+"◊"+data[5]+"◊";

            }
            System.out.println("returnNode: " + returnNode);

            //String returnNode =data[5].substring(data[5].lastIndexOf(",")+1);
            //String restPath=data[5].substring(0,data[5].lastIndexOf(","));
            //System.out.println(restPath+"  "+returnNode);
            for (int x = 0; x < agentList.size(); x++) {
                if (!agentList.get(x).dead
                        && returnNode.equals(agentList.get(x).port + "")) {
                    agentList.get(x).cmd = msg;
                }
            }
        }
    }

    private void print(String out){
        System.out.println("SERVER:     "+out);
    }
}