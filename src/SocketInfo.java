import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class SocketInfo {
    private Socket client;
    private DataInputStream is;
    private DataOutputStream out;

    public SocketInfo(Socket socket) throws IOException {
        this.client = socket;
        this.is = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public DataInputStream getIs() {
        return is;
    }

    public void setIs(DataInputStream is) {
        this.is = is;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }


}