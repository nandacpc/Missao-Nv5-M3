package cadastroclientv2;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class ThreadClient extends Thread {
    private final ObjectInputStream entrada;
    private final JTextArea textArea;

    public ThreadClient(ObjectInputStream entrada, JTextArea textArea) {
        this.entrada = entrada;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object mensagem = entrada.readObject();
                if (mensagem instanceof String) {
                    String texto = (String) mensagem;
                    adicionarAoTextArea(texto);
                } else if (mensagem instanceof List) {
                    List<String> produtos = (List<String>) mensagem;
                    for (String produto : produtos) {
                        adicionarAoTextArea(produto);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void adicionarAoTextArea(String mensagem) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(mensagem + "\n");
        });
    }
}
