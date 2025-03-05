drop database if exists ufc;
create database if not exists ufc;
use ufc;

-- 1. 기본 테이블들 (FK 제약조건 없이)
CREATE TABLE PrivatePhotos (
    photo_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    origin_name VARCHAR(255) NOT NULL,
    convert_name VARCHAR(300) NOT NULL,
    uploaded_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_private_origin UNIQUE (origin_name),
    CONSTRAINT uk_private_convert UNIQUE (convert_name)
);

CREATE TABLE PublicPhotos (
    photo_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    origin_name VARCHAR(255) NOT NULL,
    convert_name VARCHAR(300) NOT NULL,
    uploaded_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_public_origin UNIQUE (origin_name),
    CONSTRAINT uk_public_convert UNIQUE (convert_name)
);

CREATE TABLE Notices (
    notice_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(50) NOT NULL,
    content VARCHAR(200) NOT NULL,
    noticed_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_notice_content UNIQUE (content)
);

CREATE TABLE Users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    oauth_id VARCHAR(100),
    login_type VARCHAR(50) NOT NULL,
    user_name VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15),
    user_address VARCHAR(200),
    roles VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    photo_id BIGINT,
    intro VARCHAR(200),
    is_marketed BIT DEFAULT 0,
    user_status BIT NOT NULL DEFAULT 1,
    status_reason VARCHAR(200),
    CONSTRAINT uk_user_oauth UNIQUE (oauth_id),
    CONSTRAINT uk_user_email UNIQUE (email),
    CONSTRAINT ck_user_login_type CHECK (login_type IN ('kakao', 'naver', 'google')),
    CONSTRAINT ck_user_roles CHECK (roles IN ('ROLE_USER', 'ROLE_ADMIN', 'ROLE_CREATOR'))
);

CREATE TABLE Creators (
    creator_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    intro VARCHAR(200),
    business_cert BIGINT,
    b_regist_number VARCHAR(30) NOT NULL,
    b_name VARCHAR(20) NOT NULL,
    company_name VARCHAR(50) NOT NULL,
    address VARCHAR(200) NOT NULL,
    back_img_url BIGINT,
    pro_img_url BIGINT,
    own_user BIGINT,
    creator_status BIT NOT NULL DEFAULT 0;
);

CREATE TABLE Badges (
    badge_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    photo_id BIGINT
);

CREATE TABLE Materials (
    material_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    photo_id BIGINT,
    CONSTRAINT uk_material_name UNIQUE (name)
);

CREATE TABLE Items (
    item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    photo_id BIGINT
);

CREATE TABLE Campaigns (
    campaign_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    is_success BIT NOT NULL DEFAULT 0,
    campaign_status BIT NOT NULL DEFAULT 0,
    photo_id BIGINT;
);

CREATE TABLE CampaignBoards (
    c_board_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content MEDIUMTEXT NOT NULL,
    created_date DATETIME NOT NULL,
    campaign_id BIGINT
);

CREATE TABLE CampaignBoardReply (
    c_b_reply_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(255) NOT NULL,
    created_date DATETIME NOT NULL,
    c_board_id BIGINT,
    replyed_by BIGINT
);

CREATE TABLE CampaignReviews (
    c_review_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(255) NOT NULL,
    created_date DATETIME NOT NULL,
    rated DECIMAL(3,1),
    reviewed_by BIGINT,
    campaigned_by BIGINT,
    CONSTRAINT ck_review_rate CHECK (rated BETWEEN 0 AND 5)
);

CREATE TABLE CampaignGoals (
    goal_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    campaign_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    quantity_required INT NOT NULL
);

CREATE TABLE MaterialsDonations (
    donation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    campaign_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(10),
    donated_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_donation_status CHECK (status IN ('approved', 'rejected', 'pending', 'processing'))
);

CREATE TABLE Rewards (
    reward_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    campaign_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL
);

CREATE TABLE RewardMaterials (
    re_mater_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reward_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    quantity_required INT NOT NULL
);

