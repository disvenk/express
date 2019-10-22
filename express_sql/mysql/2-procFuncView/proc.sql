delimiter $$


-- 寄件订单号生成
drop procedure if exists generate_sup_order_number$$
CREATE  PROCEDURE `generate_send_order_number`(out result bigint)
begin
	start transaction;
        select keyvalue into result from sys_dict where keyname='SEND_ORDER_NO_SHI_SEQ' for update;
        update sys_dict set keyvalue = result+1 where keyname='SEND_ORDER_NO_SHI_SEQ';
    commit;

end$$

--批量修改收件订单为'已退回'
DELIMITER $$
DROP PROCEDURE IF EXISTS update_order_back$$
CREATE PROCEDURE update_order_back()
BEGIN
DECLARE row_id INT;-- 定义游标变量ID
DECLARE row_order_id INT;-- 定义订单id变量
DECLARE arrivedDate DATETIME;-- 到达时间字符串
DECLARE arrived DATETIME;-- 到达时间戳
DECLARE done INT;
-- 定义游标
DECLARE rs_cursor CURSOR FOR
SELECT id FROM exp_receiver_order;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;
OPEN rs_cursor;
cursor_loop:LOOP
FETCH rs_cursor INTO row_id; -- 取数据
IF done=1 THEN
LEAVE cursor_loop;
END IF;
-- 更新表
SELECT arrived_date INTO arrived FROM exp_receiver_order WHERE id=row_id;
IF UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(arrived)>=172800 THEN
UPDATE exp_receiver_order SET order_status=8 WHERE id=row_id and order_status=2;
END IF;
END LOOP cursor_loop;
CLOSE rs_cursor;
END$$



