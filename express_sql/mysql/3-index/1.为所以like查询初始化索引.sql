ALTER TABLE `ygf_deal` ADD INDEX `ygf_deal_phone` (`phone`) USING BTREE ;
ALTER TABLE `ygf_item` ADD INDEX `ygf_item_name` (`name`) USING BTREE ;
ALTER TABLE `ygf_sec_user_card` ADD INDEX `ygf_sec_user_card_phone` (`c_user_phone`) USING BTREE ;
ALTER TABLE `ygf_pack` ADD INDEX `ygf_pack_name` (`name`) USING BTREE ;
ALTER TABLE `ygf_buy_info` ADD INDEX `ygf_buy_info_name` (`name`) USING BTREE ;
ALTER TABLE `ygf_store_amount` ADD INDEX `content` (`content`) USING BTREE ;
ALTER TABLE `ygf_user_login` ADD INDEX `ygf_user_login_phone` (`phone`) USING BTREE ;
