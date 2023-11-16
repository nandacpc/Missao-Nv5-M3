package cadastroserver;

import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import model.Produtos;
import model.Movimento;
import model.Pessoa;

public class CadastroThreadV2 extends Thread {
    private final ProdutoJpaController ctrl;
    private final UsuarioJpaController ctrlUsu;
    private final MovimentoJpaController ctrlMov;
    private final PessoaJpaController ctrlPessoa;
    private final Socket s1;

    public CadastroThread(ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, MovimentoJpaController ctrlMov, PessoaJpaController ctrlPessoa, Socket s1) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.ctrlMov = ctrlMov;
        this.ctrlPessoa = ctrlPessoa;
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
                } else if (comando.equals("E") || comando.equals("S")) {
                    int pessoaId = (int) in.readObject();
                    int produtoId = (int) in.readObject();
                    int quantidade = (int) in.readObject();
                    double valorUnitario = (double) in.readObject();

                    Pessoa pessoa = ctrlPessoa.findPessoa(pessoaId);
                    Produtos produto = ctrl.findProduto(produtoId);

                    if (pessoa != null && produto != null) {
                        Movimento movimento = new Movimento();
                        movimento.setUsuario(usuario);
                        movimento.setTipo(comando);
                        movimento.setPessoa(pessoa);
                        movimento.setProduto(produto);
                        movimento.setQuantidade(quantidade);
                        movimento.setValorUnitario(valorUnitario);

                        ctrlMov.create(movimento);
                                                
                        if (comando.equals("E")) {
                            produto.setQuantidade(produto.getQuantidade() + quantidade);
                        } else if (comando.equals("S")) {
                            produto.setQuantidade(produto.getQuantidade() - quantidade);
                        }
                        ctrl.edit(produto);
                    }
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