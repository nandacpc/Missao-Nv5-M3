package cadastroserver;

import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class CadastroServer {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroServerPU");
        ProdutoJpaController ctrl = new ProdutoJpaController(emf);
        UsuarioJpaController ctrlUsu = new UsuarioJpaController(emf);
        MovimentoJpaController ctrlMov = new MovimentoJpaController(emf); //CadastroThreadV2
        PessoaJpaController ctrlPessoa = new PessoaJpaController(emf); //CadastroThreadV2

        try (ServerSocket serverSocket = new ServerSocket(4321)) {
            while (true) {
                System.out.println("Aguardando conexao...");
                Socket clientSocket = serverSocket.accept();

                CadastroThread cadastroThread = new CadastroThread(ctrl, ctrlUsu, ctrlMov, ctrlPessoa, clientSocket);
                cadastroThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            emf.close();
        }
    }
}
