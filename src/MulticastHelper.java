import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

class MulticastHelper
{
    protected String IP;
    protected Integer port;

    protected InetAddress group;
    protected MulticastSocket socket;

    protected byte[] buffer = new byte[1024];

    MulticastHelper(String IP, Integer port) throws IOException
    {
        this.IP = IP;
        this.port = port;

        this.group = InetAddress.getByName(IP);
        this.socket = new MulticastSocket(port);

        this.socket.joinGroup(this.group);
    }

    void close()
    {
        if(!this.socket.isClosed()){
            this.socket.close();
        }
    }

    void setTimeout(Integer miliseconds) throws SocketException
    {
        this.socket.setSoTimeout(miliseconds);
    }

    String receive() throws IOException, SocketTimeoutException
    {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        this.socket.receive(packet);
        return new String(packet.getData(), packet.getOffset(), packet.getLength());
    }

    void send(String message) throws IOException
    {
		byte[] messageBytes = message.getBytes();
		DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, this.group, this.port);
		this.socket.send(packet);
    }
}