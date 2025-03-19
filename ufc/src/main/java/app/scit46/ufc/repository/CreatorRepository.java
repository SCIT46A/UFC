package app.scit46.ufc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import app.scit46.ufc.entity.CreatorEntity;
import app.scit46.ufc.entity.UserEntity;

@Repository
public interface CreatorRepository extends JpaRepository<CreatorEntity, Long> {

    @Query("SELECT c FROM CreatorEntity c JOIN FETCH c.ownUser WHERE c.creatorStatus = false")
    List<CreatorEntity> findByCreatorStatusFalseWithUser();

    CreatorEntity findByOwnUser(UserEntity ownUser);

    // 새로운 메서드 추가
    @Query("SELECT c FROM CreatorEntity c WHERE c.ownUser = :ownUser")
    Optional<CreatorEntity> findCreatorByUser(@Param("ownUser") UserEntity ownUser);

    @Query("SELECT c FROM CreatorEntity c WHERE c.creatorId = :creatorId")
    Optional<CreatorEntity> findByCreatorId(@Param("creatorId") Long creatorId);

    @Query(value = """
            WITH CreatorProducts AS (
                SELECT p.product_id, p.price, pp.purchased_date
                FROM Products p
                JOIN ProductPayments pp ON p.product_id = pp.product_id
                WHERE p.created_by = :creatorId AND pp.status = 'completed'
            ),
            CreatorCampaigns AS (
                SELECT c.campaign_id, c.title,
                       COALESCE(SUM(md.quantity), 0) AS donated_quantity,
                       COALESCE(SUM(cg.quantity_required), 1) AS required_quantity
                FROM Campaigns c
                LEFT JOIN MaterialsDonations md ON c.campaign_id = md.campaign_id
                LEFT JOIN CampaignGoals cg ON c.campaign_id = cg.campaign_id
                WHERE c.created_by = :creatorId
                GROUP BY c.campaign_id, c.title
            ),
            RevenueData AS (
                SELECT DATE(purchased_date) AS purchased_date, SUM(price) AS total_amount
                FROM CreatorProducts
                WHERE purchased_date >= NOW() - INTERVAL 30 DAY
                GROUP BY DATE(purchased_date)
            ),
            OrdersData AS (
                SELECT DATE(purchased_date) AS order_date, i.name, i.item_id, COUNT(*) AS cnt,
                       CASE
                           WHEN i.item_id % 5 = 0 THEN '#4361ee'
                           WHEN i.item_id % 5 = 1 THEN '#f72585'
                           WHEN i.item_id % 5 = 2 THEN '#4cc9f0'
                           WHEN i.item_id % 5 = 3 THEN '#4895ef'
                           ELSE '#3f37c9'
                       END AS color
                FROM CreatorProducts cp
                JOIN Products p ON cp.product_id = p.product_id
                JOIN Items i ON p.item_id = i.item_id
                WHERE DATE_FORMAT(cp.purchased_date, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')
                GROUP BY DATE(purchased_date), i.name, i.item_id
            ),
            TotalCompleted AS (
                SELECT COUNT(*) AS total_count
                FROM ProductPayments
                WHERE product_id IN (SELECT product_id FROM CreatorProducts)
                  AND status = 'completed'
            )
            SELECT JSON_OBJECT(
                'totalSales', (SELECT COALESCE(SUM(price), 0) FROM CreatorProducts),
                'newOrders', (SELECT COUNT(*) FROM CreatorProducts WHERE purchased_date >= NOW() - INTERVAL 30 DAY),
                'activeCampaigns', (SELECT COUNT(*) FROM Campaigns WHERE created_by = :creatorId AND campaign_status = 1),
                'campaignLikes', (SELECT COUNT(*) FROM Likes WHERE campaign_id IN (SELECT campaign_id FROM CreatorCampaigns)),
                'campaigns', (SELECT JSON_ARRAYAGG(JSON_OBJECT(
                    'name', title,
                    'achievementRate', COALESCE((donated_quantity / NULLIF(required_quantity, 0)) * 100, 0)
                )) FROM CreatorCampaigns),
                'revenue', (SELECT JSON_ARRAYAGG(JSON_OBJECT(
                    'date', purchased_date,
                    'amount', total_amount
                )) FROM RevenueData),
                'orders', (
                    SELECT JSON_ARRAYAGG(order_obj)
                    FROM (
                        SELECT JSON_OBJECT(
                            'date', order_date,
                            'products', (
                                SELECT JSON_ARRAYAGG(JSON_OBJECT(
                                    'name', do2.name,
                                    'sales', do2.cnt,
                                    'color', do2.color
                                ))
                                FROM OrdersData do2
                                WHERE do2.order_date = do1.order_date
                            )
                        ) AS order_obj
                        FROM OrdersData do1
                        GROUP BY order_date
                    ) orders_agg
                ),
                'products', (
                    SELECT JSON_ARRAYAGG(
                        JSON_OBJECT(
                            'name', prod.name,
                            'percentage', prod.percentage
                        )
                    )
                    FROM (
                        SELECT i.name AS name,
                               (COUNT(*) * 100.0 / NULLIF(MAX(sub.total_count), 0)) AS percentage
                        FROM ProductPayments pp
                        JOIN Products p ON pp.product_id = p.product_id
                        JOIN Items i ON p.item_id = i.item_id
                        CROSS JOIN TotalCompleted sub
                        WHERE pp.status = 'completed' AND p.created_by = :creatorId
                        GROUP BY i.name
                    ) prod
                )
            ) AS dashboardData
            """, nativeQuery = true)
    String getDashboardDataByCreatorId(@Param("creatorId") Long creatorId);

}
