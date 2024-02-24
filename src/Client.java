import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    //you can run mutliple threads by running the code multiple times
    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            String response;
            while ((response = input.readLine()) != null) {
                System.out.println(response);
                if (response.contains("Game finished")) {
                    break;
                }
                System.out.print("Enter your guess ");
                String guess = scanner.nextLine();
                output.println(guess);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}