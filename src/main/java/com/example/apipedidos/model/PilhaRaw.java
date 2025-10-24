package com.example.apipedidos.model;

public class PilhaRaw {
    private Pedido[] elementos;
    private int topo;
    private int capacidade;

    public PilhaRaw(int tamanho) {
        capacidade = tamanho;
        elementos = new Pedido[capacidade];
        topo = -1;
    }

    public void push(Pedido elemento) {
        if (isFull()) {
            throw new RuntimeException("Pilha cheia! Não é possível adicionar mais elementos.");
        }
        elementos[++topo] = elemento;
        System.out.println("Adicionado: " + elemento.getId() + " (Tamanho: " + size() + ")");
    }

    public Pedido pop() {
        if (isEmpty()) {
            throw new RuntimeException("Pilha vazia! Não há elementos para remover.");
        }
        Pedido elementoRemovido = elementos[topo--];
        System.out.println("Removido: " + elementoRemovido.getId() + " (Tamanho: " + size() + ")");
        return elementoRemovido;
    }

    public Pedido peek() {
        if (isEmpty()) {
            throw new RuntimeException("Pilha vazia! Não há elemento no topo.");
        }
        return elementos[topo];
    }

    public boolean isEmpty() {
        return topo == -1;
    }

    public boolean isFull() {
        return topo == capacidade - 1;
    }

    public int size() {
        return topo + 1;
    }
}