CREATE TABLE Products (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_id BIGINT NOT NULL,
    stock_quantity INT NOT NULL,
    created_by BIGINT NOT NULL
);

CREATE TABLE ProductPayments (
    pay_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT,
    purchased_by BIGINT,
    price INT NOT NULL,
    stock TINYINT NOT NULL,
    purchased_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    receipt_number VARCHAR(100) NOT NULL,
    status VARCHAR(10),
    is_adjust BIT NOT NULL DEFAULT 0,
    CONSTRAINT ck_payment_status CHECK (status IN ('pending', 'completed', 'cancelled'))
);

CREATE TABLE RewardDeliveries (
    r_delivery_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice VARCHAR(40) NOT NULL,
    status VARCHAR(100),
    campaign_id BIGINT NOT NULL,
    donation_id BIGINT,
    CONSTRAINT ck_reward_delivery_status CHECK (status IN ('preparing', 'shipping', 'completed', 'cancelled'))
);

CREATE TABLE ProductDeliveries (
    p_delivery_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice VARCHAR(40) NOT NULL,
    status VARCHAR(100),
    product_id BIGINT NOT NULL,
    pay_id BIGINT,
    CONSTRAINT ck_product_delivery_status CHECK (status IN ('preparing', 'shipping', 'completed', 'cancelled'))
);

CREATE TABLE Tags (
    tag_id INT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(20) NOT NULL,
    CONSTRAINT uk_tag_content UNIQUE (content)
);

CREATE TABLE CampaignTags (
    c_tag_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    campaign_id BIGINT NOT NULL,
    tag_id INT NOT NULL
);

CREATE TABLE ProductTags (
    p_tag_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tag_id INT NOT NULL,
    product_id BIGINT
);

CREATE TABLE Alerts (
    alert_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(255) NOT NULL,
    alert_type VARCHAR(30),
    alert_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_alert_type CHECK (alert_type IN ('campaign', 'user', 'delivery', 'payment', 'event', 'system'))
);

CREATE TABLE UserAlert (
    user_alert_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    is_read BOOLEAN DEFAULT FALSE
);

CREATE TABLE AlertTarget (
    alert_target_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    alert_id BIGINT,
    user_alert_id BIGINT,
    target_campaign BIGINT,
    target_product BIGINT,
    target_badge BIGINT,
    target_notice INT
);

CREATE TABLE UserBadges (
    user_badge_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    badge_id BIGINT
);

CREATE TABLE Likes (
    like_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    creator_id BIGINT,
    campaign_id BIGINT,
    product_id BIGINT
);

