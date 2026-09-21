package br.com.projetoteatro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = "cliente")
public class Cliente extends Pessoa {

    public Cliente() {
        super();
    }
}