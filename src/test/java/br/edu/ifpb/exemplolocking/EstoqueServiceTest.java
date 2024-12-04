package br.edu.ifpb.exemplolocking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class EstoqueServiceTest {

    @Autowired
    private EstoqueService estoqueService;

    @Autowired
    private ItemRepository itemRepository;

    @SpyBean
    private ItemService itemService;

    private final List<Long> quantidadeAcrescimo = Arrays.asList(10L, 5L);

    @Test
    @DisplayName("Incrementar quantidade sem concorrência")
    void deveIncrementarQuantidade_semConcorrencia() {
        final Item srcItem = itemRepository.save(new Item());
        assertEquals(0, srcItem.getVersao());

        for(final Long quantidade: quantidadeAcrescimo) {
            estoqueService.incrementarQuantidadeProduto(srcItem.getId(), quantidade);
        }

        final Item item = itemRepository.findById(srcItem.getId()).get();

        assertAll(
                () -> assertEquals(2, item.getVersao()),
                () -> assertEquals(15, item.getQuantidade()),
                () -> verify(itemService, times(2)).incrementarQuantidade(anyLong(), anyLong())
        );
    }

    @Test
    @DisplayName("Incrementar quantidade com concorrência")
    void deveIncrementarQuantidade_comOptimisticLocking() throws InterruptedException {
        // given
        final Item srcItem = itemRepository.save(new Item());
        assertEquals(0, srcItem.getVersao());

        // when
        final ExecutorService executor = Executors
                .newFixedThreadPool(quantidadeAcrescimo.size());

        for (final Long amount : quantidadeAcrescimo) {
            executor.execute(() -> estoqueService.incrementarQuantidadeProduto(srcItem.getId(), amount));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // then
        final Item item = itemRepository.findById(srcItem.getId()).get();

        assertAll(
                () -> assertEquals(2, item.getVersao()),
                () -> assertEquals(15, item.getQuantidade()),
                () -> verify(itemService, atLeast(3)).incrementarQuantidade(anyLong(), anyLong())
        );
    }

}