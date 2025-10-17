package com.example.apipedidos.service;

import com.example.apipedidos.dto.PedidoRequestDTO;
import com.example.apipedidos.dto.PedidoResponseDTO;
import com.example.apipedidos.exception.PedidoNotFoundException;
import com.example.apipedidos.model.Pedido;
import com.example.apipedidos.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócio relacionada aos pedidos
 */
@Service
@Transactional
public class PedidoService {
    
    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);
    
    @Autowired
    private PedidoRepository pedidoRepository;
    
    // Stack para gerenciar fila de pedidos criados
    private final Stack<PedidoResponseDTO> filaPedidos = new Stack<>();
    
    /**
     * Cria um novo pedido no sistema
     * @param request Dados do pedido a ser criado
     * @return DTO com os dados do pedido criado
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PedidoResponseDTO criarPedido(PedidoRequestDTO request) {
        log.info("Criando novo pedido para cliente: {}", request.getNomeCliente());
        
        // Validar dados do pedido (validações adicionais além das anotações)
        validarDadosPedido(request);
        
        // Converter DTO para entidade
        Pedido pedido = convertToEntity(request);
        
        // Salvar no banco de dados
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        
        log.info("Pedido criado com sucesso. ID: {}", pedidoSalvo.getId());
        
        // Converter entidade para DTO de resposta
        PedidoResponseDTO pedidoResponse = convertToResponseDTO(pedidoSalvo);
        
        // Adicionar pedido à fila (Stack)
        adicionarPedidoNaFila(pedidoResponse);
        
        return pedidoResponse;
    }
    
    /**
     * Lista todos os pedidos do sistema ordenados por data (mais recentes primeiro)
     * @return Lista de DTOs com os dados dos pedidos
     */
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarTodosPedidos() {
        log.info("Listando todos os pedidos");
        
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByDataPedidoDesc();
        
        log.info("Encontrados {} pedidos", pedidos.size());
        
        return pedidos.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca um pedido específico pelo seu ID
     * @param id ID do pedido a ser buscado
     * @return DTO com os dados do pedido encontrado
     * @throws PedidoNotFoundException se o pedido não for encontrado
     */
    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPedidoPorId(Long id) {
        log.info("Buscando pedido com ID: {}", id);
        
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pedido não encontrado com ID: {}", id);
                    return new PedidoNotFoundException(id);
                });
        
        log.info("Pedido encontrado: {}", pedido.getId());
        
        return convertToResponseDTO(pedido);
    }
    
    /**
     * Valida os dados do pedido aplicando regras de negócio adicionais
     * @param request DTO com os dados do pedido a serem validados
     */
    private void validarDadosPedido(PedidoRequestDTO request) {
        // As validações básicas já são feitas pelas anotações Jakarta Validation
        // Aqui podem ser adicionadas validações de negócio mais complexas no futuro
        
        log.debug("Validando dados do pedido para cliente: {}", request.getNomeCliente());
        
        // Exemplo de validação adicional: normalizar nome do cliente
        if (request.getNomeCliente() != null) {
            request.setNomeCliente(request.getNomeCliente().trim());
        }
        
        // Exemplo de validação adicional: normalizar descrição
        if (request.getDescricao() != null) {
            request.setDescricao(request.getDescricao().trim());
        }
    }
    
    /**
     * Converte um DTO de request para uma entidade Pedido
     * @param request DTO com os dados do pedido
     * @return Entidade Pedido
     */
    private Pedido convertToEntity(PedidoRequestDTO request) {
        Pedido pedido = new Pedido();
        pedido.setNomeCliente(request.getNomeCliente());
        pedido.setDescricao(request.getDescricao());
        pedido.setValor(request.getValor());
        // dataPedido será definida automaticamente pelo @PrePersist
        
        return pedido;
    }
    
    /**
     * Converte uma entidade Pedido para um DTO de response
     * @param pedido Entidade Pedido
     * @return DTO de response com os dados do pedido
     */
    private PedidoResponseDTO convertToResponseDTO(Pedido pedido) {
        PedidoResponseDTO response = new PedidoResponseDTO();
        response.setId(pedido.getId());
        response.setNomeCliente(pedido.getNomeCliente());
        response.setDescricao(pedido.getDescricao());
        response.setValor(pedido.getValor());
        response.setDataPedido(pedido.getDataPedido());
        return response;
    }
    
    /**
     * Adiciona um pedido à fila (Stack)
     * @param pedido DTO do pedido a ser adicionado à fila
     */
    private void adicionarPedidoNaFila(PedidoResponseDTO pedido) {
        filaPedidos.push(pedido);
        log.info("Pedido ID {} adicionado à fila. Total de pedidos na fila: {}", 
                pedido.getId(), filaPedidos.size());
    }
    
    /**
     * Remove e retorna o último pedido da fila (LIFO - Last In, First Out)
     * @return DTO do pedido removido da fila, ou null se a fila estiver vazia
     */
    public PedidoResponseDTO processarProximoPedidoDaFila() {
        if (filaPedidos.isEmpty()) {
            log.info("Fila de pedidos está vazia");
            return null;
        }
        
        PedidoResponseDTO pedido = filaPedidos.pop();
        log.info("Pedido ID {} removido da fila. Pedidos restantes na fila: {}", 
                pedido.getId(), filaPedidos.size());
        return pedido;
    }
    
    /**
     * Retorna o próximo pedido da fila sem removê-lo
     * @return DTO do próximo pedido da fila, ou null se a fila estiver vazia
     */
    public PedidoResponseDTO visualizarProximoPedidoDaFila() {
        if (filaPedidos.isEmpty()) {
            log.info("Fila de pedidos está vazia");
            return null;
        }
        
        PedidoResponseDTO pedido = filaPedidos.peek();
        log.info("Próximo pedido da fila: ID {}", pedido.getId());
        return pedido;
    }
    
    /**
     * Retorna o tamanho atual da fila de pedidos
     * @return Número de pedidos na fila
     */
    public int getTamanhoDaFila() {
        return filaPedidos.size();
    }
    
    /**
     * Verifica se a fila de pedidos está vazia
     * @return true se a fila estiver vazia, false caso contrário
     */
    public boolean isFilaVazia() {
        return filaPedidos.isEmpty();
    }
    
    /**
     * Obtém todas as mensagens (pedidos) que estão atualmente na fila
     * @return Lista com todos os pedidos da fila (do topo para a base)
     */
    public List<PedidoResponseDTO> obterTodasAsMensagens() {
        log.info("Obtendo todas as mensagens da fila. Total: {}", filaPedidos.size());
        
        // Retorna uma cópia da lista para evitar modificações externas
        // A ordem será do topo da pilha (último adicionado) para a base (primeiro adicionado)
        return new ArrayList<>(filaPedidos);
    }
}