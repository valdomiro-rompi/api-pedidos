package com.example.apipedidos.dto;

/**
 * DTO para retornar informações sobre o status da fila
 */
public class FilaStatusDTO {

        private int tamanho;
        private boolean vazia;

        public FilaStatusDTO() {
        }

        public FilaStatusDTO(int tamanho, boolean vazia) {
                this.tamanho = tamanho;
                this.vazia = vazia;
        }

        public int getTamanho() {
                return tamanho;
        }

        public void setTamanho(int tamanho) {
                this.tamanho = tamanho;
        }

        public boolean isVazia() {
                return vazia;
        }

        public void setVazia(boolean vazia) {
                this.vazia = vazia;
        }
}
