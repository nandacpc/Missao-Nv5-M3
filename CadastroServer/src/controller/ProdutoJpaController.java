/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.MovimentoVenda;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.MovimentoCompra;
import model.Produtos;

public class ProdutoJpaController implements Serializable {

    public ProdutoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Produtos produtos) throws PreexistingEntityException, Exception {
        if (produtos.getMovimentoVendaCollection() == null) {
            produtos.setMovimentoVendaCollection(new ArrayList<MovimentoVenda>());
        }
        if (produtos.getMovimentoCompraCollection() == null) {
            produtos.setMovimentoCompraCollection(new ArrayList<MovimentoCompra>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<MovimentoVenda> attachedMovimentoVendaCollection = new ArrayList<MovimentoVenda>();
            for (MovimentoVenda movimentoVendaCollectionMovimentoVendaToAttach : produtos.getMovimentoVendaCollection()) {
                movimentoVendaCollectionMovimentoVendaToAttach = em.getReference(movimentoVendaCollectionMovimentoVendaToAttach.getClass(), movimentoVendaCollectionMovimentoVendaToAttach.getIDMovimento());
                attachedMovimentoVendaCollection.add(movimentoVendaCollectionMovimentoVendaToAttach);
            }
            produtos.setMovimentoVendaCollection(attachedMovimentoVendaCollection);
            Collection<MovimentoCompra> attachedMovimentoCompraCollection = new ArrayList<MovimentoCompra>();
            for (MovimentoCompra movimentoCompraCollectionMovimentoCompraToAttach : produtos.getMovimentoCompraCollection()) {
                movimentoCompraCollectionMovimentoCompraToAttach = em.getReference(movimentoCompraCollectionMovimentoCompraToAttach.getClass(), movimentoCompraCollectionMovimentoCompraToAttach.getIDMovimento());
                attachedMovimentoCompraCollection.add(movimentoCompraCollectionMovimentoCompraToAttach);
            }
            produtos.setMovimentoCompraCollection(attachedMovimentoCompraCollection);
            em.persist(produtos);
            for (MovimentoVenda movimentoVendaCollectionMovimentoVenda : produtos.getMovimentoVendaCollection()) {
                Produtos oldIDProdutoOfMovimentoVendaCollectionMovimentoVenda = movimentoVendaCollectionMovimentoVenda.getIDProduto();
                movimentoVendaCollectionMovimentoVenda.setIDProduto(produtos);
                movimentoVendaCollectionMovimentoVenda = em.merge(movimentoVendaCollectionMovimentoVenda);
                if (oldIDProdutoOfMovimentoVendaCollectionMovimentoVenda != null) {
                    oldIDProdutoOfMovimentoVendaCollectionMovimentoVenda.getMovimentoVendaCollection().remove(movimentoVendaCollectionMovimentoVenda);
                    oldIDProdutoOfMovimentoVendaCollectionMovimentoVenda = em.merge(oldIDProdutoOfMovimentoVendaCollectionMovimentoVenda);
                }
            }
            for (MovimentoCompra movimentoCompraCollectionMovimentoCompra : produtos.getMovimentoCompraCollection()) {
                Produtos oldIDProdutoOfMovimentoCompraCollectionMovimentoCompra = movimentoCompraCollectionMovimentoCompra.getIDProduto();
                movimentoCompraCollectionMovimentoCompra.setIDProduto(produtos);
                movimentoCompraCollectionMovimentoCompra = em.merge(movimentoCompraCollectionMovimentoCompra);
                if (oldIDProdutoOfMovimentoCompraCollectionMovimentoCompra != null) {
                    oldIDProdutoOfMovimentoCompraCollectionMovimentoCompra.getMovimentoCompraCollection().remove(movimentoCompraCollectionMovimentoCompra);
                    oldIDProdutoOfMovimentoCompraCollectionMovimentoCompra = em.merge(oldIDProdutoOfMovimentoCompraCollectionMovimentoCompra);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProdutos(produtos.getIDProduto()) != null) {
                throw new PreexistingEntityException("Produtos " + produtos + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Produtos produtos) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produtos persistentProdutos = em.find(Produtos.class, produtos.getIDProduto());
            Collection<MovimentoVenda> movimentoVendaCollectionOld = persistentProdutos.getMovimentoVendaCollection();
            Collection<MovimentoVenda> movimentoVendaCollectionNew = produtos.getMovimentoVendaCollection();
            Collection<MovimentoCompra> movimentoCompraCollectionOld = persistentProdutos.getMovimentoCompraCollection();
            Collection<MovimentoCompra> movimentoCompraCollectionNew = produtos.getMovimentoCompraCollection();
            Collection<MovimentoVenda> attachedMovimentoVendaCollectionNew = new ArrayList<MovimentoVenda>();
            for (MovimentoVenda movimentoVendaCollectionNewMovimentoVendaToAttach : movimentoVendaCollectionNew) {
                movimentoVendaCollectionNewMovimentoVendaToAttach = em.getReference(movimentoVendaCollectionNewMovimentoVendaToAttach.getClass(), movimentoVendaCollectionNewMovimentoVendaToAttach.getIDMovimento());
                attachedMovimentoVendaCollectionNew.add(movimentoVendaCollectionNewMovimentoVendaToAttach);
            }
            movimentoVendaCollectionNew = attachedMovimentoVendaCollectionNew;
            produtos.setMovimentoVendaCollection(movimentoVendaCollectionNew);
            Collection<MovimentoCompra> attachedMovimentoCompraCollectionNew = new ArrayList<MovimentoCompra>();
            for (MovimentoCompra movimentoCompraCollectionNewMovimentoCompraToAttach : movimentoCompraCollectionNew) {
                movimentoCompraCollectionNewMovimentoCompraToAttach = em.getReference(movimentoCompraCollectionNewMovimentoCompraToAttach.getClass(), movimentoCompraCollectionNewMovimentoCompraToAttach.getIDMovimento());
                attachedMovimentoCompraCollectionNew.add(movimentoCompraCollectionNewMovimentoCompraToAttach);
            }
            movimentoCompraCollectionNew = attachedMovimentoCompraCollectionNew;
            produtos.setMovimentoCompraCollection(movimentoCompraCollectionNew);
            produtos = em.merge(produtos);
            for (MovimentoVenda movimentoVendaCollectionOldMovimentoVenda : movimentoVendaCollectionOld) {
                if (!movimentoVendaCollectionNew.contains(movimentoVendaCollectionOldMovimentoVenda)) {
                    movimentoVendaCollectionOldMovimentoVenda.setIDProduto(null);
                    movimentoVendaCollectionOldMovimentoVenda = em.merge(movimentoVendaCollectionOldMovimentoVenda);
                }
            }
            for (MovimentoVenda movimentoVendaCollectionNewMovimentoVenda : movimentoVendaCollectionNew) {
                if (!movimentoVendaCollectionOld.contains(movimentoVendaCollectionNewMovimentoVenda)) {
                    Produtos oldIDProdutoOfMovimentoVendaCollectionNewMovimentoVenda = movimentoVendaCollectionNewMovimentoVenda.getIDProduto();
                    movimentoVendaCollectionNewMovimentoVenda.setIDProduto(produtos);
                    movimentoVendaCollectionNewMovimentoVenda = em.merge(movimentoVendaCollectionNewMovimentoVenda);
                    if (oldIDProdutoOfMovimentoVendaCollectionNewMovimentoVenda != null && !oldIDProdutoOfMovimentoVendaCollectionNewMovimentoVenda.equals(produtos)) {
                        oldIDProdutoOfMovimentoVendaCollectionNewMovimentoVenda.getMovimentoVendaCollection().remove(movimentoVendaCollectionNewMovimentoVenda);
                        oldIDProdutoOfMovimentoVendaCollectionNewMovimentoVenda = em.merge(oldIDProdutoOfMovimentoVendaCollectionNewMovimentoVenda);
                    }
                }
            }
            for (MovimentoCompra movimentoCompraCollectionOldMovimentoCompra : movimentoCompraCollectionOld) {
                if (!movimentoCompraCollectionNew.contains(movimentoCompraCollectionOldMovimentoCompra)) {
                    movimentoCompraCollectionOldMovimentoCompra.setIDProduto(null);
                    movimentoCompraCollectionOldMovimentoCompra = em.merge(movimentoCompraCollectionOldMovimentoCompra);
                }
            }
            for (MovimentoCompra movimentoCompraCollectionNewMovimentoCompra : movimentoCompraCollectionNew) {
                if (!movimentoCompraCollectionOld.contains(movimentoCompraCollectionNewMovimentoCompra)) {
                    Produtos oldIDProdutoOfMovimentoCompraCollectionNewMovimentoCompra = movimentoCompraCollectionNewMovimentoCompra.getIDProduto();
                    movimentoCompraCollectionNewMovimentoCompra.setIDProduto(produtos);
                    movimentoCompraCollectionNewMovimentoCompra = em.merge(movimentoCompraCollectionNewMovimentoCompra);
                    if (oldIDProdutoOfMovimentoCompraCollectionNewMovimentoCompra != null && !oldIDProdutoOfMovimentoCompraCollectionNewMovimentoCompra.equals(produtos)) {
                        oldIDProdutoOfMovimentoCompraCollectionNewMovimentoCompra.getMovimentoCompraCollection().remove(movimentoCompraCollectionNewMovimentoCompra);
                        oldIDProdutoOfMovimentoCompraCollectionNewMovimentoCompra = em.merge(oldIDProdutoOfMovimentoCompraCollectionNewMovimentoCompra);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = produtos.getIDProduto();
                if (findProdutos(id) == null) {
                    throw new NonexistentEntityException("The produtos with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produtos produtos;
            try {
                produtos = em.getReference(Produtos.class, id);
                produtos.getIDProduto();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The produtos with id " + id + " no longer exists.", enfe);
            }
            Collection<MovimentoVenda> movimentoVendaCollection = produtos.getMovimentoVendaCollection();
            for (MovimentoVenda movimentoVendaCollectionMovimentoVenda : movimentoVendaCollection) {
                movimentoVendaCollectionMovimentoVenda.setIDProduto(null);
                movimentoVendaCollectionMovimentoVenda = em.merge(movimentoVendaCollectionMovimentoVenda);
            }
            Collection<MovimentoCompra> movimentoCompraCollection = produtos.getMovimentoCompraCollection();
            for (MovimentoCompra movimentoCompraCollectionMovimentoCompra : movimentoCompraCollection) {
                movimentoCompraCollectionMovimentoCompra.setIDProduto(null);
                movimentoCompraCollectionMovimentoCompra = em.merge(movimentoCompraCollectionMovimentoCompra);
            }
            em.remove(produtos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Produtos> findProdutosEntities() {
        return findProdutosEntities(true, -1, -1);
    }

    public List<Produtos> findProdutosEntities(int maxResults, int firstResult) {
        return findProdutosEntities(false, maxResults, firstResult);
    }

    private List<Produtos> findProdutosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Produtos.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Produtos findProdutos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produtos.class, id);
        } finally {
            em.close();
        }
    }

    public int getProdutosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produtos> rt = cq.from(Produtos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public List<Produtos> listarProdutos() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