CREATE TABLE Reports (
    report_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    status VARCHAR(20),
    reason VARCHAR(150) NOT NULL,
    reported_by BIGINT,
    reported_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT,
    campaign_id BIGINT,
    product_id BIGINT,
    CONSTRAINT ck_report_status CHECK (status IN ('registed', 'ok', 'rejected');

-- Users 관련 FK
ALTER TABLE Users
    ADD CONSTRAINT fk_user_photo 
    FOREIGN KEY (photo_id) REFERENCES PrivatePhotos(photo_id);

-- Creators 관련 FK
ALTER TABLE Creators
    ADD CONSTRAINT fk_creator_business 
    FOREIGN KEY (business_cert) REFERENCES PrivatePhotos(photo_id),
    ADD CONSTRAINT fk_creator_back 
    FOREIGN KEY (back_img_url) REFERENCES PrivatePhotos(photo_id),
    ADD CONSTRAINT fk_creator_pro 
    FOREIGN KEY (pro_img_url) REFERENCES PrivatePhotos(photo_id),
    ADD CONSTRAINT fk_creator_user 
    FOREIGN KEY (own_user) REFERENCES Users(user_id);

-- Badges 관련 FK
ALTER TABLE Badges
    ADD CONSTRAINT fk_badge_photo 
    FOREIGN KEY (photo_id) REFERENCES PrivatePhotos(photo_id);

-- Materials 관련 FK
ALTER TABLE Materials
    ADD CONSTRAINT fk_material_photo 
    FOREIGN KEY (photo_id) REFERENCES PublicPhotos(photo_id);

-- Items 관련 FK
ALTER TABLE Items
    ADD CONSTRAINT fk_item_photo 
    FOREIGN KEY (photo_id) REFERENCES PublicPhotos(photo_id);

-- Campaigns 관련 FK
ALTER TABLE Campaigns
    ADD CONSTRAINT fk_campaign_creator 
    FOREIGN KEY (created_by) REFERENCES Creators(creator_id),
    ADD CONSTRAINT fk_campaign_photo 
    FOREIGN KEY (photo_id) REFERENCES PublicPhotos(photo_id);

-- CampaignBoards 관련 FK
ALTER TABLE CampaignBoards
    ADD CONSTRAINT fk_board_campaign 
    FOREIGN KEY (campaign_id) REFERENCES Campaigns(campaign_id);

-- CampaignBoardReply 관련 FK
ALTER TABLE CampaignBoardReply
    ADD CONSTRAINT fk_reply_board 
    FOREIGN KEY (c_board_id) REFERENCES CampaignBoards(c_board_id),
    ADD CONSTRAINT fk_reply_user 
    FOREIGN KEY (replyed_by) REFERENCES Users(user_id);

-- CampaignReviews 관련 FK
ALTER TABLE CampaignReviews
    ADD CONSTRAINT fk_review_user 
    FOREIGN KEY (reviewed_by) REFERENCES Users(user_id),
    ADD CONSTRAINT fk_review_campaign 
    FOREIGN KEY (campaigned_by) REFERENCES Campaigns(campaign_id);

-- CampaignGoals 관련 FK
ALTER TABLE CampaignGoals
    ADD CONSTRAINT fk_goal_campaign 
    FOREIGN KEY (campaign_id) REFERENCES Campaigns(campaign_id),
    ADD CONSTRAINT fk_goal_material 
    FOREIGN KEY (material_id) REFERENCES Materials(material_id);

-- MaterialsDonations 관련 FK
ALTER TABLE MaterialsDonations
    ADD CONSTRAINT fk_donation_campaign 
    FOREIGN KEY (campaign_id) REFERENCES Campaigns(campaign_id),
    ADD CONSTRAINT fk_donation_user 
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    ADD CONSTRAINT fk_donation_material 
    FOREIGN KEY (material_id) REFERENCES Materials(material_id);

-- Rewards 관련 FK
ALTER TABLE Rewards
    ADD CONSTRAINT fk_reward_campaign 
    FOREIGN KEY (campaign_id) REFERENCES Campaigns(campaign_id),
    ADD CONSTRAINT fk_reward_item 
    FOREIGN KEY (item_id) REFERENCES Items(item_id);

-- RewardMaterials 관련 FK
ALTER TABLE RewardMaterials
    ADD CONSTRAINT fk_reward_material_reward 
    FOREIGN KEY (reward_id) REFERENCES Rewards(reward_id),
    ADD CONSTRAINT fk_reward_material_material 
    FOREIGN KEY (material_id) REFERENCES Materials(material_id);

-- Products 관련 FK
ALTER TABLE Products
    ADD CONSTRAINT fk_product_item 
    FOREIGN KEY (item_id) REFERENCES Items(item_id),
    ADD CONSTRAINT fk_product_creator 
    FOREIGN KEY (created_by) REFERENCES Creators(creator_id);

-- ProductPayments 관련 FK
ALTER TABLE ProductPayments
    ADD CONSTRAINT fk_payment_product 
    FOREIGN KEY (product_id) REFERENCES Products(product_id),
    ADD CONSTRAINT fk_payment_user 
    FOREIGN KEY (purchased_by) REFERENCES Users(user_id);

-- RewardDeliveries 관련 FK
ALTER TABLE RewardDeliveries
    ADD CONSTRAINT fk_reward_delivery_campaign 
    FOREIGN KEY (campaign_id) REFERENCES Campaigns(campaign_id),
    ADD CONSTRAINT fk_reward_delivery_donation 
    FOREIGN KEY (donation_id) REFERENCES MaterialsDonations(donation_id);

-- ProductDeliveries 관련 FK
ALTER TABLE ProductDeliveries
    ADD CONSTRAINT fk_product_delivery_product 
    FOREIGN KEY (product_id) REFERENCES Products(product_id),
    ADD CONSTRAINT fk_product_delivery_payment 
    FOREIGN KEY (pay_id) REFERENCES ProductPayments(pay_id);

-- Tags 관련 FK
ALTER TABLE Tags
    ADD CONSTRAINT fk_tag_campaign 
    FOREIGN KEY (campaign_id) REFERENCES Campaigns(campaign_id),
    ADD CONSTRAINT fk_tag_product 
    FOREIGN KEY (product_id) REFERENCES Products(product_id);

-- CampaignTags 관련 FK
ALTER TABLE CampaignTags
    ADD CONSTRAINT fk_campaign_tag_campaign 
    FOREIGN KEY (campaign_id) REFERENCES Campaigns(campaign_id),
    ADD CONSTRAINT fk_campaign_tag_tag 
    FOREIGN KEY (tag_id) REFERENCES Tags(tag_id);

-- ProductTags 관련 FK
ALTER TABLE ProductTags
    ADD CONSTRAINT fk_product_tag_tag 
    FOREIGN KEY (tag_id) REFERENCES Tags(tag_id),
    ADD CONSTRAINT fk_product_tag_product 
    FOREIGN KEY (product_id) REFERENCES Products(product_id);

-- UserAlert 관련 FK
ALTER TABLE UserAlert
    ADD CONSTRAINT fk_user_alert_user 
    FOREIGN KEY (user_id) REFERENCES Users(user_id);

-- AlertTarget 관련 FK
ALTER TABLE AlertTarget
    ADD CONSTRAINT fk_alert_target_alert 
    FOREIGN KEY (alert_id) REFERENCES Alerts(alert_id),
    ADD CONSTRAINT fk_alert_target_user_alert 
    FOREIGN KEY (user_alert_id) REFERENCES UserAlert(user_alert_id),
    ADD CONSTRAINT fk_alert_target_campaign 
    FOREIGN KEY (target_campaign) REFERENCES Campaigns(campaign_id),
    ADD CONSTRAINT fk_alert_target_product 
    FOREIGN KEY (target_product) REFERENCES Products(product_id),
    ADD CONSTRAINT fk_alert_target_badge 
    FOREIGN KEY (target_badge) REFERENCES Badges(badge_id),
    ADD CONSTRAINT fk_alert_target_notice 
    FOREIGN KEY (target_notice) REFERENCES Notices(notice_id);

-- UserBadges 관련 FK
ALTER TABLE UserBadges
    ADD CONSTRAINT fk_user_badge_user 
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    ADD CONSTRAINT fk_user_badge_badge 
    FOREIGN KEY (badge_id) REFERENCES Badges(badge_id);

-- Likes 관련 FK
ALTER TABLE Likes
    ADD CONSTRAINT fk_like_user 
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    ADD CONSTRAINT fk_like_creator 
    FOREIGN KEY (creator_id) REFERENCES Creators(creator_id),
    ADD CONSTRAINT fk_like_campaign 
    FOREIGN KEY (campaign_id) REFERENCES Campaigns(campaign_id),
    ADD CONSTRAINT fk_like_product 
    FOREIGN KEY (product_id) REFERENCES Products(product_id);

-- Reports 관련 FK
ALTER TABLE Reports
    ADD CONSTRAINT fk_report_reporter 
    FOREIGN KEY (reported_by) REFERENCES Users(user_id),
    ADD CONSTRAINT fk_report_user 
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    ADD CONSTRAINT fk_report_campaign 
    FOREIGN KEY (campaign_id) REFERENCES Campaigns(campaign_id),
    ADD CONSTRAINT fk_report_product 
    FOREIGN KEY (product_id) REFERENCES Products(product_id);

DELIMITER //

-- AlertTarget 테이블의 참조 제약조건
CREATE TRIGGER trg_alert_target_check BEFORE INSERT ON AlertTarget
FOR EACH ROW
BEGIN
    DECLARE target_count INT;
    SET target_count = (NEW.target_campaign IS NOT NULL) + 
                      (NEW.target_product IS NOT NULL) + 
                      (NEW.target_badge IS NOT NULL) + 
                      (NEW.target_notice IS NOT NULL);
    IF target_count != 1 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'AlertTarget must reference exactly one target';
    END IF;
END //

-- AlertTarget 테이블의 UPDATE 트리거
CREATE TRIGGER trg_alert_target_check_update BEFORE UPDATE ON AlertTarget
FOR EACH ROW
BEGIN
    DECLARE target_count INT;
    SET target_count = (NEW.target_campaign IS NOT NULL) + 
                      (NEW.target_product IS NOT NULL) + 
                      (NEW.target_badge IS NOT NULL) + 
                      (NEW.target_notice IS NOT NULL);
    IF target_count != 1 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'AlertTarget must reference exactly one target';
    END IF;
END //

-- Likes 테이블의 참조 제약조건
CREATE TRIGGER trg_likes_check BEFORE INSERT ON Likes
FOR EACH ROW
BEGIN
    DECLARE target_count INT;
    SET target_count = (NEW.creator_id IS NOT NULL) + 
                      (NEW.campaign_id IS NOT NULL) + 
                      (NEW.product_id IS NOT NULL);
    IF target_count != 1 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Likes must reference exactly one target';
    END IF;
END //

-- Likes 테이블의 UPDATE 트리거
CREATE TRIGGER trg_likes_check_update BEFORE UPDATE ON Likes
FOR EACH ROW
BEGIN
    DECLARE target_count INT;
    SET target_count = (NEW.creator_id IS NOT NULL) + 
                      (NEW.campaign_id IS NOT NULL) + 
                      (NEW.product_id IS NOT NULL);
    IF target_count != 1 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Likes must reference exactly one target';
    END IF;
END //

-- Reports 테이블의 참조 제약조건
CREATE TRIGGER trg_reports_check BEFORE INSERT ON Reports
FOR EACH ROW
BEGIN
    DECLARE target_count INT;
    SET target_count = (NEW.user_id IS NOT NULL) + 
                      (NEW.campaign_id IS NOT NULL) + 
                      (NEW.product_id IS NOT NULL);
    IF target_count != 1 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Reports must reference exactly one target';
    END IF;
END //

-- Reports 테이블의 UPDATE 트리거
CREATE TRIGGER trg_reports_check_update BEFORE UPDATE ON Reports
FOR EACH ROW
BEGIN
    DECLARE target_count INT;
    SET target_count = (NEW.user_id IS NOT NULL) + 
                      (NEW.campaign_id IS NOT NULL) + 
                      (NEW.product_id IS NOT NULL);
    IF target_count != 1 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Reports must reference exactly one target';
    END IF;
END //

DELIMITER ;


-- view
CREATE OR REPLACE VIEW SearchView AS
SELECT
    c.campaign_id AS id,
    c.title AS name,
    'Campaign' AS type,
    c.created_by
FROM Campaigns c
UNION ALL
SELECT
    p.product_id AS id,
    i.name AS name,
    'Product' AS type,
    p.created_by
FROM Products p
         JOIN Items i ON p.item_id = i.item_id
UNION ALL
SELECT
    t.tag_id AS id,
    t.content AS name,
    'Tag' AS type,
    t.tag_id AS created_by
FROM Tags t;



