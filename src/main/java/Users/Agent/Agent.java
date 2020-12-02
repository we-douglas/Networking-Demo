package Users.Agent;

public class Agent {

    public String cmd;
    public long freq;
    public long lastPing;
    public int port;
    public int fallbackPort;
    // private Calendar c;
    public Boolean dead;



    public Agent(long freq,String cmd,int port){
        lastPing = System.currentTimeMillis();
        this.cmd =cmd;
        this.freq = freq;
        this.port=port;
        this.fallbackPort=-1;
        //c=c;
        dead = false;
    }
}