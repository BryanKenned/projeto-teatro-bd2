package br.com.projetoteatro.config;

import jakarta.persistence.EntityManager;

public class TesteJPA {

    public static void main(String[] args) {

        EntityManager em = JPAUtil.getEntityManager();

        System.out.println("=================================");
        System.out.println("JPA CONECTADO COM SUCESSO!");
        System.out.println("=================================");

        em.getTransaction().begin();

        em.getTransaction().commit();

        em.close();
        JPAUtil.fechar();
    }
}