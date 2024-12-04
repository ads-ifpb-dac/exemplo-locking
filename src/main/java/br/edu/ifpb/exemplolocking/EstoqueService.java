package br.edu.ifpb.exemplolocking;

import jakarta.persistence.LockModeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstoqueService {

    private Logger log = LoggerFactory.getLogger(EstoqueService.class);
    private final ItemService itemService;

    public EstoqueService(ItemService itemService) {
        this.itemService = itemService;
    }

    @Transactional
    public void incrementarQuantidadeProduto(Long idItem, Long quantidade) {
        try {
            itemService.incrementarQuantidade(idItem, quantidade);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Alguém já fez update. Tentando novamente...", idItem);
            itemService.incrementarQuantidade(idItem, quantidade);
        }
    }
}
