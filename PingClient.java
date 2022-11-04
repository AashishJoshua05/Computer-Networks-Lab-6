import java.io.*;
import java.net.*;
import java.util.*;



public class PingClient
{

public static void main(String[] args) throws Exception
{
    // Get command line argument.
    if (args.length != 2) 
    {
        System.out.println("Required arguments: host port");
        return;
    }

    String ServerName =args[0];	
    int port = Integer.parseInt(args[1]);
    // Create a datagram socket for receiving and sending UDP packets
    // through the port specified on the command line.
    DatagramSocket socket = new DatagramSocket();
    InetAddress IPAddress =InetAddress.getByName(ServerName);
    // Processing loop.

    // You should write the client so that it sends
    // 10 ping requests to the server, separated by
    // approximately one second.
    long timeTakenArray[]=new long[10];
    int packetNotRecivied[] = new int[10];
    for(int i=0;i<10;i++)
    {
        // Create a datagram packet to hold outgoing UDP packet.
        //Each message contains
        // a payload of data that includes the keyword PING,
        // a sequence number, and a timestamp.
        Random random = new Random();
        long SendTime = System.currentTimeMillis();
        String Message = "Ping "+ i + " " + SendTime + "\n";
        DatagramPacket request =new DatagramPacket(Message.getBytes(), Message.length(),IPAddress,port );
        socket.send(request);
        DatagramPacket reply =new DatagramPacket(new byte[1024], 1024);
        socket.setSoTimeout(1000);
        try
        {
            socket.receive(reply);

        }
        catch(IOException E)
        {
            System.out.println(" Reply not sent.");
        }
        long RecieveTime = System.currentTimeMillis();
        long Time_Taken=RecieveTime-SendTime;
        if(random.nextDouble() < 0.3)
        {
            packetNotRecivied[i] = 1;
        }
        timeTakenArray[i] = Time_Taken;
        printData(reply,Time_Taken, packetNotRecivied[i]);

        // Thread.sleep(1000);
    }
    long minRTT = Integer.MAX_VALUE;
    long maxRTT = Integer.MIN_VALUE;
    double avgRTT = 0;


    for(int i=0;i<10;i++)
    {
        System.out.println(timeTakenArray[i]);
        if(packetNotRecivied[i]==1)
        {
            continue;
        }
        else
        {
            if(minRTT > timeTakenArray[i])
            {
                minRTT = timeTakenArray[i];
            }
            if(maxRTT < timeTakenArray[i])
            {
                maxRTT = timeTakenArray[i];
            }
            avgRTT += timeTakenArray[i];
        }
    }
    System.out.println("The Max RTT is "+maxRTT);
    System.out.println("The Min RTT is "+minRTT);
    avgRTT = avgRTT/10;
    System.out.println("The Avg RTT is "+avgRTT);
    // After sending each packet, the client waits up
    // to one second to receive a reply.
    // If one seconds goes by without a reply from the server,
    // then the client assumes that its packet or the
    // server's reply packet has been lost in the network.
}

    /*
    * Print ping data to the standard output stream.
    */
private static void printData(DatagramPacket request,long timetaken,int notRecivied) throws Exception
{
    // Obtain references to the packet's array of bytes.
    byte[] buf = request.getData();

    // Wrap the bytes in a byte array input stream,
    // so that you can read the data as a stream of bytes.
    ByteArrayInputStream bais = new ByteArrayInputStream(buf);

    // Wrap the byte array output stream in an input stream reader,
    // so you can read the data as a stream of characters.
    InputStreamReader isr = new InputStreamReader(bais);

    // Wrap the input stream reader in a bufferred reader,
    // so you can read the character data a line at a time.
    // (A line is a sequence of chars terminated by any combination of \r and \n.)
    BufferedReader br = new BufferedReader(isr);

    // The message data is contained in a single line, so read this line.
    String line = br.readLine();
    // Print host address and data received from it.
    if(notRecivied==1)
    {
        System.out.println(" Reply not sent.");
    }
    else
    {
        System.out.println("Received from " +request.getAddress().getHostAddress() +": " +new String(line)+"\nTime Taken: " +timetaken+"ms");
        Thread.sleep(1000); 
    }
}

};