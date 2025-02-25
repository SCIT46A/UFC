package app.scit46.ufc.service;

import org.springframework.stereotype.Service;

import app.scit46.ufc.dto.ItemDTO;
import app.scit46.ufc.entity.ItemEntity;
import app.scit46.ufc.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemEntity addItem(ItemDTO itemDTO) {
        ItemEntity item = itemRepository.findByName(itemDTO.getName());
        if(item != null) {
            return item;
        }
        return itemRepository.save(ItemEntity.toEntity(itemDTO));
    }
}
