set global log_bin_trust_function_creators=true;

/* 在mysql中并没有split函数，需要自己写：*/
delimiter $$

-- 1）获得按指定字符分割的字符串的个数：
drop function if exists fs_get_split_string_total$$

create function fs_get_split_string_total(f_string varchar(1000),f_delimiter varchar(100))
returns int(11)
begin
declare returnInt int(11);
declare delimiterLength int(11);

set delimiterLength=length(f_delimiter);
return 1+(length(f_string) - length(replace(f_string,f_delimiter,'')))/delimiterLength;
end$$
-- select fs_get_split_string_total('abc||def||gh','||');   -- 结果为 3

-- 2）得到第i个分割后的字符串(i从1开始)。
drop function if exists fs_get_split_string$$

create function fs_get_split_string(f_string varchar(1000),f_delimiter varchar(100),f_count int)
returns varchar(255) charset utf8
begin
declare preresult varchar(255) default '';
declare result varchar(255) default '';

set result = substring_index(f_string,f_delimiter,f_count);
if f_count>1 then 
	set preresult = concat(substring_index(f_string,f_delimiter,f_count-1),f_delimiter);
end if;
return replace(result,preresult,'');
-- select replace('abc|abc','ab','mn')
-- select reverse('abc')
-- select substring('ab|cd|ef',1,3);  -- 此方法索引从1开始的
-- select substring_index('abc|abc|','|',2) 返回字符串 str 中在第 count 个出现的分隔符 delim 之前的子串,count从1开始。中文英文字母都算一个。
-- select rtrim(ltrim('  abc  '))  去除左右空格
-- select trim('  abc  ')          去除左右空格 
-- select locate('ab', 'abcde');  -- 此函数是java 中的index类似。但找不到返回0，找到则大于0。这个函数即使有多个也只返回第一个找到的。
-- select character_length('苏州abcd');  -- 要用这个，不要用 length

-- select length('苏州abcd'); 
end$$
-- select fs_get_split_string('abc||def||gh','||',5);   -- 结果为 gh