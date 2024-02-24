import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Server {
    private static final int PORT = 12345;
    private static volatile int secretNumber;
    private static volatile List<PrintWriter> clientOutputStreams = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running...");
            secretNumber = generateRandomNumber();
            System.out.println("Secret number: " + secretNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New Client connected: " + clientSocket);
                PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
                clientOutputStreams.add(clientOut);
                new Thread(new Threade(clientSocket, clientOut)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(100) + 1; // Generate a random number between 1 and 100
    }

    private static class Threade implements Runnable {
        private Socket clientSocket;
        private PrintWriter clientOut;

        public Threade(Socket socket, PrintWriter out) {
            this.clientSocket = socket;
            this.clientOut = out;
        }

        @Override
        public void run() {
            try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                clientOut.println("Welcome");

                String guess;
                while ((guess = input.readLine()) != null) {
                    int guessedNumber = Integer.parseInt(guess);
                    if (guessedNumber == secretNumber) {
                        broadcast("Client " + Thread.currentThread().getId() + " guessed the number!");
                        break;
                    } else if (guessedNumber < secretNumber) {
                        clientOut.println("The secret number is greater");
                    } else {
                        clientOut.println("The secret number is smaller");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void broadcast(String message) {
        for (PrintWriter writer : clientOutputStreams) {
            writer.println(message);
        }
    }
}
