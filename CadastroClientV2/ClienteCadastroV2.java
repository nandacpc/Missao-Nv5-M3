package cadastroclientv2;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CadastroClientV2 {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 4321);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)) {
            
            out.writeObject("op1"); // Login
            out.writeObject("op1"); // Senha

            BlockingQueue<String> messages = new ArrayBlockingQueue<>(10);
            Thread messageReader = new Thread(() -> {
                while (true) {
                    try {
                        String message = (String) in.readObject();
                        messages.put(message);
                    } catch (IOException | ClassNotFoundException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            messageReader.start();

            while (true) {                
                System.out.print("Escolha uma opção: ");
                System.out.println("L - Listar | X - Finalizar | E - Entrada | S - Saída");
                String comando = reader.readLine();

                out.writeObject(comando);

                if (comando.equals("L")) {
                    
                } else if (comando.equals("E") || comando.equals("S")) {
                    System.out.print("Id da pessoa: ");
                    String pessoaId = reader.readLine();
                    out.writeObject(pessoaId);

                    System.out.print("Id do produto: ");
                    String produtoId = reader.readLine();
                    out.writeObject(produtoId);

                    System.out.print("Quantidade: ");
                    String quantidade = reader.readLine();
                    out.writeObject(quantidade);

                    System.out.print("Valor unitário: ");
                    String valorUnitario = reader.readLine();
                    out.writeObject(valorUnitario);
                }

                if (comando.equals("X")) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
