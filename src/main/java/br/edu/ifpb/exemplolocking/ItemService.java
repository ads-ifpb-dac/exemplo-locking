package br.edu.ifpb.exemplolocking;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementarQuantidade(Long id, Long quantidade) {
        Item item = itemRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        item.setQuantidade(item.getQuantidade() + quantidade);
    }

}
