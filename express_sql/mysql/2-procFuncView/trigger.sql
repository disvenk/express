/********************************************************************************************
* 项目名称: 触发器
* 创建日期: 2014-10-23 09:42:00
* 创建人员: wang.h
* 文件说明：本文件用于创建系统所需的触发器。
*			所有触发器都以 t 开头，后跟类型。
*********************************************************************************************
*							     系统—ts
*********************************************************************************************
*       ts_updateModuleCount         更新module的subCount数量
*********************************************************************************************
*							     业务—tb
**********************************************************************************************

**********************************************************************************************/


/********************************************************************************************
*  名称: ts_updateModuleCount
********************************************************************************************/
-- drop trigger if exists ts_updateModuleCount$$
-- create trigger ts_updateModuleCount after insert on s_module
-- for each row
-- begin
-- set
-- UPDATE s_module SET subCount=subCount+1 where parentId=new.parentId;
-- end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorRole$$ create trigger ts_updateGeneratorRole after insert on SYS_Role for each row
begin
IF new.createdDate is NULL THEN
  UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='SYS_Role';
END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorUser$$ create trigger ts_updateGeneratorUser after insert on SYS_User for each row
begin
IF new.createdDate is NULL THEN
 UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='SYS_User';
END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorBrand$$ create trigger ts_updateGeneratorBrand after insert on ygf_brand for each row
begin
IF new.createdDate is NULL THEN
UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_brand';
END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorBrandTemp$$ create trigger ts_updateGeneratorBrandTemp after insert on ygf_brand_temp for each row
begin
IF new.createdDate is NULL THEN
UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_brand_temp' ;
END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorDeal$$ create trigger ts_updateGeneratorDeal after insert on ygf_deal for each row
begin
IF new.createdDate is NULL THEN
 UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_deal';
END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorDealCash$$ create trigger ts_updateGeneratorDealCash after insert on ygf_deal_cash for each row
begin
IF new.createdDate is NULL THEN
UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_deal_cash';
END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorDealCashBeautician$$ create trigger ts_updateGeneratorDealCashBeautician after insert on ygf_deal_cash_beautician for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_deal_cash_beautician'; END IF;
 end$$

/**/
delimiter $$
 drop trigger if exists ts_updateGeneratorMember$$ create trigger ts_updateGeneratorMember after insert on ygf_member for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_member'; END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorPackOrder$$ create trigger ts_updateGeneratorPackOrder after insert on ygf_pack_order for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_pack_order'; END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorPackOrderPacks$$ create trigger ts_updateGeneratorPackOrderPacks after insert on ygf_pack_order_packs for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_pack_order_packs'; END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorPackOrderPacksItems$$ create trigger ts_updateGeneratorPackOrderPacksItems after insert on ygf_pack_order_packs_items for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_pack_order_packs_items'; END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorPayment$$ create trigger ts_updateGeneratorPayment after insert on ygf_payment for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_payment'; END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorRechargeOrder$$ create trigger ts_updateGeneratorRechargeOrder after insert on ygf_recharge_order for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_recharge_order'; END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorRechargeRecordLog$$ create trigger ts_updateGeneratorRechargeRecordLog after insert on ygf_recharge_record_log for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_recharge_record_log'; END IF;
 end$$

/**/
delimiter $$
 drop trigger if exists ts_updateGeneratorRechargeUserRecord$$ create trigger ts_updateGeneratorRechargeUserRecord after insert on ygf_recharge_user_record for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_recharge_user_record'; END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorSecUserCard$$ create trigger ts_updateGeneratorSecUserCard after insert on ygf_sec_user_card for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_sec_user_card'; END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorSecUserCardItem$$ create trigger ts_updateGeneratorSecUserCardItem after insert on ygf_sec_user_card_item for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_sec_user_card_item'; END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorStoreAmount$$ create trigger ts_updateGeneratorStoreAmount after insert on ygf_store_amount for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_store_amount'; END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorUploadFile$$ create trigger ts_updateGeneratorUploadFile after insert on ygf_upload_file for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_upload_file'; END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorUserLogin$$ create trigger ts_updateGeneratorUserLogin after insert on ygf_user_login for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_user_login'; END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorBuyInfo$$ create trigger ts_updateGeneratorBuyInfo after insert on ygf_buy_info for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_buy_info'; END IF;
 end$$

delimiter $$
 drop trigger if exists ts_updateGeneratorItem$$ create trigger ts_updateGeneratorItem after insert on ygf_item for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_item'; END IF;
 end$$

 delimiter $$
 drop trigger if exists ts_updateGeneratorPack$$ create trigger ts_updateGeneratorPack after insert on ygf_pack for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_pack'; END IF;
 end$$

 delimiter $$
 drop trigger if exists ts_updateGeneratorPackItem$$ create trigger ts_updateGeneratorPackItem after insert on ygf_pack_item for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_pack_item'; END IF;
 end$$


 delimiter $$
 drop trigger if exists ts_updateGeneratorRechargeItem$$ create trigger ts_updateGeneratorRechargeItem after insert on ygf_recharge_item for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_recharge_item'; END IF;
 end$$

 delimiter $$
 drop trigger if exists ts_updateGeneratorRechargeBeau$$ create trigger ts_updateGeneratorRechargeBeau after insert on ygf_rechargeorder_beautician for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_rechargeorder_beautician'; END IF;
 end$$

  delimiter $$
 drop trigger if exists ts_updateGeneratorPackBeau$$ create trigger ts_updateGeneratorPackBeau after insert on ygf_packorder_beautician for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_packorder_beautician'; END IF;
 end$$


  delimiter $$
 drop trigger if exists ts_updateGeneratorStore$$ create trigger ts_updateGeneratorStore after insert on ygf_store for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_store'; END IF;
 end$$

   delimiter $$
 drop trigger if exists ts_updateGeneratorBeautician$$ create trigger ts_updateGeneratorBeautician after insert on ygf_beautician for each row
begin IF new.createdDate is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_beautician'; END IF;
 end$$



   delimiter $$
 drop trigger if exists ts_updateGeneratorBeautician_schedule$$ create trigger ts_updateGeneratorBeautician_schedule after insert on ygf_beautician_schedule for each row
begin IF new.createdChannel is NULL THEN UPDATE SYS_Table_Generator  SET `value`=`value`+1 where `name`='ygf_beautician_schedule'; END IF;
 end$$













