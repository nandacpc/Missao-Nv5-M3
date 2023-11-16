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
import model.MovimentoVenda;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.PessoaFisica;

public class PessoaFisicaJpaController implements Serializable {

    public PessoaFisicaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PessoaFisica pessoaFisica) throws IllegalOrphanException, PreexistingEntityException, Exception {
        if (pessoaFisica.getMovimentoVendaCollection() == null) {
            pessoaFisica.setMovimentoVendaCollection(new ArrayList<MovimentoVenda>());
        }
        List<String> illegalOrphanMessages = null;
        Pessoa pessoaOrphanCheck = pessoaFisica.getPessoa();
        if (pessoaOrphanCheck != null) {
            PessoaFisica oldPessoaFisicaOfPessoa = pessoaOrphanCheck.getPessoaFisica();
            if (oldPessoaFisicaOfPessoa != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Pessoa " + pessoaOrphanCheck + " already has an item of type PessoaFisica whose pessoa column cannot be null. Please make another selection for the pessoa field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa pessoa = pessoaFisica.getPessoa();
            if (pessoa != null) {
                pessoa = em.getReference(pessoa.getClass(), pessoa.getIDPessoa());
                pessoaFisica.setPessoa(pessoa);
            }
            Collection<MovimentoVenda> attachedMovimentoVendaCollection = new ArrayList<MovimentoVenda>();
            for (MovimentoVenda movimentoVendaCollectionMovimentoVendaToAttach : pessoaFisica.getMovimentoVendaCollection()) {
                movimentoVendaCollectionMovimentoVendaToAttach = em.getReference(movimentoVendaCollectionMovimentoVendaToAttach.getClass(), movimentoVendaCollectionMovimentoVendaToAttach.getIDMovimento());
                attachedMovimentoVendaCollection.add(movimentoVendaCollectionMovimentoVendaToAttach);
            }
            pessoaFisica.setMovimentoVendaCollection(attachedMovimentoVendaCollection);
            em.persist(pessoaFisica);
            if (pessoa != null) {
                pessoa.setPessoaFisica(pessoaFisica);
                pessoa = em.merge(pessoa);
            }
            for (MovimentoVenda movimentoVendaCollectionMovimentoVenda : pessoaFisica.getMovimentoVendaCollection()) {
                PessoaFisica oldIDPessoaFisicaOfMovimentoVendaCollectionMovimentoVenda = movimentoVendaCollectionMovimentoVenda.getIDPessoaFisica();
                movimentoVendaCollectionMovimentoVenda.setIDPessoaFisica(pessoaFisica);
                movimentoVendaCollectionMovimentoVenda = em.merge(movimentoVendaCollectionMovimentoVenda);
                if (oldIDPessoaFisicaOfMovimentoVendaCollectionMovimentoVenda != null) {
                    oldIDPessoaFisicaOfMovimentoVendaCollectionMovimentoVenda.getMovimentoVendaCollection().remove(movimentoVendaCollectionMovimentoVenda);
                    oldIDPessoaFisicaOfMovimentoVendaCollectionMovimentoVenda = em.merge(oldIDPessoaFisicaOfMovimentoVendaCollectionMovimentoVenda);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPessoaFisica(pessoaFisica.getIDPessoa()) != null) {
                throw new PreexistingEntityException("PessoaFisica " + pessoaFisica + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PessoaFisica pessoaFisica) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PessoaFisica persistentPessoaFisica = em.find(PessoaFisica.class, pessoaFisica.getIDPessoa());
            Pessoa pessoaOld = persistentPessoaFisica.getPessoa();
            Pessoa pessoaNew = pessoaFisica.getPessoa();
            Collection<MovimentoVenda> movimentoVendaCollectionOld = persistentPessoaFisica.getMovimentoVendaCollection();
            Collection<MovimentoVenda> movimentoVendaCollectionNew = pessoaFisica.getMovimentoVendaCollection();
            List<String> illegalOrphanMessages = null;
            if (pessoaNew != null && !pessoaNew.equals(pessoaOld)) {
                PessoaFisica oldPessoaFisicaOfPessoa = pessoaNew.getPessoaFisica();
                if (oldPessoaFisicaOfPessoa != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Pessoa " + pessoaNew + " already has an item of type PessoaFisica whose pessoa column cannot be null. Please make another selection for the pessoa field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (pessoaNew != null) {
                pessoaNew = em.getReference(pessoaNew.getClass(), pessoaNew.getIDPessoa());
                pessoaFisica.setPessoa(pessoaNew);
            }
            Collection<MovimentoVenda> attachedMovimentoVendaCollectionNew = new ArrayList<MovimentoVenda>();
            for (MovimentoVenda movimentoVendaCollectionNewMovimentoVendaToAttach : movimentoVendaCollectionNew) {
                movimentoVendaCollectionNewMovimentoVendaToAttach = em.getReference(movimentoVendaCollectionNewMovimentoVendaToAttach.getClass(), movimentoVendaCollectionNewMovimentoVendaToAttach.getIDMovimento());
                attachedMovimentoVendaCollectionNew.add(movimentoVendaCollectionNewMovimentoVendaToAttach);
            }
            movimentoVendaCollectionNew = attachedMovimentoVendaCollectionNew;
            pessoaFisica.setMovimentoVendaCollection(movimentoVendaCollectionNew);
            pessoaFisica = em.merge(pessoaFisica);
            if (pessoaOld != null && !pessoaOld.equals(pessoaNew)) {
                pessoaOld.setPessoaFisica(null);
                pessoaOld = em.merge(pessoaOld);
            }
            if (pessoaNew != null && !pessoaNew.equals(pessoaOld)) {
                pessoaNew.setPessoaFisica(pessoaFisica);
                pessoaNew = em.merge(pessoaNew);
            }
            for (MovimentoVenda movimentoVendaCollectionOldMovimentoVenda : movimentoVendaCollectionOld) {
                if (!movimentoVendaCollectionNew.contains(movimentoVendaCollectionOldMovimentoVenda)) {
                    movimentoVendaCollectionOldMovimentoVenda.setIDPessoaFisica(null);
                    movimentoVendaCollectionOldMovimentoVenda = em.merge(movimentoVendaCollectionOldMovimentoVenda);
                }
            }
            for (MovimentoVenda movimentoVendaCollectionNewMovimentoVenda : movimentoVendaCollectionNew) {
                if (!movimentoVendaCollectionOld.contains(movimentoVendaCollectionNewMovimentoVenda)) {
                    PessoaFisica oldIDPessoaFisicaOfMovimentoVendaCollectionNewMovimentoVenda = movimentoVendaCollectionNewMovimentoVenda.getIDPessoaFisica();
                    movimentoVendaCollectionNewMovimentoVenda.setIDPessoaFisica(pessoaFisica);
                    movimentoVendaCollectionNewMovimentoVenda = em.merge(movimentoVendaCollectionNewMovimentoVenda);
                    if (oldIDPessoaFisicaOfMovimentoVendaCollectionNewMovimentoVenda != null && !oldIDPessoaFisicaOfMovimentoVendaCollectionNewMovimentoVenda.equals(pessoaFisica)) {
                        oldIDPessoaFisicaOfMovimentoVendaCollectionNewMovimentoVenda.getMovimentoVendaCollection().remove(movimentoVendaCollectionNewMovimentoVenda);
                        oldIDPessoaFisicaOfMovimentoVendaCollectionNewMovimentoVenda = em.merge(oldIDPessoaFisicaOfMovimentoVendaCollectionNewMovimentoVenda);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pessoaFisica.getIDPessoa();
                if (findPessoaFisica(id) == null) {
                    throw new NonexistentEntityException("The pessoaFisica with id " + id + " no longer exists.");
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
            PessoaFisica pessoaFisica;
            try {
                pessoaFisica = em.getReference(PessoaFisica.class, id);
                pessoaFisica.getIDPessoa();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pessoaFisica with id " + id + " no longer exists.", enfe);
            }
            Pessoa pessoa = pessoaFisica.getPessoa();
            if (pessoa != null) {
                pessoa.setPessoaFisica(null);
                pessoa = em.merge(pessoa);
            }
            Collection<MovimentoVenda> movimentoVendaCollection = pessoaFisica.getMovimentoVendaCollection();
            for (MovimentoVenda movimentoVendaCollectionMovimentoVenda : movimentoVendaCollection) {
                movimentoVendaCollectionMovimentoVenda.setIDPessoaFisica(null);
                movimentoVendaCollectionMovimentoVenda = em.merge(movimentoVendaCollectionMovimentoVenda);
            }
            em.remove(pessoaFisica);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PessoaFisica> findPessoaFisicaEntities() {
        return findPessoaFisicaEntities(true, -1, -1);
    }

    public List<PessoaFisica> findPessoaFisicaEntities(int maxResults, int firstResult) {
        return findPessoaFisicaEntities(false, maxResults, firstResult);
    }

    private List<PessoaFisica> findPessoaFisicaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PessoaFisica.class));
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

    public PessoaFisica findPessoaFisica(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PessoaFisica.class, id);
        } finally {
            em.close();
        }
    }

    public int getPessoaFisicaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PessoaFisica> rt = cq.from(PessoaFisica.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
