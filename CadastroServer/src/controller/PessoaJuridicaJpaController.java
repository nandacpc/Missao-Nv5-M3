/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Pessoa;
import model.MovimentoCompra;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.PessoaJuridica;

public class PessoaJuridicaJpaController implements Serializable {

    public PessoaJuridicaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PessoaJuridica pessoaJuridica) throws IllegalOrphanException, PreexistingEntityException, Exception {
        if (pessoaJuridica.getMovimentoCompraCollection() == null) {
            pessoaJuridica.setMovimentoCompraCollection(new ArrayList<MovimentoCompra>());
        }
        List<String> illegalOrphanMessages = null;
        Pessoa pessoaOrphanCheck = pessoaJuridica.getPessoa();
        if (pessoaOrphanCheck != null) {
            PessoaJuridica oldPessoaJuridicaOfPessoa = pessoaOrphanCheck.getPessoaJuridica();
            if (oldPessoaJuridicaOfPessoa != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Pessoa " + pessoaOrphanCheck + " already has an item of type PessoaJuridica whose pessoa column cannot be null. Please make another selection for the pessoa field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa pessoa = pessoaJuridica.getPessoa();
            if (pessoa != null) {
                pessoa = em.getReference(pessoa.getClass(), pessoa.getIDPessoa());
                pessoaJuridica.setPessoa(pessoa);
            }
            Collection<MovimentoCompra> attachedMovimentoCompraCollection = new ArrayList<MovimentoCompra>();
            for (MovimentoCompra movimentoCompraCollectionMovimentoCompraToAttach : pessoaJuridica.getMovimentoCompraCollection()) {
                movimentoCompraCollectionMovimentoCompraToAttach = em.getReference(movimentoCompraCollectionMovimentoCompraToAttach.getClass(), movimentoCompraCollectionMovimentoCompraToAttach.getIDMovimento());
                attachedMovimentoCompraCollection.add(movimentoCompraCollectionMovimentoCompraToAttach);
            }
            pessoaJuridica.setMovimentoCompraCollection(attachedMovimentoCompraCollection);
            em.persist(pessoaJuridica);
            if (pessoa != null) {
                pessoa.setPessoaJuridica(pessoaJuridica);
                pessoa = em.merge(pessoa);
            }
            for (MovimentoCompra movimentoCompraCollectionMovimentoCompra : pessoaJuridica.getMovimentoCompraCollection()) {
                PessoaJuridica oldIDPessoaJuridicaOfMovimentoCompraCollectionMovimentoCompra = movimentoCompraCollectionMovimentoCompra.getIDPessoaJuridica();
                movimentoCompraCollectionMovimentoCompra.setIDPessoaJuridica(pessoaJuridica);
                movimentoCompraCollectionMovimentoCompra = em.merge(movimentoCompraCollectionMovimentoCompra);
                if (oldIDPessoaJuridicaOfMovimentoCompraCollectionMovimentoCompra != null) {
                    oldIDPessoaJuridicaOfMovimentoCompraCollectionMovimentoCompra.getMovimentoCompraCollection().remove(movimentoCompraCollectionMovimentoCompra);
                    oldIDPessoaJuridicaOfMovimentoCompraCollectionMovimentoCompra = em.merge(oldIDPessoaJuridicaOfMovimentoCompraCollectionMovimentoCompra);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPessoaJuridica(pessoaJuridica.getIDPessoa()) != null) {
                throw new PreexistingEntityException("PessoaJuridica " + pessoaJuridica + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PessoaJuridica pessoaJuridica) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PessoaJuridica persistentPessoaJuridica = em.find(PessoaJuridica.class, pessoaJuridica.getIDPessoa());
            Pessoa pessoaOld = persistentPessoaJuridica.getPessoa();
            Pessoa pessoaNew = pessoaJuridica.getPessoa();
            Collection<MovimentoCompra> movimentoCompraCollectionOld = persistentPessoaJuridica.getMovimentoCompraCollection();
            Collection<MovimentoCompra> movimentoCompraCollectionNew = pessoaJuridica.getMovimentoCompraCollection();
            List<String> illegalOrphanMessages = null;
            if (pessoaNew != null && !pessoaNew.equals(pessoaOld)) {
                PessoaJuridica oldPessoaJuridicaOfPessoa = pessoaNew.getPessoaJuridica();
                if (oldPessoaJuridicaOfPessoa != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Pessoa " + pessoaNew + " already has an item of type PessoaJuridica whose pessoa column cannot be null. Please make another selection for the pessoa field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (pessoaNew != null) {
                pessoaNew = em.getReference(pessoaNew.getClass(), pessoaNew.getIDPessoa());
                pessoaJuridica.setPessoa(pessoaNew);
            }
            Collection<MovimentoCompra> attachedMovimentoCompraCollectionNew = new ArrayList<MovimentoCompra>();
            for (MovimentoCompra movimentoCompraCollectionNewMovimentoCompraToAttach : movimentoCompraCollectionNew) {
                movimentoCompraCollectionNewMovimentoCompraToAttach = em.getReference(movimentoCompraCollectionNewMovimentoCompraToAttach.getClass(), movimentoCompraCollectionNewMovimentoCompraToAttach.getIDMovimento());
                attachedMovimentoCompraCollectionNew.add(movimentoCompraCollectionNewMovimentoCompraToAttach);
            }
            movimentoCompraCollectionNew = attachedMovimentoCompraCollectionNew;
            pessoaJuridica.setMovimentoCompraCollection(movimentoCompraCollectionNew);
            pessoaJuridica = em.merge(pessoaJuridica);
            if (pessoaOld != null && !pessoaOld.equals(pessoaNew)) {
                pessoaOld.setPessoaJuridica(null);
                pessoaOld = em.merge(pessoaOld);
            }
            if (pessoaNew != null && !pessoaNew.equals(pessoaOld)) {
                pessoaNew.setPessoaJuridica(pessoaJuridica);
                pessoaNew = em.merge(pessoaNew);
            }
            for (MovimentoCompra movimentoCompraCollectionOldMovimentoCompra : movimentoCompraCollectionOld) {
                if (!movimentoCompraCollectionNew.contains(movimentoCompraCollectionOldMovimentoCompra)) {
                    movimentoCompraCollectionOldMovimentoCompra.setIDPessoaJuridica(null);
                    movimentoCompraCollectionOldMovimentoCompra = em.merge(movimentoCompraCollectionOldMovimentoCompra);
                }
            }
            for (MovimentoCompra movimentoCompraCollectionNewMovimentoCompra : movimentoCompraCollectionNew) {
                if (!movimentoCompraCollectionOld.contains(movimentoCompraCollectionNewMovimentoCompra)) {
                    PessoaJuridica oldIDPessoaJuridicaOfMovimentoCompraCollectionNewMovimentoCompra = movimentoCompraCollectionNewMovimentoCompra.getIDPessoaJuridica();
                    movimentoCompraCollectionNewMovimentoCompra.setIDPessoaJuridica(pessoaJuridica);
                    movimentoCompraCollectionNewMovimentoCompra = em.merge(movimentoCompraCollectionNewMovimentoCompra);
                    if (oldIDPessoaJuridicaOfMovimentoCompraCollectionNewMovimentoCompra != null && !oldIDPessoaJuridicaOfMovimentoCompraCollectionNewMovimentoCompra.equals(pessoaJuridica)) {
                        oldIDPessoaJuridicaOfMovimentoCompraCollectionNewMovimentoCompra.getMovimentoCompraCollection().remove(movimentoCompraCollectionNewMovimentoCompra);
                        oldIDPessoaJuridicaOfMovimentoCompraCollectionNewMovimentoCompra = em.merge(oldIDPessoaJuridicaOfMovimentoCompraCollectionNewMovimentoCompra);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pessoaJuridica.getIDPessoa();
                if (findPessoaJuridica(id) == null) {
                    throw new NonexistentEntityException("The pessoaJuridica with id " + id + " no longer exists.");
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
            PessoaJuridica pessoaJuridica;
            try {
                pessoaJuridica = em.getReference(PessoaJuridica.class, id);
                pessoaJuridica.getIDPessoa();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pessoaJuridica with id " + id + " no longer exists.", enfe);
            }
            Pessoa pessoa = pessoaJuridica.getPessoa();
            if (pessoa != null) {
                pessoa.setPessoaJuridica(null);
                pessoa = em.merge(pessoa);
            }
            Collection<MovimentoCompra> movimentoCompraCollection = pessoaJuridica.getMovimentoCompraCollection();
            for (MovimentoCompra movimentoCompraCollectionMovimentoCompra : movimentoCompraCollection) {
                movimentoCompraCollectionMovimentoCompra.setIDPessoaJuridica(null);
                movimentoCompraCollectionMovimentoCompra = em.merge(movimentoCompraCollectionMovimentoCompra);
            }
            em.remove(pessoaJuridica);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PessoaJuridica> findPessoaJuridicaEntities() {
        return findPessoaJuridicaEntities(true, -1, -1);
    }

    public List<PessoaJuridica> findPessoaJuridicaEntities(int maxResults, int firstResult) {
        return findPessoaJuridicaEntities(false, maxResults, firstResult);
    }

    private List<PessoaJuridica> findPessoaJuridicaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PessoaJuridica.class));
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

    public PessoaJuridica findPessoaJuridica(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PessoaJuridica.class, id);
        } finally {
            em.close();
        }
    }

    public int getPessoaJuridicaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PessoaJuridica> rt = cq.from(PessoaJuridica.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
