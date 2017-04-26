import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.zip.CRC32;

/**
 * Created by Vincent on 4/25/2017.
 */
public class Ex3Client {

    private final static String SERVER_NAME = "codebank.xyz";
    private final static int PORT_NUMBER = 38103;
    String serverName;
    int portNumber;
    byte[] byteArray;

    public Ex3Client(String serverName, int portNumber) {
        this.serverName = serverName;
        this.portNumber = portNumber;

        callServer();
    }

    private void callServer() {
        Integer bytes = 0;
        try (Socket socket = new Socket(serverName, portNumber)) {
            System.out.println("Connected to server");

            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            OutputStream os = socket.getOutputStream();
            PrintStream out = new PrintStream(os, true, "UTF-8");
            CRC32 cyclicRedundancyCheck = new CRC32();
            int count, index = 0;
            count = is.read();
            byteArray = new byte[count];
            System.out.println("Reading " + count + " bytes.\nData received:");
            while ((count--) > 0) {
                bytes = is.read();
                byteArray[index++] = bytes.byteValue();
                System.out.print(Integer.toHexString(bytes & 0xFF).toUpperCase());

                if (index % 8 == 0)
                    System.out.println();
            }
            System.out.println();
            short checkSum = checkSum(byteArray);
            for (int j = 1; j >= 0; j--) {
                os.write((byte) (checkSum >> j * 8));
                // System.out.println(Long.toHexString(checkSum >> j *8));
            }
            System.out.println("Checksum calculated: 0x" + Long.toHexString(checkSum&0xFF).toUpperCase());
            if (is.read() == 1) {
                System.out.println("1\n" + "Response good.");
            } else {
                System.out.println("0\n" + "Response bad.");
            }
//            if (is.read() == 1)
//                System.out.println("Response good.\nDisconnected from server.");
//            else
//                System.out.println("Response bad yo\nDisconnected from server.");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public short checkSum(byte[] b) {

        Long sum = (long) 0;//Long.parseLong("0");
        Long temp;
        int i = 0;
        while (i < b.length) {
            temp = (long) b[i++] & 0xFF;
                temp = temp << 8;
            if(i < b.length)
                temp |= b[i++] & 0xFF;
            sum += temp;
            //   System.out.println(Long.toHexString(temp & 0xFFFF).toUpperCase());
            //  System.out.println(Long.toHexString(sum & 0xFFFFFFFF).toUpperCase());
            if ((sum & 0xFFFF0000) > 0) {
                sum &= 0xFFFF;
                sum++;
                // System.out.println("carry occured " + i);
            }
        }
        return (short) ~(sum & 0xFFFF);
    }


    public static void main(String[] args) {
        new Ex3Client(SERVER_NAME, PORT_NUMBER);
    }

}
