package app.scit46.ufc.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")

public class ItemController {

    private final List<String> allItems = List.of(
            "아이템 1", "아이템 2", "아이템 3", "아이템 4", "아이템 5", "아이템 6", "아이템 7", "아이템 8");

    @GetMapping("/more")
    public List<String> getMoreItems(@RequestParam("page") int page) {
        int pageSize = 6;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, allItems.size());

        if (start >= allItems.size()) {
            return List.of(); // 더 이상 불러올 데이터가 없음
        }

        return allItems.subList(start, end);
    }
}
