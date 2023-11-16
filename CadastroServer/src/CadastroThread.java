package cadastroserver;

import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import model.Produtos;

public class CadastroThread extends Thread {
    private final ProdutoJpaController ctrl;
    private final UsuarioJpaController ctrlUsu;
    private final Socket s1;

    public CadastroThread(ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, Socket s1) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.s1 = s1;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(s1.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(s1.getInputStream());
            
            String login = (String) in.readObject();
            String senha = (String) in.readObject();
           
            Usuario usuario = ctrlUsu.findUsuarios(login, senha);

            if (usuario == null) {                
                s1.close();
                return;
            }
            
            while (true) {
                String comando = (String) in.readObject();

                if (comando.equals("L")) {                    
                    List<Produtos> produtos = ctrl.findProdutoEntities();
                    out.writeObject(produtos);
                } else {                    
                }
            }
        } catch (IOException | ClassNotFoundException e) {
        } finally {
            try {
                s1.close();
            } catch (IOException e) {
            }
        }
    }
}
