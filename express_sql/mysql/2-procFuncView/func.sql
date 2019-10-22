-- /********************************************************************************************
-- * 项目名称: 系统函数库--系统内部及公用
-- * 创建日期: 2014-10-23 09:42:00
-- * 创建人员: wang.h
-- * 文件说明：本文件用于创建系统所需的函数库，用于视图、存储过程等。
-- *			所有函数都以 f 开头，后跟类型，例如：fs_getname，中 s表示系统函数
-- *********************************************************************************************
-- *							     系统—fs
-- *********************************************************************************************
-- *       fs_getmoduleintdatabyid         根据ID获取层数或下级模块数量
-- *       fs_getmoduleparentstrbyid       仅用于菜单
-- *       fs_getdepartmentintdatabyid			根据ID获取层数或下级部门数量
-- *       fs_getdepartmentparentstrbyid		根据ID获取父级部门编号字符串
-- *********************************************************************************************
-- *							     业务—fb
-- *       fb_getname		根据ID获取名称
-- **********************************************************************************************
--
-- **********************************************************************************************/
delimiter $$





-- -- 统计报表，统计门店下指定月份的充值卡收现总金额
-- drop function if exists get_recharge_order_actual_price$$
--
-- create function get_recharge_order_actual_price(storeId int(11) , dateStar int(11), dateEnd int(11))
-- returns double(11,2)
-- begin
-- declare price double(11,2);
--
-- 	SELECT IFNULL(SUM(recOrd.actual_price),0) INTO price FROM ygf_recharge_order recOrd
-- 	WHERE recOrd.`status` in (-11, 20)  AND (recOrd.isDestroy is null or  recOrd.isDestroy =0) And recOrd.store_id = storeId And recOrd.create_time BETWEEN dateStar And dateEnd;
--
-- return price;
-- end$$
--
-- -- select get_recharge_order_actual_price(68,1396281600,1491872744)
--

