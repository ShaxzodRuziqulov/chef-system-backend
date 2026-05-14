package com.example.oshpazbackendsystem.repository;

import com.example.oshpazbackendsystem.entity.ShoppingListItem;
import com.example.oshpazbackendsystem.entity.enums.ShoppingItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {

    // Ro'yxatning barcha elementlari
    List<ShoppingListItem> findByShoppingListId(Long shoppingListId);

    // Status bo'yicha — hali sotib olinmaganlar
    List<ShoppingListItem> findByShoppingListIdAndStatus(
            Long shoppingListId, ShoppingItemStatus status);

    // Ro'yxat o'chirilganda barcha elementlarini o'chirish
    @Modifying
    @Query("DELETE FROM ShoppingListItem i WHERE i.shoppingList.id = :listId")
    void deleteByShoppingListId(@Param("listId") Long listId);

    // Barcha elementlarni "sotib olindi" deb belgilash
    @Modifying
    @Query("""
            UPDATE ShoppingListItem i
            SET i.status = com.example.oshpazbackendsystem.entity.enums.ShoppingItemStatus.PURCHASED
            WHERE i.shoppingList.id = :listId
            """)
    void markAllAsPurchased(@Param("listId") Long listId);
}
