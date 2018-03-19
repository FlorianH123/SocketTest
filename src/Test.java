import java.awt.image.DataBufferInt;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Test {
    private int taskId;

    public static void main(String[] args) {
        new Test().start();
    }

    private static int swap(int value) {
        int b1 = (value) & 0xff;
        int b2 = (value >> 8) & 0xff;
        int b3 = (value >> 16) & 0xff;
        int b4 = (value >> 24) & 0xff;

        return b1 << 24 | b2 << 16 | b3 << 8 | b4;
    }

    /**
     * Byte swap a single float value.
     *
     * @param value Value to byte swap.
     * @return Byte swapped representation.
     */
    public static float swap(float value) {
        int intValue = Float.floatToIntBits(value);
        intValue = swap(intValue);
        return Float.intBitsToFloat(intValue);
    }

    private void start() {
        int messageType;

        try {
            ServerSocket socket = new ServerSocket(32001);
            Socket client;
            client = socket.accept();
            DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());

            while (true) {
                messageType = swap(dataInputStream.readInt());
                System.out.println("Message Type: " + messageType);

                switch (messageType) {
                    case 1:
                        setTarget(dataOutputStream, dataInputStream);
                        break;

                    case 3:
                        getPID(dataOutputStream, dataInputStream);
                        break;

                    case 4:
                        setPID(dataOutputStream, dataInputStream);
                        break;

                    case 6:
                        setVelocity(dataOutputStream, dataInputStream);
                        break;

                    case 2:
                        receiveConnect(dataOutputStream, dataInputStream, client.getInetAddress());
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTarget(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {
        System.out.println("Message Size: " + swap(dataInputStream.readInt()));
        taskId = swap(dataInputStream.readInt());
        System.out.println("Task ID: " + taskId);
        System.out.println("Engine ID: " + swap(dataInputStream.readInt()));

        dataOutputStream.writeInt(swap(1));
        dataOutputStream.writeInt(swap(8));
    }

    private void getPID(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {
        System.out.println("Message Size: " + swap(dataInputStream.readInt()));

        dataOutputStream.writeInt(swap(3));
        dataOutputStream.writeInt(swap(20));
        dataOutputStream.writeFloat(swap(3.6f));
        dataOutputStream.writeFloat(swap(4.7f));
        dataOutputStream.writeFloat(swap(2.5f));
    }

    private void setPID(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {
        System.out.println("Message Size: " + swap(dataInputStream.readInt()));
        System.out.println("P: " + swap(dataInputStream.readFloat()));
        System.out.println("I: " + swap(dataInputStream.readFloat()));
        System.out.println("D: " + swap(dataInputStream.readFloat()));

        dataOutputStream.writeInt(swap(4));
        dataOutputStream.writeInt(swap(8));
    }

    private void setVelocity(DataOutputStream dataOutputStream, DataInputStream dataInputStream) throws IOException {
        System.out.println("Message Size: " + swap(dataInputStream.readInt()));
        System.out.println("Velocity: " + swap(dataInputStream.readFloat()));

        dataOutputStream.writeInt(swap(6));
        dataOutputStream.writeInt(swap(8));
    }

    private void receiveConnect(DataOutputStream dataOutputStream, DataInputStream dataInputStream, InetAddress inetAddress) throws IOException {
        int port;
        System.out.println("Message Size: " + swap(dataInputStream.readInt()));
        port = swap(dataInputStream.readInt());
        System.out.println("Port: " + port);

        dataOutputStream.writeInt(swap(2));
        dataOutputStream.writeInt(swap(8));
        System.out.println(inetAddress.getHostAddress());
        Socket socket = new Socket(inetAddress.getHostAddress(), port);
        DataOutputStream dataOutputStream1 = new DataOutputStream(socket.getOutputStream());

        while(true) {
            dataOutputStream1.writeFloat((float) (Math.random() * 1000));
        }
    }


}
