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
import model.PessoaJuridica;
import model.PessoaFisica;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Pessoa;

public class PessoaJpaController implements Serializable {

    public PessoaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pessoa pessoa) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PessoaJuridica pessoaJuridica = pessoa.getPessoaJuridica();
            if (pessoaJuridica != null) {
                pessoaJuridica = em.getReference(pessoaJuridica.getClass(), pessoaJuridica.getIDPessoa());
                pessoa.setPessoaJuridica(pessoaJuridica);
            }
            PessoaFisica pessoaFisica = pessoa.getPessoaFisica();
            if (pessoaFisica != null) {
                pessoaFisica = em.getReference(pessoaFisica.getClass(), pessoaFisica.getIDPessoa());
                pessoa.setPessoaFisica(pessoaFisica);
            }
            em.persist(pessoa);
            if (pessoaJuridica != null) {
                Pessoa oldPessoaOfPessoaJuridica = pessoaJuridica.getPessoa();
                if (oldPessoaOfPessoaJuridica != null) {
                    oldPessoaOfPessoaJuridica.setPessoaJuridica(null);
                    oldPessoaOfPessoaJuridica = em.merge(oldPessoaOfPessoaJuridica);
                }
                pessoaJuridica.setPessoa(pessoa);
                pessoaJuridica = em.merge(pessoaJuridica);
            }
            if (pessoaFisica != null) {
                Pessoa oldPessoaOfPessoaFisica = pessoaFisica.getPessoa();
                if (oldPessoaOfPessoaFisica != null) {
                    oldPessoaOfPessoaFisica.setPessoaFisica(null);
                    oldPessoaOfPessoaFisica = em.merge(oldPessoaOfPessoaFisica);
                }
                pessoaFisica.setPessoa(pessoa);
                pessoaFisica = em.merge(pessoaFisica);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPessoa(pessoa.getIDPessoa()) != null) {
                throw new PreexistingEntityException("Pessoa " + pessoa + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pessoa pessoa) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa persistentPessoa = em.find(Pessoa.class, pessoa.getIDPessoa());
            PessoaJuridica pessoaJuridicaOld = persistentPessoa.getPessoaJuridica();
            PessoaJuridica pessoaJuridicaNew = pessoa.getPessoaJuridica();
            PessoaFisica pessoaFisicaOld = persistentPessoa.getPessoaFisica();
            PessoaFisica pessoaFisicaNew = pessoa.getPessoaFisica();
            List<String> illegalOrphanMessages = null;
            if (pessoaJuridicaOld != null && !pessoaJuridicaOld.equals(pessoaJuridicaNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain PessoaJuridica " + pessoaJuridicaOld + " since its pessoa field is not nullable.");
            }
            if (pessoaFisicaOld != null && !pessoaFisicaOld.equals(pessoaFisicaNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain PessoaFisica " + pessoaFisicaOld + " since its pessoa field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (pessoaJuridicaNew != null) {
                pessoaJuridicaNew = em.getReference(pessoaJuridicaNew.getClass(), pessoaJuridicaNew.getIDPessoa());
                pessoa.setPessoaJuridica(pessoaJuridicaNew);
            }
            if (pessoaFisicaNew != null) {
                pessoaFisicaNew = em.getReference(pessoaFisicaNew.getClass(), pessoaFisicaNew.getIDPessoa());
                pessoa.setPessoaFisica(pessoaFisicaNew);
            }
            pessoa = em.merge(pessoa);
            if (pessoaJuridicaNew != null && !pessoaJuridicaNew.equals(pessoaJuridicaOld)) {
                Pessoa oldPessoaOfPessoaJuridica = pessoaJuridicaNew.getPessoa();
                if (oldPessoaOfPessoaJuridica != null) {
                    oldPessoaOfPessoaJuridica.setPessoaJuridica(null);
                    oldPessoaOfPessoaJuridica = em.merge(oldPessoaOfPessoaJuridica);
                }
                pessoaJuridicaNew.setPessoa(pessoa);
                pessoaJuridicaNew = em.merge(pessoaJuridicaNew);
            }
            if (pessoaFisicaNew != null && !pessoaFisicaNew.equals(pessoaFisicaOld)) {
                Pessoa oldPessoaOfPessoaFisica = pessoaFisicaNew.getPessoa();
                if (oldPessoaOfPessoaFisica != null) {
                    oldPessoaOfPessoaFisica.setPessoaFisica(null);
                    oldPessoaOfPessoaFisica = em.merge(oldPessoaOfPessoaFisica);
                }
                pessoaFisicaNew.setPessoa(pessoa);
                pessoaFisicaNew = em.merge(pessoaFisicaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pessoa.getIDPessoa();
                if (findPessoa(id) == null) {
                    throw new NonexistentEntityException("The pessoa with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa pessoa;
            try {
                pessoa = em.getReference(Pessoa.class, id);
                pessoa.getIDPessoa();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pessoa with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            PessoaJuridica pessoaJuridicaOrphanCheck = pessoa.getPessoaJuridica();
            if (pessoaJuridicaOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pessoa (" + pessoa + ") cannot be destroyed since the PessoaJuridica " + pessoaJuridicaOrphanCheck + " in its pessoaJuridica field has a non-nullable pessoa field.");
            }
            PessoaFisica pessoaFisicaOrphanCheck = pessoa.getPessoaFisica();
            if (pessoaFisicaOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pessoa (" + pessoa + ") cannot be destroyed since the PessoaFisica " + pessoaFisicaOrphanCheck + " in its pessoaFisica field has a non-nullable pessoa field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(pessoa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pessoa> findPessoaEntities() {
        return findPessoaEntities(true, -1, -1);
    }

    public List<Pessoa> findPessoaEntities(int maxResults, int firstResult) {
        return findPessoaEntities(false, maxResults, firstResult);
    }

    private List<Pessoa> findPessoaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pessoa.class));
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

    public Pessoa findPessoa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pessoa.class, id);
        } finally {
            em.close();
        }
    }

    public int getPessoaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pessoa> rt = cq.from(Pessoa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
